package com.example.sudokuslayer.presentation.screen.sudokucreator

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.Stable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.domain.core.Game
import com.example.domain.core.GameDifficulty
import com.example.domain.core.SudokuGridSize
import com.example.domain.creator.CreateNewGameUseCase
import com.example.domain.creator.GetSavedGameUseCase
import com.example.domain.creator.SaveGameUseCase
import kotlinx.collections.immutable.toPersistentList
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@Stable
@Immutable
data class SudokuCreatorUiState(
	val loadingState: ScreenState = ScreenState.INITIAL,
	val selectedDifficulty: GameDifficulty = GameDifficulty.Easy,
	val selectedGridSize: SudokuGridSize = SudokuGridSize.FOUR,
	val savedGame: Game? = null,
)

@Stable
enum class ScreenState {
	INITIAL,
	LOADING,
	DONE,
}

class SudokuCreatorViewModel(
	private val createNewGameUseCase: CreateNewGameUseCase,
	private val getSavedGameUseCase: GetSavedGameUseCase,
	private val saveGameUseCase: SaveGameUseCase,
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
			getSavedGameUseCase()?.let { savedGame ->
				_uiState.update {
					it.copy(
						savedGame = savedGame,
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
			is Event.NewGame -> handleNewGame()
			is Event.LoadSudoku -> handleLoadGame()
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
		viewModelScope.launch(Dispatchers.IO) {
			_uiState.update {
				it.copy(loadingState = ScreenState.LOADING)
			}
			val newGame =
				createNewGameUseCase(
					_uiState.value.selectedGridSize,
					_uiState.value.selectedDifficulty,
				)
			saveGameUseCase(newGame)

			_uiState.update {
				it.copy(
					loadingState = ScreenState.DONE,
				)
			}
		}
	}

	private fun handleLoadGame() {
		viewModelScope.launch {
			_uiState.update {
				it.copy(
					loadingState = ScreenState.DONE,
				)
			}
		}
	}
}
