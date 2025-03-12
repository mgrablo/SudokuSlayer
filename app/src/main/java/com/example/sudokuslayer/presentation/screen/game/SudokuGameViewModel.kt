package com.example.sudokuslayer.presentation.screen.game

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.game.ProtoGameRepository
import com.example.data.game.models.GameDifficulty
import com.example.data.settings.SettingsRepository
import com.example.domain.game.usecases.GetGameUseCase
import com.example.domain.game.usecases.SaveGameUseCase
import com.example.domain.game.usecases.SelectCellUseCase
import com.example.sudoku.model.CellAttributes
import com.example.sudoku.model.SudokuCellData
import com.example.sudoku.model.SudokuGrid
import com.example.sudoku.model.addAttribute
import com.example.sudoku.model.addCornerNote
import com.example.sudoku.model.clearAllCornerNotes
import com.example.sudoku.model.clearCornerNotes
import com.example.sudoku.model.clearGrid
import com.example.sudoku.model.clearMatchingNumberHighlight
import com.example.sudoku.model.clearRowColumnHighlight
import com.example.sudoku.model.clearRuleBreakingCells
import com.example.sudoku.model.fillNotes
import com.example.sudoku.model.highlightMatchingCells
import com.example.sudoku.model.highlightRowAndColumn
import com.example.sudoku.model.markRuleBreakingCells
import com.example.sudoku.model.removeAttribute
import com.example.sudoku.model.removeCornerNote
import com.example.sudoku.solver.ClassicSudokuSolver
import com.example.sudoku.solver.Hint
import com.example.sudoku.solver.HintProvider
import com.example.sudoku.solver.HintType
import com.example.sudoku.solver.fillCandidates
import com.example.sudokuslayer.presentation.screen.game.model.GameState
import com.example.sudokuslayer.presentation.screen.game.model.HintLog
import com.example.sudokuslayer.presentation.screen.game.model.SudokuGameUiState
import com.example.sudokuslayer.presentation.screen.game.model.SudokuMove
import com.example.sudokuslayer.presentation.screen.sudokucreator.SudokuDifficulty
import kotlinx.collections.immutable.minus
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.plus
import kotlinx.collections.immutable.toPersistentList
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class SudokuGameViewModel(
	private val dataStoreRepository: ProtoGameRepository,
	private val settingsRepository: SettingsRepository,
	private val getGameUseCase: GetGameUseCase,
	private val saveGameUseCase: SaveGameUseCase,
	private val selectCellUseCase: SelectCellUseCase,
) : ViewModel() {
	private val _uiState = MutableStateFlow<SudokuGameUiState>(SudokuGameUiState())
	val uiState: StateFlow<SudokuGameUiState> = _uiState

	init {
		_uiState.update {
			it.copy(
				gameState = GameState.LOADING,
			)
		}
		viewModelScope
			.launch(Dispatchers.IO) {
				loadData()
			}.invokeOnCompletion {
				_uiState.update {
					it.copy(
						gameState = GameState.PLAYING,
					)
				}
			}

		viewModelScope.launch {
			settingsRepository.leftHandMode.collect { isLeftHandMode ->
				_uiState.update {
					it.copy(
						isLeftHandMode = isLeftHandMode,
					)
				}
			}
		}
		viewModelScope.launch {
			settingsRepository.showActionButtonsOnTop.collect { showActionButtonsOnTop ->
				_uiState.update {
					it.copy(
						showActionButtonsOnTop = showActionButtonsOnTop,
					)
				}
			}
		}
	}

	override fun onCleared() {
		super.onCleared()
		saveGameState()
	}

	private val lastMoves: ArrayDeque<SudokuMove> = ArrayDeque()
	private val futureMoves: ArrayDeque<SudokuMove> = ArrayDeque()

	sealed interface Event {
		data class SelectCell(
			val row: Int,
			val col: Int,
		) : Event

		data class InputNumber(
			val number: Int,
		) : Event

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

		data object ResetTimer : Event
	}

	fun onEvent(event: Event) {
		when (event) {
			is Event.SelectCell -> selectCell(event.row, event.col)
			is Event.InputNumber ->
				inputNumber(
					event.number,
					_uiState.value.selectedCell,
					_uiState.value.isInNoteMode,
				)

			is Event.ClearCell ->
				inputNumber(
					0,
					_uiState.value.selectedCell,
					_uiState.value.isInNoteMode,
				)

			is Event.Undo -> undoLastMove()
			is Event.Redo -> redoLastMove()
			is Event.ResetGame -> resetGame()
			is Event.ProvideHint -> {
				provideHint()
			}

			is Event.ExplainHint -> {
				explainHint()
			}

			is Event.HintFillNotes -> fillNotes()
			is Event.ShowMistakes -> {}
			is Event.DismissVictoryDialog -> handleDismissVictoryDialog()
			is Event.SwitchInputMode -> switchInputMode()
			is Event.ResetNotes -> resetNotes()
			is Event.ResetTimer -> {}
		}
	}

	private suspend fun loadData() {
		dataStoreRepository.getGame().firstOrNull()?.let { game ->
			viewModelScope.launch(Dispatchers.IO) {
				_uiState.update {
					it.copy(
						sudoku = game.grid,
						difficulty =
							when (game.difficulty) {
								GameDifficulty.Easy -> SudokuDifficulty.EASY
								GameDifficulty.Medium -> SudokuDifficulty.MEDIUM
								GameDifficulty.Hard -> SudokuDifficulty.HARD
								GameDifficulty.Expert -> SudokuDifficulty.EXPERT
							},
					)
				}
			}
		} ?: throw Exception("Proto Sudoku not found!")
	}

	private fun selectCell(
		row: Int,
		col: Int,
	) {
		viewModelScope.launch {
			var updatedSudoku =
				selectCellUseCase(
					sudoku = _uiState.value.sudoku,
					selectedCell = row to col,
				)

			_uiState.update {
				it.copy(
					sudoku = updatedSudoku,
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
			val currentState = _uiState.value
			var updatedSudoku = currentState.sudoku
			val (row, col) = selectedCell ?: return@launch

			if (updatedSudoku.getCellAt(row, col).attributes.contains(
					CellAttributes.GENERATED,
				)
			) {
				return@launch
			}

			val backupCell = updatedSudoku.getCellAt(row, col)
			updatedSudoku =
				if (noteMode) {
					handleNoteInput(number, updatedSudoku, selectedCell, isHint)
				} else {
					handleNumberInput(number, updatedSudoku, selectedCell)
				}

			if (isHint && !noteMode) {
				updatedSudoku = updatedSudoku.removeAttribute(row, col, CellAttributes.HINT_FOCUS)
				updatedSudoku = updatedSudoku.addAttribute(row, col, CellAttributes.HINT_REVEALED)
			}

			saveMoveAndUpdateState(backupCell, updatedSudoku)
		}
	}

	private fun resetGame() {
		viewModelScope.launch {
			var updatedSudoku = _uiState.value.sudoku
			updatedSudoku = updatedSudoku.clearGrid()
			_uiState.update {
				it.copy(
					sudoku = updatedSudoku,
					hintLogs = persistentListOf(),
					lastHint = null,
					selectedCell = null,
				)
			}
			lastMoves.clear()
			futureMoves.clear()
			dataStoreRepository.updateGrid(updatedSudoku)
		}
	}

	private fun resetNotes() {
		viewModelScope.launch {
			var updatedSudoku = _uiState.value.sudoku
			updatedSudoku = updatedSudoku.clearAllCornerNotes()
			_uiState.update {
				it.copy(
					sudoku = updatedSudoku,
				)
			}
			dataStoreRepository.updateGrid(updatedSudoku)
		}
	}

	private fun handleAllCellsFilled() {
		val result = ClassicSudokuSolver.isValidSolution(_uiState.value.sudoku)
		if (result) {
			_uiState.update {
				it.copy(
					gameState = GameState.VICTORY,
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

	private fun handleMove(
		moveStack: ArrayDeque<SudokuMove>,
		targetStack: ArrayDeque<SudokuMove>,
	) {
		if (moveStack.isEmpty()) {
			return
		}

		viewModelScope.launch {
			val (previousCellData, newCellData) = moveStack.removeLast()
			var updatedSudoku = _uiState.value.sudoku

			updatedSudoku =
				updatedSudoku.withReplacedCell(
					previousCellData.row,
					previousCellData.col,
					previousCellData,
				)
			targetStack.add(SudokuMove(newCellData, previousCellData))

			updatedSudoku =
				updatedSudoku
					.clearMatchingNumberHighlight()
					.clearRowColumnHighlight()
			updatedSudoku =
				updatedSudoku
					.highlightMatchingCells(previousCellData.number)
					.highlightRowAndColumn(previousCellData.row, previousCellData.col)
			updatedSudoku =
				updatedSudoku
					.clearRuleBreakingCells()
					.markRuleBreakingCells()

			_uiState.update { it.copy(sudoku = updatedSudoku) }

			dataStoreRepository.updateCell(
				row = previousCellData.row,
				column = previousCellData.col,
				cellData = updatedSudoku.getCellAt(previousCellData.row, previousCellData.col),
			)
		}
	}

	private fun undoLastMove() = handleMove(lastMoves, futureMoves)

	private fun redoLastMove() = handleMove(futureMoves, lastMoves)

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

		if (number == 0) {
			updatedSudoku = updatedSudoku.withValue(row, col, 0)
			updatedSudoku = updatedSudoku.clearCornerNotes(row, col)
		} else {
			val newValue = if (sudoku.getCellAt(row, col).number == number) 0 else number
			updatedSudoku = updatedSudoku.withValue(row, col, newValue)
			updatedSudoku = updatedSudoku.clearMatchingNumberHighlight()
			updatedSudoku = updatedSudoku.highlightMatchingCells(number)

			if (_uiState.value.lastHint?.row == row &&
				_uiState.value.lastHint?.col == col &&
				number == _uiState.value.lastHint?.value
			) {
				val updatedLogs = _uiState.value.hintLogs.toMutableList()
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
						hintLogs = updatedLogs.toPersistentList(),
					)
				}
			}
		}

		updatedSudoku = updatedSudoku.clearRuleBreakingCells()
		updatedSudoku = updatedSudoku.markRuleBreakingCells()
		return updatedSudoku
	}

	private fun handleNoteInput(
		number: Int,
		sudoku: SudokuGrid,
		selectedCell: Pair<Int, Int>,
		isHint: Boolean = false,
	): SudokuGrid {
		val (row, col) = selectedCell
		var updatedSudoku = sudoku
		if (number == 0) {
			updatedSudoku = updatedSudoku.withValue(row, col, 0)
			updatedSudoku = updatedSudoku.clearCornerNotes(row, col)
		} else if (number in sudoku.getCellAt(row, col).cornerNotes && !isHint) {
			updatedSudoku = updatedSudoku.removeCornerNote(row, col, number)
		} else {
			updatedSudoku = updatedSudoku.addCornerNote(row, col, number)
		}
		return updatedSudoku
	}

	private fun saveGameState() {
		viewModelScope.launch(Dispatchers.IO) {
			val currentState = _uiState.value
			dataStoreRepository.updateGrid(currentState.sudoku)
		}
	}

	private fun saveMoveAndUpdateState(
		previousCellData: SudokuCellData,
		updatedSudoku: SudokuGrid,
	) {
		val (row, col) = previousCellData
		lastMoves.add(
			SudokuMove(
				previousCellData = previousCellData,
				newCellData = updatedSudoku.getCellAt(row, col),
			),
		)

		_uiState.update {
			it.copy(sudoku = updatedSudoku)
		}

		saveGameState()

		if (updatedSudoku.getEmptyCellsCount() == 0) {
			handleAllCellsFilled()
		}
	}

	private fun fillNotes() {
		viewModelScope.launch {
			var updatedSudoku = _uiState.value.sudoku
			updatedSudoku = updatedSudoku.fillNotes()
			_uiState.update {
				it.copy(
					sudoku = updatedSudoku,
				)
			}
			dataStoreRepository.updateGrid(updatedSudoku)
		}
	}

	private fun provideHint() {
		viewModelScope.launch {
			if (_uiState.value.gameState == GameState.VICTORY) return@launch
			var updatedSudoku = _uiState.value.sudoku
			val hintProvider = HintProvider()
			// Fill candidates once
			val filledCandidatesGrid =
				hintProvider.fillCandidates(updatedSudoku.data).toMutableList()

			// Remove candidates from all affected cells for locked candidate hints
			_uiState.value.hintLogs.forEach { log ->
				log.hint.let { hint ->
					when (hint.type) {
						is HintType.ClaimingCandidate, is HintType.PointingCandidate -> {
							if (hint.affectedCells.isNotEmpty()) {
								hint.affectedCells.forEach { affected ->
									filledCandidatesGrid
										.indexOfFirst { it.row == affected.row && it.col == affected.col }
										.takeIf { it != -1 }
										?.let { index ->
											val cell = filledCandidatesGrid[index]
											filledCandidatesGrid[index] =
												cell.copy(candidates = cell.candidates - hint.value)
										}
								}
							} else {
								filledCandidatesGrid
									.indexOfFirst { it.row == hint.row && it.col == hint.col }
									.takeIf { it != -1 }
									?.let { index ->
										val cell = filledCandidatesGrid[index]
										filledCandidatesGrid[index] =
											cell.copy(candidates = cell.candidates - hint.value)
									}
							}
						}

						else -> Unit
					}
				}
			}

			val hint: Hint? = hintProvider.provideHint(data = filledCandidatesGrid)
			if (hint != null) {
				when (hint.type!!) {
					is HintType.HiddenSingle, is HintType.NakedSingle -> {
						updatedSudoku =
							updatedSudoku.addAttribute(
								hint.row,
								hint.col,
								CellAttributes.HINT_FOCUS,
							)
					}

					is HintType.PointingCandidate -> {
						hint.enforcingCells.forEach { cell ->
							updatedSudoku =
								updatedSudoku.addAttribute(
									cell.row,
									cell.col,
									CellAttributes.HINT_FOCUS,
								)
						}
					}

					is HintType.ClaimingCandidate -> {
						hint.enforcingCells.forEach { cell ->
							updatedSudoku =
								updatedSudoku.addAttribute(
									cell.row,
									cell.col,
									CellAttributes.HINT_FOCUS,
								)
						}
					}
				}
				selectCell(hint.row, hint.col)
				val explanationSteps =
					hint.explanationStrategy?.generateHintExplanationSteps(updatedSudoku, hint)
						?: emptyList()
				val hintLog =
					HintLog(
						hint = hint,
						isUserGuessed = false,
						isRevealed = false,
						explanation = explanationSteps.toPersistentList(),
					)
				_uiState.update {
					it.copy(
						sudoku = updatedSudoku,
						lastHint = hint,
						hintLogs = it.hintLogs + hintLog,
					)
				}
			}
		}
	}

	private fun explainHint() {
		viewModelScope.launch {
			val hint: Hint = _uiState.value.lastHint ?: return@launch
			if (_uiState.value.sudoku
					.getCellAt(hint.row, hint.col)
					.number != 0
			) {
				return@launch
			}
			selectCell(hint.row, hint.col)

			when (hint.type) {
				is HintType.PointingCandidate -> {
					val otherCells = hint.enforcingCells
					otherCells.forEach { cell ->
						inputNumber(
							number = hint.value,
							selectedCell = cell.row to cell.col,
							noteMode = true,
							isHint = true,
						)
					}
				}

				is HintType.ClaimingCandidate -> {
					val otherCells = hint.enforcingCells
					otherCells.forEach { cell ->
						inputNumber(
							number = hint.value,
							selectedCell = cell.row to cell.col,
							noteMode = false,
							isHint = true,
						)
					}
				}

				else -> {
					inputNumber(
						number = hint.value,
						selectedCell = hint.row to hint.col,
						noteMode = false,
						isHint = true,
					)
				}
			}

			val updatedLogs = _uiState.value.hintLogs.toMutableList()
			val lastHintId = updatedLogs.indexOfLast { it.hint == _uiState.value.lastHint }
			updatedLogs[lastHintId] = updatedLogs[lastHintId].copy(isRevealed = true)

			_uiState.update {
				it.copy(
					hintLogs = updatedLogs.toPersistentList(),
					lastHint = null,
				)
			}
		}
	}
}
