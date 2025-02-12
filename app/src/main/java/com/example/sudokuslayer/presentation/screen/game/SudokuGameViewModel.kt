package com.example.sudokuslayer.presentation.screen.game

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.sudoku.model.CellAttributes
import com.example.sudoku.model.SudokuCellData
import com.example.sudoku.model.SudokuGrid
import com.example.sudoku.solver.ClassicSudokuSolver
import com.example.sudoku.solver.Hint
import com.example.sudoku.solver.HintProvider
import com.example.sudoku.solver.HintType
import com.example.sudoku.solver.fillCandidates
import com.example.sudokuslayer.data.datastore.SudokuDataStoreRepository
import com.example.sudokuslayer.presentation.screen.game.model.GameState
import com.example.sudokuslayer.presentation.screen.game.model.HintLog
import com.example.sudokuslayer.presentation.screen.game.model.InputMode
import com.example.sudokuslayer.presentation.screen.game.model.SudokuGameUiState
import com.example.sudokuslayer.presentation.screen.game.model.SudokuMove
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch


class SudokuGameViewModel(
	private val dataStoreRepository: SudokuDataStoreRepository
) : ViewModel() {
	private val _uiState = MutableStateFlow<SudokuGameUiState>(SudokuGameUiState())
	val uiState: StateFlow<SudokuGameUiState> = _uiState
	private val _isLoading = MutableStateFlow(false)
	val isLoading: StateFlow<Boolean> = _isLoading

	init {
		_isLoading.value = true
		viewModelScope.launch(Dispatchers.IO) {
			loadData()
		}.invokeOnCompletion {
			_isLoading.value = false
		}
	}

	private val lastMoves: ArrayDeque<SudokuMove> = ArrayDeque()
	private val futureMoves: ArrayDeque<SudokuMove> = ArrayDeque()

	sealed interface Event {
		data class SelectCell(val row: Int, val col: Int) : Event
		data class InputNumber(val number: Int) : Event
		data class SwitchInputMode(val inputMode: InputMode) : Event
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
	}

	fun onEvent(event: Event) {
		when (event) {
			is Event.SelectCell -> selectCell(event.row, event.col)
			is Event.InputNumber -> inputNumber(
				event.number,
				_uiState.value.selectedCell,
				_uiState.value.inputMode
			)

			is Event.ClearCell -> inputNumber(
				0,
				_uiState.value.selectedCell,
				_uiState.value.inputMode
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
			is Event.SwitchInputMode -> switchInputMode(event.inputMode)
			is Event.ResetNotes -> resetNotes()
		}
	}

	private suspend fun loadData() {
		dataStoreRepository.sudokuGridProto.firstOrNull()?.let { gridData ->
			viewModelScope.launch(Dispatchers.IO) {
				_uiState.update {
					it.copy(
						sudoku = gridData
					)
				}
			}
		} ?: throw Exception("Proto Sudoku not found!")

		dataStoreRepository.difficultyProto.firstOrNull()?.let { difficulty ->
			_uiState.update {
				it.copy(
					difficulty = difficulty
				)
			}
		} ?: throw Exception("Proto Difficulty not found!")
	}

	private fun selectCell(row: Int, col: Int) {
		viewModelScope.launch {
			val updatedSudoku = _uiState.value.sudoku.clone()
			val lastSelected = _uiState.value.selectedCell
			lastSelected?.let {
				updatedSudoku.removeAttribute(it.first, it.second, CellAttributes.SELECTED)
				if (updatedSudoku[row, col].number != 0)
					updatedSudoku.clearNumberHighlight()
				updatedSudoku.clearRowColumnHighlight()
			}

			updatedSudoku.addAttribute(row, col, CellAttributes.SELECTED)
			val currentlySelected = updatedSudoku[row, col]
			updatedSudoku.highlightMatchingCells(currentlySelected.number)
			updatedSudoku.highlightRowAndColumn(currentlySelected.row, currentlySelected.col)

			_uiState.update {
				it.copy(
					sudoku = updatedSudoku,
					selectedCell = currentlySelected.row to currentlySelected.col
				)
			}
		}
	}

	private fun inputNumber(
		number: Int,
		selectedCell: Pair<Int, Int>?,
		inputMode: InputMode,
		isHint: Boolean = false
	) {
		viewModelScope.launch {
			val currentState = _uiState.value
			val updatedSudoku = currentState.sudoku.clone()
			val (row, col) = selectedCell ?: return@launch

			if (updatedSudoku[row, col].attributes.contains(
					CellAttributes.GENERATED
				)
			) return@launch
			val backupCell = updatedSudoku[row, col]

			when (inputMode) {
				InputMode.NUMBER -> handleNumberInput(number, updatedSudoku, selectedCell)

				InputMode.NOTE -> handleNoteInput(number, updatedSudoku, selectedCell, isHint)

				InputMode.COLOR -> {}
			}

			if (isHint && InputMode.NUMBER == inputMode) {
				updatedSudoku.removeAttribute(row, col, CellAttributes.HINT_FOCUS)
				updatedSudoku.addAttribute(row, col, CellAttributes.HINT_REVEALED)
			}

			saveMoveAndUpdateState(backupCell, updatedSudoku)
		}
	}

	private fun resetGame() {
		viewModelScope.launch {
			var updatedSudoku = _uiState.value.sudoku.clone()
			updatedSudoku.resetGame()
			_uiState.update {
				it.copy(
					sudoku = updatedSudoku,
					hintLogs = emptyList(),
					lastHint = null,
					selectedCell = null,
				)
			}
			lastMoves.clear()
			futureMoves.clear()
			dataStoreRepository.updateData(updatedSudoku)
		}
	}

	private fun resetNotes() {
		viewModelScope.launch {
			val updatedSudoku = _uiState.value.sudoku.clone()
			updatedSudoku.clearNotes()
			_uiState.update {
				it.copy(
					sudoku = updatedSudoku
				)
			}
			dataStoreRepository.updateData(updatedSudoku)
		}
	}

	private fun handleAllCellsFilled() {
		val result = ClassicSudokuSolver.isValidSolution(_uiState.value.sudoku)
		if (result) {
			_uiState.update {
				it.copy(
					gameState = GameState.VICTORY
				)
			}
		}
	}

	private fun handleDismissVictoryDialog() {
		_uiState.update {
			it.copy(
				gameState = GameState.PLAYING
			)
		}
	}

	private fun switchInputMode(mode: InputMode) {
		_uiState.update {
			it.copy(
				inputMode = mode
			)
		}
	}

	private fun handleMove(
		moveStack: ArrayDeque<SudokuMove>,
		targetStack: ArrayDeque<SudokuMove>
	) {
		if (moveStack.isEmpty())
			return

		viewModelScope.launch {
			val (previousCellData, newCellData) = moveStack.removeLast()
			val updatedSudoku = _uiState.value.sudoku.clone()

			updatedSudoku.replaceCell(previousCellData.row, previousCellData.col, previousCellData)
			targetStack.add(SudokuMove(newCellData, previousCellData))

			updatedSudoku.clearRuleBreakingCells()
			updatedSudoku.markRuleBreakingCells()

			_uiState.update { it.copy(sudoku = updatedSudoku) }

			dataStoreRepository.updateCell(
				row = previousCellData.row,
				col = previousCellData.col,
				newCellData = updatedSudoku[previousCellData.row, previousCellData.col]
			)
		}
	}

	private fun undoLastMove() = handleMove(lastMoves, futureMoves)

	private fun redoLastMove() = handleMove(futureMoves, lastMoves)

	private fun handleNumberInput(number: Int, sudoku: SudokuGrid, selectedCell: Pair<Int, Int>) {
		val (row, col) = selectedCell
		if (number == 0) {
			sudoku[row, col] = 0
			sudoku.clearCornerNotes(row, col)
		} else {
			sudoku[row, col] = if (sudoku[row, col].number == number) 0 else number
			if (_uiState.value.lastHint?.row == row &&
				_uiState.value.lastHint?.col == col &&
				number == _uiState.value.lastHint?.value
			) {
				val updatedLogs = _uiState.value.hintLogs.toMutableList()
				val lastHintId = updatedLogs.indexOfLast { it.hint == _uiState.value.lastHint }
				val log = updatedLogs[lastHintId]
				updatedLogs[lastHintId] = log.copy(
					isUserGuessed = true,
					explanation = log.explanation + "~You guessed correctly!~"
				)
				_uiState.update {
					it.copy(
						lastHint = null,
						hintLogs = updatedLogs
					)
				}
			}
			sudoku.clearNumberHighlight()
			sudoku.highlightMatchingCells(number)
			sudoku.clearRuleBreakingCells()
			sudoku.markRuleBreakingCells()
		}
	}

	private fun handleNoteInput(
		number: Int,
		sudoku: SudokuGrid,
		selectedCell: Pair<Int, Int>,
		isHint: Boolean = false
	) {
		val (row, col) = selectedCell
		if (number == 0) {
			sudoku[row, col] = 0
			sudoku.clearCornerNotes(row, col)
		} else if (number in sudoku[row, col].cornerNotes && !isHint) {
			sudoku.removeCornerNote(row, col, number)
		} else {
			sudoku.addCornerNote(row, col, number)
		}
	}

	private fun saveMoveAndUpdateState(
		previousCellData: SudokuCellData,
		updatedSudoku: SudokuGrid
	) {
		val (row, col) = previousCellData
		lastMoves.add(
			SudokuMove(
				previousCellData = previousCellData,
				newCellData = updatedSudoku[row, col]
			)
		)

		_uiState.update {
			it.copy(sudoku = updatedSudoku)
		}

		viewModelScope.launch {
			dataStoreRepository.updateCell(row, col, updatedSudoku[row, col])
		}

		if (updatedSudoku.getEmptyCellsCount() == 0) {
			handleAllCellsFilled()
		}
	}

	private fun fillNotes() {
		viewModelScope.launch {
			val updatedSudoku = _uiState.value.sudoku.clone()
			updatedSudoku.fillNotes()
			_uiState.update {
				it.copy(
					sudoku = updatedSudoku
				)
			}
			dataStoreRepository.updateData(updatedSudoku)
		}
	}

	private fun provideHint() {
		viewModelScope.launch {
			if (_uiState.value.gameState == GameState.VICTORY) return@launch
			val updatedSudoku = _uiState.value.sudoku.clone()
			val hintProvider = HintProvider(updatedSudoku.gridSize)
			// Fill candidates once
			val filledCandidatesGrid = hintProvider.fillCandidates(updatedSudoku.getArray())

			// Remove candidates from all affected cells for locked candidate hints
			_uiState.value.hintLogs.forEach { log ->
				log.hint.let { hint ->
					when (hint.type) {
						is HintType.ClaimingCandidate, is HintType.PointingCandidate -> {
							if (hint.affectedCells.isNotEmpty()) {
								hint.affectedCells.forEach { affected ->
									filledCandidatesGrid.indexOfFirst { it.row == affected.row && it.col == affected.col }
										.takeIf { it != -1 }?.let { index ->
											val cell = filledCandidatesGrid[index]
											filledCandidatesGrid[index] =
												cell.copy(candidates = cell.candidates - hint.value)
										}
								}
							} else {
								filledCandidatesGrid.indexOfFirst { it.row == hint.row && it.col == hint.col }
									.takeIf { it != -1 }?.let { index ->
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

			Log.d("Hint", hint.toString())
			if (hint != null) {
				when (hint.type!!) {
					is HintType.HiddenSingle, is HintType.NakedSingle -> {
						updatedSudoku.addAttribute(hint.row, hint.col, CellAttributes.HINT_FOCUS)
					}

					is HintType.PointingCandidate -> {
							hint.enforcingCells.forEach { cell ->
								updatedSudoku.addAttribute(
									cell.row,
									cell.col,
									CellAttributes.HINT_FOCUS
								)
							}
					}

					is HintType.ClaimingCandidate -> {
							hint.enforcingCells.forEach { cell ->
								updatedSudoku.addAttribute(
									cell.row,
									cell.col,
									CellAttributes.HINT_FOCUS
								)
						}
					}
				}


				selectCell(hint.row, hint.col)
				val explanationSteps = hint.explanationStrategy?.generateHintExplanationSteps(updatedSudoku, hint) ?: emptyList()
				val hintLog = HintLog(
					hint = hint,
					isUserGuessed = false,
					isRevealed = false,
					explanation = explanationSteps
				)
				_uiState.update {
					it.copy(
						sudoku = updatedSudoku,
						lastHint = hint,
						hintLogs = it.hintLogs + hintLog
					)
				}
			}
		}
	}

	private fun explainHint() {
		viewModelScope.launch {
			val hint: Hint = _uiState.value.lastHint ?: return@launch
			if (_uiState.value.sudoku[hint.row, hint.col].number != 0) return@launch

			selectCell(hint.row, hint.col)
			when (hint.type) {
				is HintType.PointingCandidate -> {
					val otherCells = hint.enforcingCells
					otherCells.forEach { cell ->
						_uiState.value.sudoku.highlightMatchingCells(hint.value)
						inputNumber(hint.value, cell.row to cell.col, InputMode.NOTE, true)
					}
				}

				is HintType.ClaimingCandidate -> {
					val otherCells = hint.enforcingCells
					otherCells.forEach { cell ->
						_uiState.value.sudoku.highlightMatchingCells(hint.value)
						inputNumber(hint.value, cell.row to cell.col, InputMode.NOTE, true)
					}
				}

				else -> {
					inputNumber(hint.value, hint.row to hint.col, InputMode.NUMBER, true)
				}
			}

			val updatedLogs = _uiState.value.hintLogs.toMutableList()
			val lastHintId = updatedLogs.indexOfLast { it.hint == _uiState.value.lastHint }
			updatedLogs[lastHintId] = updatedLogs[lastHintId].copy(isRevealed = true)

			_uiState.update {
				it.copy(
					hintLogs = updatedLogs,
					lastHint = null
				)
			}
		}
	}
}

class SudokuGameViewModelFactory(
	private val dataStoreRepository: SudokuDataStoreRepository
) : ViewModelProvider.NewInstanceFactory() {
	override fun <T : ViewModel> create(modelClass: Class<T>): T =
		SudokuGameViewModel(dataStoreRepository) as T
}