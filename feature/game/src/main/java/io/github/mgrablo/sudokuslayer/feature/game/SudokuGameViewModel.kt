package io.github.mgrablo.sudokuslayer.feature.game

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import io.github.mgrablo.sudokucore.hints.Hint
import io.github.mgrablo.sudokucore.hints.HintType
import io.github.mgrablo.sudokucore.model.CellAttributes
import io.github.mgrablo.sudokucore.model.SudokuGrid
import io.github.mgrablo.sudokucore.model.clearAllCornerNotes
import io.github.mgrablo.sudokucore.model.fillNotes
import io.github.mgrablo.sudokucore.solver.ClassicSudokuSolver
import io.github.mgrablo.sudokuslayer.domain.core.Game
import io.github.mgrablo.sudokuslayer.domain.core.GameDifficulty
import io.github.mgrablo.sudokuslayer.domain.core.GameResult
import io.github.mgrablo.sudokuslayer.domain.core.HintLog
import io.github.mgrablo.sudokuslayer.domain.core.OperationRepository
import io.github.mgrablo.sudokuslayer.domain.core.SudokuGridSize
import io.github.mgrablo.sudokuslayer.domain.game.ElapsedTimerManager
import io.github.mgrablo.sudokuslayer.domain.game.GameManagementUseCases
import io.github.mgrablo.sudokuslayer.domain.game.HintUseCases
import io.github.mgrablo.sudokuslayer.domain.game.repositories.GameResultWriter
import io.github.mgrablo.sudokuslayer.domain.game.usecases.input.RecordUndoOperationUseCase
import io.github.mgrablo.sudokuslayer.domain.game.usecases.input.RedoOperationUseCase
import io.github.mgrablo.sudokuslayer.domain.game.usecases.input.UndoOperationUseCase
import io.github.mgrablo.sudokuslayer.domain.game.usecases.time.GetBestTimeUseCase
import io.github.mgrablo.sudokuslayer.domain.game.usecases.time.GetElapsedTimeUseCase
import io.github.mgrablo.sudokuslayer.domain.game.usecases.time.SaveElapsedTimeUseCase
import io.github.mgrablo.sudokuslayer.domain.settings.SettingsRepository
import io.github.mgrablo.sudokuslayer.feature.game.model.GameState
import io.github.mgrablo.sudokuslayer.feature.game.model.SnackbarState
import io.github.mgrablo.sudokuslayer.feature.game.model.SudokuGameUiState
import io.github.mgrablo.sudokuslayer.feature.game.util.AndroidHintStringProvider
import kotlinx.collections.immutable.PersistentList
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
import kotlinx.coroutines.flow.distinctUntilChangedBy
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

internal class SudokuGameViewModel(
	private val sudokuGridSize: SudokuGridSize,
	private val application: Application,
	private val settingsRepository: SettingsRepository,
	private val operationRepository: OperationRepository,
	private val gameManagementUseCases: GameManagementUseCases,
	private val hintUseCases: HintUseCases,
	private val undoOperationUseCase: UndoOperationUseCase,
	private val redoOperationUseCase: RedoOperationUseCase,
	private val recordUndoOperation: RecordUndoOperationUseCase,
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

	private val _uiState = MutableStateFlow(SudokuGameUiState())
	val uiState: StateFlow<SudokuGameUiState> = _uiState
	private var hintFocusJob: Job? = null

	val game: StateFlow<Game?> = gameManagementUseCases.getGame().stateIn(
		scope = viewModelScope,
		started = SharingStarted.WhileSubscribed(5000L),
		initialValue = null,
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
		if (showRemainingDigitCounts && game != null) {
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
		_uiState.update {
			it.copy(
				sudokuGridSize = this.sudokuGridSize,
			)
		}
		observeSettings()
		observeGameState()
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

		data object FindMistakes : Event

		data object ShowMistakes : Event

		data object DismissSnackbar : Event

		data object HintFillNotes : Event

		data object ResetGame : Event

		data object ResetNotes : Event

		data object StopTimer : Event

		data object StartTimer : Event
	}

	fun onEvent(event: Event) {
		when (event) {
			is Event.SelectCell -> viewModelScope.launch { selectCell(event.row, event.column) }

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

			is Event.FindMistakes -> handleFindMistakes()

			is Event.ShowMistakes -> handleShowMistakes()

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

			is Event.DismissSnackbar -> {
				_uiState.update {
					it.copy(
						snackbarState = null,
					)
				}
			}
		}
	}

	private suspend fun saveGame() {
		game.value?.let { currentGame ->
			gameManagementUseCases.saveGame(
				currentGame.copy(
					elapsedTime = elapsedTime.value,
				),
			)
		}
	}

	private suspend inline fun updateGame(transform: (Game) -> Game) {
		updateGameMutex.withLock {
			game.value?.let { currentGame ->
				gameManagementUseCases.saveGame(transform(currentGame))
			}
		}
	}

	private fun observeGameState() {
		viewModelScope.launch {
			game.filterNotNull().distinctUntilChangedBy(Game::toStateKey)
				.collect { g ->
					delay(1100)
					val bestTime = getBestTimeUseCase(
						g.difficulty,
						SudokuGridSize.fromIntSize(g.grid.gridSize),
					)
					val lastHint = g.hintLogs.lastOrNull()?.takeIf { !it.isRevealed }?.hint

					_uiState.update { state ->
						val isNewBest = if (g.completed) {
							bestTime == null || bestTime >= g.elapsedTime
						} else {
							state.isNewBestTime
						}

						state.copy(
							gameState = if (g.completed) GameState.VICTORY else GameState.PLAYING,
							currentBestTime = bestTime,
							isNewBestTime = isNewBest,
							lastHint = lastHint,
						)
					}

					if (g.completed) {
						elapsedTimerManager.stopTracking()
					} else {
						elapsedTimerManager.startTracking()
					}
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

	private suspend fun selectCell(row: Int, col: Int) {
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

	private fun inputNumber(
		number: Int,
		selectedCell: Pair<Int, Int>?,
		isNote: Boolean,
		isHint: Boolean = false,
	) {
		viewModelScope.launch {
			if (uiState.value.gameState == GameState.VICTORY) return@launch
			val (row, col) = selectedCell ?: return@launch
			updateGame { currentGame ->
				val (updatedGrid, changes) = gameManagementUseCases.calculateGridChanges(
					initialGrid = currentGame.grid,
					row = row,
					column = col,
					number = number,
					isNote = isNote,
					isHint = isHint,
				)
				recordUndoOperation(changes)
				val updatedLogs = handleHintGuess(row, col, number)
				currentGame.copy(
					grid = updatedGrid,
					hintLogs = updatedLogs.toPersistentList(),
				)
			}
			handleAllCellsFilled()
		}
	}

	private fun handleHintGuess(row: Int, column: Int, number: Int): PersistentList<HintLog> {
		val lastHint = _uiState.value.lastHint
		val isCorrectGuess =
			lastHint != null && lastHint.row == row && lastHint.col == column && lastHint.value == number

		val hintLogs = game.value?.hintLogs
		if (!isCorrectGuess) {
			return hintLogs ?: persistentListOf()
		}

		val updatedLogs = hintLogs?.toMutableList() ?: mutableListOf()
		val hintLogIndex =
			updatedLogs.indexOfLast {
				it.hint.row == lastHint.row &&
					it.hint.col == lastHint.col &&
					it.hint.value == lastHint.value &&
					it.hint.type == lastHint.type
			}

		if (hintLogIndex != -1) {
			val log = updatedLogs[hintLogIndex]
			updatedLogs[hintLogIndex] = log.copy(isUserGuessed = true, isRevealed = true)
		}

		return updatedLogs.toPersistentList()
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

	@OptIn(ExperimentalTime::class)
	private fun handleAllCellsFilled() {
		viewModelScope.launch {
			game.value?.let { currentGame ->
				if (currentGame.grid.getEmptyCellsCount() != 0) {
					return@launch
				}
				val result = ClassicSudokuSolver.isValidSolution(currentGame.grid)
				if (result) {
					_uiState.update {
						it.copy(
							gameState = GameState.VICTORY,
							isNewBestTime = it.currentBestTime == null || it.currentBestTime >= elapsedTime.value,
							focusedCells = persistentSetOf(),
						)
					}
					elapsedTimerManager.stopTracking()
					val gridSize = SudokuGridSize.fromIntSize(currentGame.grid.gridSize)
					gameResultWriter.saveGameResult(
						GameResult(
							timeInSeconds = elapsedTime.value,
							difficulty = currentGame.difficulty,
							gridSize = gridSize,
							hintsUsed = currentGame.hintsUsed,
							completionDate = Clock.System.now().toLocalDateTime(
								TimeZone.currentSystemDefault(),
							),
							seed = currentGame.grid.seed,
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
			game.value?.let { g ->
				g.hintLogs.lastOrNull()?.isRevealed?.let { if (!it) return@launch }
				val hint: Hint? = hintUseCases.provideHint(g)

				updateGame { currentGame ->
					if (hint != null) {
						val grid = gameManagementUseCases.selectCell(
							sudoku = currentGame.grid,
							selectedCell = hint.row to hint.col,
						)
						hintFocus(hint)
						val hintLog =
							hintUseCases.generateLog(
								id = currentGame.hintLogs.size,
								hint = hint,
								grid = grid,
								stringProvider = stringProvider,
							)

						_uiState.update {
							it.copy(
								lastHint = hint,
							)
						}

						currentGame.copy(
							grid = grid,
							hintLogs = currentGame.hintLogs + hintLog,
							hintsUsed = currentGame.hintsUsed + 1,
						)
					} else {
						currentGame
					}
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
			if (game.value?.grid
					?.getCellAt(hint.row, hint.col)
					?.number != 0
			) {
				return@launch
			}
			selectCell(hint.row, hint.col)
			updateGame { currentGame ->
				val (updatedGrid, changes) = hintUseCases.revealOnGrid(hint, currentGame.grid)
				val updatedLogs = hintUseCases.revealLastLog(currentGame.hintLogs)

				recordUndoOperation(changes)
				currentGame.copy(
					grid = updatedGrid,
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

			val cell = game.value?.grid?.getCellAt(row, column) ?: return@launch
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

	private fun handleFindMistakes() {
		viewModelScope.launch {
			game.value?.let { currentGame ->
				val mistakes = gameManagementUseCases.findMistakes(currentGame)
				_uiState.update {
					it.copy(
						foundMistakes = mistakes.toPersistentList(),
						snackbarState = if (mistakes.isNotEmpty()) {
							SnackbarState.FoundMistakes(mistakes.size)
						} else {
							SnackbarState.NoMistakesFound
						},
					)
				}
			}
		}
	}

	private fun handleShowMistakes() {
		viewModelScope.launch {
			val mistakesToShow = _uiState.value.foundMistakes
			if (mistakesToShow.isEmpty()) return@launch
			updateGame { currentGame ->
				var updatedGrid = currentGame.grid
				for ((row, col) in mistakesToShow) {
					updatedGrid = updatedGrid.updateCell(row, col) {
						it.copy(
							attributes = it.attributes + CellAttributes.SOLUTION_CONFLICT,
						)
					}
				}
				currentGame.copy(grid = updatedGrid)
			}
			_uiState.update {
				it.copy(
					foundMistakes = persistentListOf(),
					snackbarState = null,
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

private data class GameStateKey(
	val completed: Boolean,
	val difficulty: GameDifficulty,
	val gridSize: Int,
	val seed: Long?,
)

private fun Game.toStateKey() = GameStateKey(
	completed,
	difficulty,
	grid.gridSize,
	grid.seed,
)
