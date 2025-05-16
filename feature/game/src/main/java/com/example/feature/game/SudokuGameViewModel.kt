package com.example.feature.game

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.domain.core.Game
import com.example.domain.core.GameDifficulty
import com.example.domain.core.GameResult
import com.example.domain.core.SudokuGridSize
import com.example.domain.game.ElapsedTimerManager
import com.example.domain.game.repositories.GameResultWriter
import com.example.domain.game.repositories.Operation
import com.example.domain.game.repositories.OperationRepository
import com.example.domain.game.usecases.FocusOnHintCellsUseCase
import com.example.domain.game.usecases.GenerateHintLogUseCase
import com.example.domain.game.usecases.GetGameUseCase
import com.example.domain.game.usecases.InputNumberUseCase
import com.example.domain.game.usecases.ProvideHintUseCase
import com.example.domain.game.usecases.RedoOperationUseCase
import com.example.domain.game.usecases.ResetGameUseCase
import com.example.domain.game.usecases.RevealHintOnGridUseCase
import com.example.domain.game.usecases.RevealLastHintLogUseCase
import com.example.domain.game.usecases.SaveGameUseCase
import com.example.domain.game.usecases.SelectCellUseCase
import com.example.domain.game.usecases.UndoOperationUseCase
import com.example.domain.settings.SettingsRepository
import com.example.feature.game.model.GameState
import com.example.feature.game.model.SudokuGameUiState
import com.example.sudoku.model.CellAttributes
import com.example.sudoku.model.SudokuGrid
import com.example.sudoku.model.clearAllCornerNotes
import com.example.sudoku.model.fillNotes
import com.example.sudoku.solver.ClassicSudokuSolver
import com.example.sudoku.solver.Hint
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.plus
import kotlinx.collections.immutable.toPersistentList
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.datetime.Clock.System
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

internal class SudokuGameViewModel(
	private val settingsRepository: SettingsRepository,
	private val operationRepository: OperationRepository,
	private val getGameUseCase: GetGameUseCase,
	private val saveGameUseCase: SaveGameUseCase,
	private val selectCellUseCase: SelectCellUseCase,
	private val inputNumberUseCase: InputNumberUseCase,
	private val provideHintUseCase: ProvideHintUseCase,
	private val focusOnHintCellsUseCase: FocusOnHintCellsUseCase,
	private val generateHintLogUseCase: GenerateHintLogUseCase,
	private val revealHintOnGridUseCase: RevealHintOnGridUseCase,
	private val revealLastHintLogUseCase: RevealLastHintLogUseCase,
	private val undoOperationUseCase: UndoOperationUseCase,
	private val redoOperationUseCase: RedoOperationUseCase,
	private val resetGameUseCase: ResetGameUseCase,
	private val elapsedTimerManager: ElapsedTimerManager,
	private val gameResultWriter: GameResultWriter,
) : ViewModel() {
	private val mutex = Mutex()
	private val _uiState = MutableStateFlow<SudokuGameUiState>(SudokuGameUiState())
	val uiState: StateFlow<SudokuGameUiState> = _uiState

	private val _game =
		MutableStateFlow<Game>(
			Game(
				grid = SudokuGrid(),
				difficulty = GameDifficulty.Easy,
				elapsedTime = 0L,
				hintsUsed = 0,
				hintLogs = persistentListOf(),
			),
		)
	val game: StateFlow<Game> = _game
	val elapsedTime =
		elapsedTimerManager.elapsedTime.stateIn(
			scope = viewModelScope,
			started = SharingStarted.WhileSubscribed(5000L),
			initialValue = 0L,
		)

	init {
		viewModelScope.launch {
			_uiState.update {
				it.copy(
					gameState = GameState.LOADING,
				)
			}
			loadData()
			_uiState.update {
				it.copy(
					gameState = GameState.PLAYING,
				)
			}
			elapsedTimerManager.startTracking()
		}
		viewModelScope.launch {
			game.collect { game ->
				saveGameUseCase(
					game.copy(elapsedTime = elapsedTime.firstOrNull() ?: 0L),
				)
			}
		}
	}

	override fun onCleared() {
		stopTrackingTime()
	}

	sealed interface Event {
		data class SelectCell(val row: Int, val col: Int) : Event

		data class InputNumber(val number: Int) : Event

		data object SwitchInputMode : Event

		data object ClearCell : Event

		data object Undo : Event

		data object Redo : Event

		data object ProvideHint : Event

		data object ExplainHint : Event

		data object ShowMistakes : Event

		data object HintFillNotes : Event

		data object ResetGame : Event

		data object ResetNotes : Event

		data object DismissVictoryDialog : Event

		data object StopTimer : Event
	}

	fun onEvent(event: Event) {
		when (event) {
			is Event.SelectCell -> selectCell(event.row, event.col)
			is Event.InputNumber ->
				inputNumber(
					number = event.number,
					selectedCell = _uiState.value.selectedCell,
					noteMode = _uiState.value.isInNoteMode,
				)

			is Event.ClearCell ->
				inputNumber(
					number = 0,
					selectedCell = _uiState.value.selectedCell,
					noteMode = _uiState.value.isInNoteMode,
				)

			is Event.Undo -> undoLastMove()
			is Event.Redo -> redoLastMove()
			is Event.ResetGame -> resetGame()
			is Event.ProvideHint -> provideHint()

			is Event.ExplainHint -> revealHint()

			is Event.HintFillNotes -> fillNotes()
			is Event.ShowMistakes -> {}
			is Event.DismissVictoryDialog -> handleDismissVictoryDialog()
			is Event.SwitchInputMode -> switchInputMode()
			is Event.ResetNotes -> resetNotes()
			is Event.StopTimer -> {
				stopTrackingTime()
			}
		}
	}

	private suspend fun loadData() {
		getGameUseCase().firstOrNull()?.let { game ->
			viewModelScope.launch(Dispatchers.IO) {
				_game.update {
					it.copy(
						grid = game.grid,
						hintsUsed = game.hintsUsed,
						elapsedTime = game.elapsedTime,
						difficulty = game.difficulty,
						hintLogs = game.hintLogs.toPersistentList(),
					)
				}
			}
		} ?: throw Exception("Proto Sudoku not found!")
		settingsRepository.leftHandMode.firstOrNull()?.let { leftHandMode ->
			_uiState.update {
				it.copy(isLeftHandMode = leftHandMode)
			}
		}
		settingsRepository.showActionButtonsOnTop.firstOrNull()?.let { actionButtonsOnTop ->
			_uiState.update {
				it.copy(showActionButtonsOnTop = actionButtonsOnTop)
			}
		}
	}

	private fun selectCell(row: Int, col: Int) {
		viewModelScope.launch {
			_game.update {
				it.copy(
					grid =
					selectCellUseCase(
						sudoku = _game.value.grid,
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
		noteMode: Boolean,
		isHint: Boolean = false,
	) {
		viewModelScope.launch {
			var updatedSudoku = _game.value.grid
			val (row, col) = selectedCell ?: return@launch

			val backupCell = updatedSudoku.getCellAt(row, col)
			updatedSudoku =
				inputNumberUseCase(
					sudokuGrid = updatedSudoku,
					number = number,
					row = row,
					column = col,
					isNote = noteMode,
					isHint = isHint,
				)
			operationRepository.apply {
				addUndoOperation(
					Operation(
						id = operationRepository.getUndoOperations().size.toLong(),
						cell = updatedSudoku.getCellAt(row, col),
						oldCell = backupCell,
					),
				)
				clearRedoOperations()
			}

			_game.update {
				it.copy(
					grid = updatedSudoku,
				)
			}
			handleAllCellsFilled()
		}
	}

	private fun resetGame() {
		viewModelScope.launch {
			_game.update { resetGameUseCase(it) }
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
			var updatedSudoku = _game.value.grid
			updatedSudoku = updatedSudoku.clearAllCornerNotes()
			_game.update {
				it.copy(
					grid = updatedSudoku,
				)
			}
		}
	}

	private fun handleAllCellsFilled() {
		viewModelScope.launch {
			val result = ClassicSudokuSolver.isValidSolution(_game.value.grid)
			if (result) {
				_uiState.update {
					it.copy(
						gameState = GameState.VICTORY,
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
					),
				)
			}
		}
	}

	private fun handleDismissVictoryDialog() {
		_uiState.update {
			it.copy(
				gameState = GameState.PLAYING,
			)
		}
	}

	private fun switchInputMode() {
		_uiState.update {
			it.copy(
				isInNoteMode = !it.isInNoteMode,
			)
		}
	}

	private fun undoLastMove() {
		viewModelScope.launch {
			mutex.withLock {
				if (operationRepository.getUndoOperations().isEmpty()) return@withLock

				val grid = undoOperationUseCase(_game.value.grid)
				_game.update {
					it.copy(grid = grid)
				}
			}
		}
	}

	private fun redoLastMove() {
		viewModelScope.launch {
			mutex.withLock {
				if (operationRepository.getRedoOperations().isEmpty()) return@withLock
				val grid = redoOperationUseCase(_game.value.grid)
				_game.update {
					it.copy(grid = grid)
				}
			}
		}
	}

	private fun handleNumberInput(
		number: Int,
		sudoku: SudokuGrid,
		selectedCell: Pair<Int, Int>,
	): SudokuGrid {
		val (row, col) = selectedCell
		var updatedSudoku = sudoku
		if (updatedSudoku.getCellAt(row, col).attributes.contains(CellAttributes.GENERATED)) {
			return updatedSudoku
		}

		if (_uiState.value.lastHint?.row == row &&
			_uiState.value.lastHint?.col == col &&
			number == _uiState.value.lastHint?.value
		) {
			val updatedLogs = _game.value.hintLogs.toMutableList()
			val lastHintId = updatedLogs.indexOfLast { it.hint == _uiState.value.lastHint }
			val log = updatedLogs[lastHintId]
			updatedLogs[lastHintId] =
				log.copy(
					isUserGuessed = true,
					explanation = log.explanation + "~You guessed correctly!~",
				)
			_uiState.update {
				it.copy(
					lastHint = null,
				)
			}
			_game.update {
				it.copy(
					hintLogs = updatedLogs.toPersistentList(),
				)
			}
		}

		return updatedSudoku
	}

	private fun fillNotes() {
		viewModelScope.launch {
			_game.update {
				it.copy(
					grid = _game.value.grid.fillNotes(),
				)
			}
		}
	}

	private fun provideHint() {
		viewModelScope.launch {
			if (_uiState.value.gameState == GameState.VICTORY) return@launch

			val hint: Hint? = provideHintUseCase(_game.value)
			if (hint != null) {
				var updatedSudoku = _game.value.grid
				updatedSudoku = focusOnHintCellsUseCase(hint, updatedSudoku)
				selectCell(hint.row, hint.col)
				val hintLog =
					generateHintLogUseCase(
						id = _game.value.hintLogs.size,
						hint = hint,
						grid = updatedSudoku,
					)
				_game.update {
					it.copy(
						grid = updatedSudoku,
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

	private fun revealHint() {
		viewModelScope.launch {
			val hint: Hint = _uiState.value.lastHint ?: return@launch
			if (_game.value.grid
					.getCellAt(hint.row, hint.col)
					.number != 0
			) {
				return@launch
			}
			selectCell(hint.row, hint.col)

			val updatedSudoku = revealHintOnGridUseCase(hint, _game.value.grid)
			val updatedLogs = revealLastHintLogUseCase(_game.value.hintLogs)

			_uiState.update {
				it.copy(
					lastHint = null,
				)
			}
			_game.update {
				it.copy(
					grid = updatedSudoku,
					hintLogs = updatedLogs,
				)
			}
		}
	}

	private fun stopTrackingTime() {
		viewModelScope.launch {
			elapsedTimerManager.stopTracking()
		}
	}
}
