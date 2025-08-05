package com.example.feature.creator

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.Stable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.domain.core.Game
import com.example.domain.core.GameDifficulty
import com.example.domain.core.SudokuGridSize
import com.example.domain.creator.CreateNewGameUseCase
import com.example.domain.creator.GetSavedGameUseCase
import com.example.domain.creator.HasActiveGameUseCase
import com.example.domain.creator.SaveGameUseCase
import com.example.domain.creator.ValidateSeedInputUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@Stable
@Immutable
internal data class SudokuCreatorUiState(
	val loadingState: ScreenState = ScreenState.INITIAL,
	val selectedDifficulty: GameDifficulty = GameDifficulty.Easy,
	val selectedGridSize: SudokuGridSize = SudokuGridSize.FOUR,
	val savedGame: Game? = null,
	val hasActiveGame: Boolean = false,
	val activeGameCardExpanded: Boolean = false,
	val advancedOptionsState: AdvancedOptionsState = AdvancedOptionsState(),
)

@Immutable
internal data class AdvancedOptionsState(
	val expanded: Boolean = false,
	val seedInput: String = "",
	val parsedSeed: Long? = null,
)

@Stable
internal enum class ScreenState {
	INITIAL,
	LOADING,
}

internal class SudokuCreatorViewModel(
	private val args: PuzzlePreset? = null,
	private val createNewGameUseCase: CreateNewGameUseCase,
	private val getSavedGameUseCase: GetSavedGameUseCase,
	private val saveGameUseCase: SaveGameUseCase,
	private val hasActiveGameUseCase: HasActiveGameUseCase,
	private val validateSeedInputUseCase: ValidateSeedInputUseCase,
) : ViewModel() {
	private val _uiState = MutableStateFlow(SudokuCreatorUiState())
	val uiState: StateFlow<SudokuCreatorUiState> = _uiState.asStateFlow()

	init {
		viewModelScope.launch {
			getSavedGameUseCase().collect { savedGame ->
				_uiState.update {
					it.copy(
						savedGame = savedGame,
					)
				}
			}
		}
		viewModelScope.launch {
			hasActiveGameUseCase().collect { hasActiveGame ->
				_uiState.update {
					it.copy(
						hasActiveGame = hasActiveGame,
					)
				}
			}
		}
		args?.let { parameters ->
			_uiState.update {
				it.copy(
					selectedDifficulty = parameters.difficulty,
					selectedGridSize = parameters.gridSize,
					advancedOptionsState = it.advancedOptionsState.copy(
						expanded = true,
						seedInput = parameters.seed.toString(),
						parsedSeed = parameters.seed,
					),
				)
			}
		}
	}

	sealed interface Event {

		data class ChangeDifficulty(val difficulty: GameDifficulty) : Event

		data class ChangeGridSize(val size: SudokuGridSize) : Event

		data object NewGame : Event

		data object LoadSudoku : Event

		data object ToggleActiveGameCard : Event

		data object ToggleAdvancedOptions : Event
		data class ChangePuzzleSeed(val seed: String) : Event
	}

	fun onEvent(event: Event) {
		when (event) {
			is Event.ChangeDifficulty -> handleChangeDifficulty(event.difficulty)
			is Event.ChangeGridSize -> handleChangeGridSize(event.size)
			is Event.NewGame -> handleNewGame()
			is Event.LoadSudoku -> handleLoadGame()
			is Event.ToggleActiveGameCard -> toggleActiveGameCard()
			is Event.ToggleAdvancedOptions -> toggleAdvancedOptions()
			is Event.ChangePuzzleSeed -> handleChangePuzzleSeed(event.seed)
		}
	}

	private fun toggleActiveGameCard() {
		_uiState.update {
			it.copy(
				activeGameCardExpanded = !it.activeGameCardExpanded,
			)
		}
	}

	private fun handleChangeGridSize(size: SudokuGridSize) {
		viewModelScope.launch {
			_uiState.update {
				it.copy(
					selectedGridSize = size,
				)
			}
		}
	}

	private fun handleChangeDifficulty(difficulty: GameDifficulty) {
		viewModelScope.launch {
			_uiState.update {
				it.copy(
					selectedDifficulty = difficulty,
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
					_uiState.value.advancedOptionsState.parsedSeed,
				)
			saveGameUseCase(newGame)

			_uiState.update {
				it.copy(
					loadingState = ScreenState.INITIAL,
				)
			}
		}
	}

	private fun handleLoadGame() {
		viewModelScope.launch {
		}
	}

	private fun toggleAdvancedOptions() {
		_uiState.updateAdvancedOptions {
			it.copy(expanded = !it.expanded)
		}
	}

	private fun handleChangePuzzleSeed(input: String) {
		viewModelScope.launch {
			val result = validateSeedInputUseCase(
				currentText = uiState.value.advancedOptionsState.seedInput,
				newText = input,
			)
			_uiState.updateAdvancedOptions {
				it.copy(
					seedInput = result.seedText,
					parsedSeed = result.parsedSeed,
				)
			}
		}
	}
}

private inline fun MutableStateFlow<SudokuCreatorUiState>.updateAdvancedOptions(
	function: (AdvancedOptionsState) -> AdvancedOptionsState,
) {
	update { state ->
		state.copy(
			advancedOptionsState = function(state.advancedOptionsState),
		)
	}
}
