package com.example.sudokuslayer.presentation.screen.sudokucreator

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.Stable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.sudoku.generator.ClassicSudokuGenerator
import com.example.sudokuslayer.data.datastore.SudokuDataStoreRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.random.Random

@Stable
@Immutable
data class SudokuCreatorUiState(
	val loadingState: ScreenState = ScreenState.INITIAL,
	val selectedDifficulty: SudokuDifficulty = SudokuDifficulty.EASY,
	val selectedGridSize: SudokuGridSize = SudokuGridSize.FOUR,
	val savedGameData: SavedGameData? = null,
)

@Stable
enum class ScreenState {
	INITIAL,
	LOADING,
	DONE
}

@Stable
data class SavedGameData(
	val elapsedTime: Long,
	val difficulty: SudokuDifficulty
)

enum class SudokuGridSize {
	FOUR,
	NINE,
	SIXTEEN;

	override fun toString(): String {
		return when (this) {
			FOUR -> "4x4"
			NINE -> "9x9"
			SIXTEEN -> "16x16"
		}
	}

	companion object {
		fun fromInt(size: Int): SudokuGridSize {
			return when (size) {
				0 -> FOUR
				1 -> NINE
				2 -> SIXTEEN
				else -> NINE
			}
		}
	}
}

enum class SudokuDifficulty {
	EASY,
	MEDIUM,
	HARD,
	EXPERT;

	companion object {
		fun fromInt(difficulty: Int): SudokuDifficulty {
			return when (difficulty) {
				0 -> EASY
				1 -> MEDIUM
				2 -> HARD
				3 -> EXPERT
				else -> EASY
			}
		}
	}
}

class SudokuCreatorViewModel(
	private val dataStoreRepository: SudokuDataStoreRepository
) : ViewModel() {
	private val _uiState = MutableStateFlow<SudokuCreatorUiState>(SudokuCreatorUiState())
	val uiState: StateFlow<SudokuCreatorUiState> = _uiState.asStateFlow()

	init {
		viewModelScope.launch {
			dataStoreRepository.sudokuGridProto.firstOrNull()?.let { sudoku ->
				_uiState.update {
					it.copy(
						selectedGridSize = SudokuGridSize.fromInt(sudoku.gridSize),
						savedGameData = SavedGameData(0, SudokuDifficulty.EASY)
					)
				}
			}

			dataStoreRepository.difficultyProto.firstOrNull()?.let { difficulty ->
				_uiState.update {
					it.copy(
						savedGameData = it.savedGameData!!.copy(difficulty = difficulty)
					)
				}
			}

			dataStoreRepository.elapsedTimeProto.firstOrNull()?.let { elapsedTime ->
				_uiState.update {
					it.copy(
						savedGameData = it.savedGameData!!.copy(elapsedTime = elapsedTime)
					)
				}
			}
		}
	}

	sealed interface Event {
		data class ChangeDifficulty(val num: Int) : Event
		data class ChangeGridSize(val num: Int) : Event
		data object NewGame : Event
		data object LoadSudoku : Event
	}

	fun onEvent(event: Event) {
		when (event) {
			is Event.ChangeDifficulty -> handleChangeDifficulty(event.num)
			is Event.ChangeGridSize -> handleChangeGridSize(event.num)
			Event.NewGame -> {
				handleNewGame()
			}

			Event.LoadSudoku -> {
				handleLoadGame()
			}
		}
	}

	private fun handleChangeGridSize(num: Int) {
		viewModelScope.launch {
			_uiState.update {
				it.copy(
					selectedGridSize = SudokuGridSize.fromInt(num)
				)
			}
		}
	}

	private fun handleChangeDifficulty(num: Int) {
		viewModelScope.launch {
			_uiState.update {
				it.copy(
					selectedDifficulty = SudokuDifficulty.fromInt(num)
				)
			}
		}
	}

	private fun handleNewGame() {
		_uiState.update {
			it.copy(loadingState = ScreenState.LOADING)
		}
		viewModelScope.launch(Dispatchers.IO) {
			val gridSize = when (_uiState.value.selectedGridSize) {
				SudokuGridSize.FOUR -> 4
				SudokuGridSize.NINE -> 9
				SudokuGridSize.SIXTEEN -> 16
			}
			val generator = ClassicSudokuGenerator(gridSize)
			val cellsToRemove: Int =
				calculateCellsToRemove(
					_uiState.value.selectedDifficulty,
					_uiState.value.selectedGridSize
				)

			val sudoku = generator.createSudoku(cellsToRemove)
			_uiState.update {
				it.copy(
					loadingState = ScreenState.DONE
				)
			}

			dataStoreRepository.updateData(sudoku)
			dataStoreRepository.updateDifficulty(_uiState.value.selectedDifficulty)
			dataStoreRepository.updateElapsedTime(0L)
		}
	}

	private fun handleLoadGame() {
		_uiState.update {
			it.copy(loadingState = ScreenState.LOADING)
		}
		viewModelScope.launch {
			_uiState.update {
				it.copy(
					loadingState = ScreenState.DONE
				)
			}
		}
	}

	private fun calculateCellsToRemove(
		difficulty: SudokuDifficulty,
		gridSize: SudokuGridSize
	): Int {
		return when (gridSize) {
			SudokuGridSize.FOUR -> when (difficulty) {
				SudokuDifficulty.EASY -> Random.nextInt(2, 4)
				SudokuDifficulty.MEDIUM -> Random.nextInt(4, 6)
				SudokuDifficulty.HARD -> Random.nextInt(6, 8)
				SudokuDifficulty.EXPERT -> Random.nextInt(8, 10)
			}

			SudokuGridSize.NINE -> when (difficulty) {
				SudokuDifficulty.EASY -> Random.nextInt(30, 40)
				SudokuDifficulty.MEDIUM -> Random.nextInt(41, 50)
				SudokuDifficulty.HARD -> Random.nextInt(51, 60)
				SudokuDifficulty.EXPERT -> Random.nextInt(61, 64)
			}

			SudokuGridSize.SIXTEEN -> when (difficulty) {
				SudokuDifficulty.EASY -> Random.nextInt(100, 120)
				SudokuDifficulty.MEDIUM -> Random.nextInt(121, 140)
				SudokuDifficulty.HARD -> Random.nextInt(141, 160)
				SudokuDifficulty.EXPERT -> Random.nextInt(161, 180)
			}
		}
	}
}

class SudokuCreatorViewModelFactory(private val dataStoreRepository: SudokuDataStoreRepository) :
	ViewModelProvider.NewInstanceFactory() {
	override fun <T : ViewModel> create(modelClass: Class<T>): T =
		SudokuCreatorViewModel(dataStoreRepository) as T
}