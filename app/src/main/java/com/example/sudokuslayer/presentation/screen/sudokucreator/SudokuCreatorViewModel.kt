package com.example.sudokuslayer.presentation.screen.sudokucreator

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.Stable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.domain.game.models.Game
import com.example.domain.game.models.GameDifficulty
import com.example.domain.game.models.SudokuGridSize
import com.example.domain.game.repositories.GameRepository
import com.example.sudoku.generator.ClassicSudokuGenerator
import kotlinx.collections.immutable.toPersistentList
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
	val selectedDifficulty: GameDifficulty = GameDifficulty.Easy,
	val selectedGridSize: SudokuGridSize = SudokuGridSize.FOUR,
	val savedGameData: SavedGameData? = null,
)

@Stable
enum class ScreenState {
	INITIAL,
	LOADING,
	DONE,
}

@Stable
data class SavedGameData(
	val elapsedTime: Long,
	val difficulty: GameDifficulty,
	val gridSize: SudokuGridSize,
)

class SudokuCreatorViewModel(
	private val dataStoreRepository: GameRepository,
) : ViewModel() {
	private val _uiState = MutableStateFlow<SudokuCreatorUiState>(SudokuCreatorUiState())
	val uiState: StateFlow<SudokuCreatorUiState> = _uiState.asStateFlow()

	val difficulties
		get() =
			GameDifficulty.entries
				.map {
					it.name.lowercase().replaceFirstChar { it.uppercase() }
				}.toPersistentList()

	val gridSizeOptions
		get() =
			SudokuGridSize.entries
				.map {
					when (it) {
						SudokuGridSize.FOUR -> "4x4"
						SudokuGridSize.NINE -> "9x9"
						SudokuGridSize.SIXTEEN -> "16x16"
					}
				}.toPersistentList()

	init {
		viewModelScope.launch {
			dataStoreRepository.getGame().firstOrNull()?.let { game ->
				val gridSize =
					when (game.grid.gridSize) {
						4 -> 0
						9 -> 1
						16 -> 2
						else -> throw IllegalArgumentException("Invalid grid size: ${game.grid.gridSize}")
					}
				_uiState.update {
					it.copy(
						savedGameData =
							SavedGameData(
								elapsedTime = game.elapsedTime,
								gridSize = SudokuGridSize.fromInt(gridSize),
								difficulty = game.difficulty,
							),
					)
				}
			}
		}
	}

	sealed interface Event {
		data class ChangeDifficulty(
			val num: Int,
		) : Event

		data class ChangeGridSize(
			val num: Int,
		) : Event

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
					selectedGridSize = SudokuGridSize.fromInt(num),
				)
			}
		}
	}

	private fun handleChangeDifficulty(num: Int) {
		viewModelScope.launch {
			_uiState.update {
				it.copy(
					selectedDifficulty = GameDifficulty.fromInt(num),
				)
			}
		}
	}

	private fun handleNewGame() {
		_uiState.update {
			it.copy(loadingState = ScreenState.LOADING)
		}
		viewModelScope.launch(Dispatchers.IO) {
			val gridSize =
				when (_uiState.value.selectedGridSize) {
					SudokuGridSize.FOUR -> 4
					SudokuGridSize.NINE -> 9
					SudokuGridSize.SIXTEEN -> 16
				}
			val generator = ClassicSudokuGenerator(gridSize)
			val cellsToRemove: Int =
				calculateCellsToRemove(
					_uiState.value.selectedDifficulty,
					_uiState.value.selectedGridSize,
				)

			val sudoku = generator.createSudoku(cellsToRemove, Random.nextLong())
			_uiState.update {
				it.copy(
					loadingState = ScreenState.DONE,
				)
			}

			dataStoreRepository.saveGame(
				Game(
					grid = sudoku,
					difficulty = _uiState.value.selectedDifficulty,
					elapsedTime = 0L,
					hintsUsed = 0,
					hintLogs = emptyList(),
				),
			)
		}
	}

	private fun handleLoadGame() {
		_uiState.update {
			it.copy(loadingState = ScreenState.LOADING)
		}
		viewModelScope.launch {
			_uiState.update {
				it.copy(
					loadingState = ScreenState.DONE,
				)
			}
		}
	}

	private fun calculateCellsToRemove(
		difficulty: GameDifficulty,
		gridSize: SudokuGridSize,
	): Int =
		when (gridSize) {
			SudokuGridSize.FOUR ->
				when (difficulty) {
					GameDifficulty.Easy -> Random.nextInt(2, 4)
					GameDifficulty.Medium -> Random.nextInt(4, 6)
					GameDifficulty.Hard -> Random.nextInt(6, 8)
					GameDifficulty.Expert -> Random.nextInt(8, 10)
				}

			SudokuGridSize.NINE ->
				when (difficulty) {
					GameDifficulty.Easy -> Random.nextInt(30, 40)
					GameDifficulty.Medium -> Random.nextInt(41, 50)
					GameDifficulty.Hard -> Random.nextInt(51, 60)
					GameDifficulty.Expert -> Random.nextInt(61, 64)
				}

			SudokuGridSize.SIXTEEN ->
				when (difficulty) {
					GameDifficulty.Easy -> Random.nextInt(100, 120)
					GameDifficulty.Medium -> Random.nextInt(121, 140)
					GameDifficulty.Hard -> Random.nextInt(141, 160)
					GameDifficulty.Expert -> Random.nextInt(161, 180)
				}
		}
}
