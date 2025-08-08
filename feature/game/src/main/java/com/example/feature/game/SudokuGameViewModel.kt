package com.example.feature.game

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.domain.core.CellChange
import com.example.domain.core.Game
import com.example.domain.core.GameDifficulty
import com.example.domain.core.GameResult
import com.example.domain.core.Operation
import com.example.domain.core.OperationRepository
import com.example.domain.core.SudokuGridSize
import com.example.domain.game.ElapsedTimerManager
import com.example.domain.game.GameManagementUseCases
import com.example.domain.game.HintUseCases
import com.example.domain.game.repositories.GameResultWriter
import com.example.domain.game.usecases.input.RedoOperationUseCase
import com.example.domain.game.usecases.input.UndoOperationUseCase
import com.example.domain.game.usecases.time.GetBestTimeUseCase
import com.example.domain.game.usecases.time.GetElapsedTimeUseCase
import com.example.domain.game.usecases.time.SaveElapsedTimeUseCase
import com.example.domain.settings.SettingsRepository
import com.example.feature.game.model.GameState
import com.example.feature.game.model.SudokuGameUiState
import com.example.feature.game.util.AndroidHintStringProvider
import com.example.sudoku.model.SudokuGrid
import com.example.sudoku.model.clearAllCornerNotes
import com.example.sudoku.model.fillNotes
import com.example.sudoku.solver.ClassicSudokuSolver
import com.example.sudoku.solver.Hint
import com.example.sudoku.solver.HintType
import kotlinx.collections.immutable.PersistentMap
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.persistentMapOf
import kotlinx.collections.immutable.persistentSetOf
import kotlinx.collections.immutable.plus
import kotlinx.collections.immutable.toPersistentList
import kotlinx.collections.immutable.toPersistentMap
import kotlinx.collections.immutable.toPersistentSet
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.datetime.Clock.System
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

internal class SudokuGameViewModel(
	private val application: Application,
	private val settingsRepository: SettingsRepository,
	private val operationRepository: OperationRepository,
	private val gameManagementUseCases: GameManagementUseCases,
	private val hintUseCases: HintUseCases,
	private val undoOperationUseCase: UndoOperationUseCase,
	private val redoOperationUseCase: RedoOperationUseCase,
	private val getBestTimeUseCase: GetBestTimeUseCase,
	private val gameResultWriter: GameResultWriter,
	private val getElapsedTimeUseCase: GetElapsedTimeUseCase,
	private val saveElapsedTimeUseCase: SaveElapsedTimeUseCase,
) : AndroidViewModel(application) {
	private val elapsedTimerManager = ElapsedTimerManager(
		scope = viewModelScope,
		getElapsedTimeUseCase = getElapsedTimeUseCase,
		saveElapsedTimeUseCase = saveElapsedTimeUseCase,
	)
	private val updateGameMutex = Mutex()

	private val _uiState = MutableStateFlow<SudokuGameUiState>(SudokuGameUiState())
	val uiState: StateFlow<SudokuGameUiState> = _uiState
	private var hintFocusJob: Job? = null

	val game: StateFlow<Game> = gameManagementUseCases.getGame().stateIn(
		scope = viewModelScope,
		started = SharingStarted.WhileSubscribed(5000L),
		initialValue = Game(
			grid = SudokuGrid(),
			difficulty = GameDifficulty.Easy,
			elapsedTime = 0L,
			hintsUsed = 0,
			hintLogs = persistentListOf(),
			completed = false,
		),
	)
	val elapsedTime =
		elapsedTimerManager.elapsedTime.stateIn(
			scope = viewModelScope,
			started = SharingStarted.WhileSubscribed(5000L),
			initialValue = 0L,
		)
	val remainingDigitCounts: StateFlow<PersistentMap<Int, Int>> = combine(
		game,
		settingsRepository.remainingDigitCounts,
	) { game, showRemainingDigitCounts ->
		if (showRemainingDigitCounts) {
			calculateRemainingCounts(game.grid).toPersistentMap()
		} else {
			persistentMapOf()
		}
	}.stateIn(
		scope = viewModelScope,
		started = SharingStarted.WhileSubscribed(5000L),
		initialValue = persistentMapOf(),
	)

	private val stringProvider by lazy {
		AndroidHintStringProvider(application)
	}

	init {
		observeSettings()
		viewModelScope.launch {
			_uiState.update {
				it.copy(
					gameState = GameState.LOADING,
				)
			}
			loadGame()
			val loadedGame = game.first()

			_uiState.update {
				it.copy(
					gameState = if (loadedGame.completed) GameState.VICTORY else GameState.PLAYING,
					isNewBestTime = if (loadedGame.completed) {
						it.currentBestTime == null || it.currentBestTime >= loadedGame.elapsedTime
					} else {
						it.isNewBestTime
					},
				)
			}
			if (_uiState.value.gameState == GameState.PLAYING) {
				elapsedTimerManager.startTracking()
			}
		}
	}

	override fun onCleared() {
		super.onCleared()
		runBlocking {
			saveGame()
		}
	}

	sealed interface Event {
		data class SelectCell(val row: Int, val column: Int) : Event

		data class CellLongClick(val row: Int, val column: Int) : Event

		data class InputNumber(val number: Int) : Event

		data class LongInputNumber(val number: Int) : Event

		data class SwitchInputMode(val isNote: Boolean) : Event

		data object ClearCell : Event

		data object Undo : Event

		data object Redo : Event

		data object ProvideHint : Event

		data object ExplainHint : Event

		data class HighlightHintCells(val hint: Hint) : Event

		data object ShowMistakes : Event

		data object HintFillNotes : Event

		data object ResetGame : Event

		data object ResetNotes : Event

		data object StopTimer : Event

		data object StartTimer : Event
	}

	fun onEvent(event: Event) {
		when (event) {
			is Event.SelectCell -> selectCell(event.row, event.column)
			is Event.CellLongClick -> handleCellLongClick(event.row, event.column)
			is Event.InputNumber ->
				inputNumber(
					number = event.number,
					selectedCell = _uiState.value.selectedCell,
					isNote = _uiState.value.isInNoteMode,
				)

			is Event.LongInputNumber -> inputNumber(
				number = event.number,
				selectedCell = _uiState.value.selectedCell,
				isNote = !uiState.value.isInNoteMode,
			)

			is Event.ClearCell ->
				inputNumber(
					number = 0,
					selectedCell = _uiState.value.selectedCell,
					isNote = _uiState.value.isInNoteMode,
				)

			is Event.Undo -> undoLastMove()
			is Event.Redo -> redoLastMove()
			is Event.ResetGame -> resetGame()
			is Event.ProvideHint -> provideHint()
			is Event.HighlightHintCells -> hintFocus(event.hint)

			is Event.ExplainHint -> revealHint()

			is Event.HintFillNotes -> fillNotes()
			is Event.ShowMistakes -> {}
			is Event.SwitchInputMode -> switchInputMode(event.isNote)
			is Event.ResetNotes -> resetNotes()
			is Event.StopTimer -> {
				viewModelScope.launch {
					elapsedTimerManager.stopTracking()
				}
			}

			is Event.StartTimer -> {
				if (uiState.value.gameState == GameState.PLAYING) {
					elapsedTimerManager.startTracking()
				}
			}
		}
	}

	private suspend fun saveGame() {
		gameManagementUseCases.saveGame(
			game.value.copy(
				elapsedTime = elapsedTime.value,
			),
		)
	}

	private suspend inline fun updateGame(transform: (Game) -> Game) {
		updateGameMutex.withLock {
			gameManagementUseCases.saveGame(transform(game.value))
		}
	}

	private suspend fun loadGame() {
		game.value.hintLogs.lastOrNull()?.let { log ->
			if (!log.isRevealed) {
				_uiState.update {
					it.copy(
						lastHint = log.hint,
					)
				}
			}
		}
		getBestTimeUseCase(
			game.value.difficulty,
			SudokuGridSize.fromIntSize(
				game.value.grid.gridSize,
			),
		)?.let { bestTime ->
			_uiState.update {
				it.copy(
					currentBestTime = bestTime,
				)
			}
		}
	}

	private fun observeSettings() {
		viewModelScope.launch {
			combine(
				settingsRepository.leftHandMode,
				settingsRepository.showActionButtonsOnTop,
				settingsRepository.autoClearNotes,
				settingsRepository.timerVisibility,
			) { leftHandMode, showActionButtonsOnTop, autoClearNotes, timerVisibility ->
				_uiState.update {
					it.copy(
						isLeftHandMode = leftHandMode,
						showActionButtonsOnTop = showActionButtonsOnTop,
						autoClearNotes = autoClearNotes,
						timerVisible = timerVisibility,
					)
				}
			}.collect()
		}
	}

	private fun selectCell(row: Int, col: Int) {
		viewModelScope.launch {
			updateGame {
				it.copy(
					grid = gameManagementUseCases.selectCell(
						sudoku = it.grid,
						selectedCell = row to col,
					),
				)
			}
			_uiState.update {
				it.copy(
					selectedCell = row to col,
				)
			}
		}
	}

	private fun inputNumber(
		number: Int,
		selectedCell: Pair<Int, Int>?,
		isNote: Boolean,
		isHint: Boolean = false,
	) {
		viewModelScope.launch {
			if (uiState.value.gameState == GameState.VICTORY) return@launch
			var updatedSudoku = game.value.grid
			val changes = mutableListOf<CellChange>()
			val (row, col) = selectedCell ?: return@launch

			val backupCell = updatedSudoku.getCellAt(row, col)
			updatedSudoku =
				gameManagementUseCases.inputNumber(
					sudokuGrid = updatedSudoku,
					number = number,
					row = row,
					column = col,
					isNote = isNote,
					isHint = isHint,
				)
			if (!isNote && !isHint) {
				updatedSudoku = gameManagementUseCases.highlightMatching(
					sudokuGrid = updatedSudoku,
					number = number,
				)
			}
			changes.add(
				CellChange(
					oldCell = backupCell,
					newCell = updatedSudoku.getCellAt(row, col),
				),
			)
			if (uiState.value.autoClearNotes && !isNote) {
				val (newSudoku, noteChanges) = gameManagementUseCases.autoClearNotes(
					sudokuGrid = updatedSudoku,
					row = row,
					column = col,
					number = number,
				)
				updatedSudoku = newSudoku
				changes.addAll(noteChanges)
			}
			operationRepository.apply {
				addUndoOperation(
					Operation(
						id = operationRepository.getUndoOperations().size.toLong(),
						changes = changes,
					),
				)
				clearRedoOperations()
			}
			val updatedLogs = game.value.hintLogs.toMutableList()
			if (uiState.value.lastHint?.row == row &&
				uiState.value.lastHint?.col == col &&
				number == uiState.value.lastHint?.value
			) {
				val lastHintId = updatedLogs.indexOfLast { it.hint == _uiState.value.lastHint }
				val log = updatedLogs[lastHintId]
				updatedLogs[lastHintId] =
					log.copy(
						isUserGuessed = true,
					)
				revealHint()
			}
			updateGame {
				it.copy(
					grid = updatedSudoku,
					hintLogs = updatedLogs.toPersistentList(),
				)
			}
			handleAllCellsFilled()
		}
	}

	private fun resetGame() {
		viewModelScope.launch {
			if (_uiState.value.gameState == GameState.VICTORY) return@launch
			updateGame { gameManagementUseCases.resetGame(it) }
			elapsedTimerManager.resetTimer()
			_uiState.update {
				it.copy(
					lastHint = null,
					selectedCell = null,
				)
			}
		}
	}

	private fun resetNotes() {
		viewModelScope.launch {
			if (_uiState.value.gameState == GameState.VICTORY) return@launch
			updateGame { it.copy(grid = it.grid.clearAllCornerNotes()) }
		}
	}

	private fun handleAllCellsFilled() {
		viewModelScope.launch {
			if (game.value.grid.getEmptyCellsCount() != 0) {
				return@launch
			}
			val result = ClassicSudokuSolver.isValidSolution(game.value.grid)
			if (result) {
				_uiState.update {
					it.copy(
						gameState = GameState.VICTORY,
						isNewBestTime = it.currentBestTime == null || it.currentBestTime >= elapsedTime.value,
					)
				}
				elapsedTimerManager.stopTracking()
				val gridSize = SudokuGridSize.fromIntSize(game.value.grid.gridSize)
				gameResultWriter.saveGameResult(
					GameResult(
						timeInSeconds = elapsedTime.value,
						difficulty = game.value.difficulty,
						gridSize = gridSize,
						hintsUsed = game.value.hintsUsed,
						completionDate = System.now().toLocalDateTime(
							TimeZone.currentSystemDefault(),
						),
						seed = game.value.grid.seed,
					),
				)
				updateGame {
					it.copy(
						completed = true,
						elapsedTime = elapsedTime.value,
					)
				}
				operationRepository.clearOperations()
			}
		}
	}

	private fun switchInputMode(isInNoteMode: Boolean) {
		_uiState.update {
			it.copy(
				isInNoteMode = isInNoteMode,
			)
		}
	}

	private fun undoLastMove() {
		viewModelScope.launch {
			if (_uiState.value.gameState == GameState.VICTORY) return@launch
			if (operationRepository.getUndoOperations().isEmpty()) return@launch
			updateGame { it.copy(grid = undoOperationUseCase(it.grid)) }
		}
	}

	private fun redoLastMove() {
		viewModelScope.launch {
			if (_uiState.value.gameState == GameState.VICTORY) return@launch
			if (operationRepository.getRedoOperations().isEmpty()) return@launch
			updateGame { it.copy(grid = redoOperationUseCase(it.grid)) }
		}
	}

	private fun fillNotes() {
		viewModelScope.launch {
			if (_uiState.value.gameState == GameState.VICTORY) return@launch
			updateGame { it.copy(grid = it.grid.fillNotes()) }
		}
	}

	private fun provideHint() {
		viewModelScope.launch {
			if (_uiState.value.gameState == GameState.VICTORY) return@launch
			game.value.hintLogs.lastOrNull()?.isRevealed?.let { if (!it) return@launch }

			val hint: Hint? = hintUseCases.provideHint(game.value)
			if (hint != null) {
				val grid = game.value.grid
				selectCell(hint.row, hint.col)
				hintFocus(hint)
				val hintLog =
					hintUseCases.generateLog(
						id = game.value.hintLogs.size,
						hint = hint,
						grid = grid,
						stringProvider = stringProvider,
					)
				updateGame {
					it.copy(
						hintLogs = it.hintLogs + hintLog,
						hintsUsed = it.hintsUsed + 1,
					)
				}
				_uiState.update {
					it.copy(
						lastHint = hint,
					)
				}
			}
		}
	}

	private fun hintFocus(hint: Hint) {
		hintFocusJob?.cancel()
		hintFocusJob = viewModelScope.launch {
			if (_uiState.value.gameState == GameState.VICTORY) return@launch

			val cellsToFocus = when (hint.type) {
				is HintType.HiddenSingle, is HintType.NakedSingle -> {
					listOf(Pair(hint.row, hint.col))
				}

				is HintType.ClaimingCandidate, is HintType.PointingCandidate -> {
					hint.enforcingCells.map { it.row to it.col }
				}
			}

			if (cellsToFocus.isEmpty()) return@launch

			_uiState.update { state ->
				state.copy(
					focusedCells = cellsToFocus.toPersistentSet(),
				)
			}

			delay(3000)

			_uiState.update { state ->
				state.copy(
					focusedCells = persistentSetOf(),
				)
			}
		}
	}

	private fun revealHint() {
		viewModelScope.launch {
			if (_uiState.value.gameState == GameState.VICTORY) return@launch
			val hint: Hint = _uiState.value.lastHint ?: return@launch
			if (game.value.grid
					.getCellAt(hint.row, hint.col)
					.number != 0
			) {
				return@launch
			}
			selectCell(hint.row, hint.col)

			val updatedSudoku = hintUseCases.revealOnGrid(hint, game.value.grid)
			val updatedLogs = hintUseCases.revealLastLog(game.value.hintLogs)

			updateGame {
				it.copy(
					grid = updatedSudoku,
					hintLogs = updatedLogs,
				)
			}
			_uiState.update {
				it.copy(lastHint = null)
			}
			handleAllCellsFilled()
		}
	}

	private fun handleCellLongClick(row: Int, column: Int) {
		viewModelScope.launch {
			if (_uiState.value.gameState == GameState.VICTORY) return@launch

			val cell = game.value.grid.getCellAt(row, column)
			if (cell.cornerNotes.size == 1) {
				selectCell(row, column)
				inputNumber(
					number = cell.cornerNotes.first(),
					selectedCell = row to column,
					isNote = false,
					isHint = false,
				)
			}
		}
	}
}

private fun calculateRemainingCounts(sudokuGrid: SudokuGrid): Map<Int, Int> {
	val remainingDigitCounts = mutableMapOf<Int, Int>()
	val max = sudokuGrid.gridSize
	for (i in 1..max) {
		remainingDigitCounts[i] = max - sudokuGrid.data.count { it.number == i }
	}
	return remainingDigitCounts
}
