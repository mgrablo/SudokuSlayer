package com.example.feature.statistics

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.domain.core.GameDifficulty
import com.example.domain.core.GameResult
import com.example.domain.core.SudokuGridSize
import com.example.domain.statistics.GameResultFilter
import com.example.domain.statistics.StatisticsRepository
import kotlinx.collections.immutable.PersistentList
import kotlinx.collections.immutable.PersistentSet
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toPersistentList
import kotlinx.collections.immutable.toPersistentSet
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

internal enum class StatisticsColumn {
	Date,
	Difficulty,
	Size,
	Time,
	HintsUsed,
	;

	fun getDisplayText(): String = when (this) {
		Date -> "Date"
		Difficulty -> "Difficulty"
		Size -> "Size"
		Time -> "Time"
		HintsUsed -> "Hints"
	}
}

internal data class SortState(
	val column: StatisticsColumn? = null,
	val direction: SortDirection = SortDirection.NONE,
)

internal enum class SortDirection { NONE, ASC, DESC }
internal data class InsightsUiState(
	val sortState: SortState,
	val gameResults: PersistentList<GameResult> = persistentListOf(),
	val isLoading: Boolean = false,
	val totalGamesPlayed: Long = 0,
	val totalTimeSpent: Long = 0,
)

internal data class FilterUiState(
	val isSolveTimeRangeEnabled: Boolean = false,
	val isHintsUsedRangeEnabled: Boolean = false,
	val maxHintsUsed: Int = 0,
	val longestGame: Long = 0,
)

internal class StatisticsViewModel(private val statisticsRepository: StatisticsRepository) :
	ViewModel() {
	private val _insightsUiState = MutableStateFlow(
		InsightsUiState(
			sortState = SortState(null, SortDirection.NONE),
		),
	)
	val insightsUiState: StateFlow<InsightsUiState> = _insightsUiState

	private val _filterUiState = MutableStateFlow(FilterUiState())
	val filterUiState: StateFlow<FilterUiState> = _filterUiState

	private val _visibleColumns: MutableStateFlow<PersistentSet<StatisticsColumn>> =
		MutableStateFlow(StatisticsColumn.entries.toPersistentSet())
	val visibleColumns = _visibleColumns.asStateFlow()

	private val _gameResultFilter: MutableStateFlow<GameResultFilter> = MutableStateFlow(
		GameResultFilter(),
	)
	val gameResultFilter = _gameResultFilter.asStateFlow()

	init {
		loadInitialData()

		viewModelScope.launch {
			_gameResultFilter
				.collect {
					updateGameResults()
				}
		}
	}

	sealed interface StatisticsEvent {
		data class ToggleColumnVisibility(val column: StatisticsColumn) : StatisticsEvent
		data class ColumnHeaderClicked(val column: StatisticsColumn) : StatisticsEvent
		data class ToggleDifficultyFilter(val difficulty: GameDifficulty) : StatisticsEvent
		data class ToggleGridSizeFilter(val gridSize: SudokuGridSize) : StatisticsEvent
		data class SetHintsUsedRangeFilter(val min: Int?, val max: Int?) : StatisticsEvent
		data class SetSolveTimeRangeFilter(val min: Long?, val max: Long?) : StatisticsEvent
		data class UpdateHintsUsedRangeEnabled(val value: Boolean) : StatisticsEvent
		data class UpdateSolveTimeRangeEnabled(val value: Boolean) : StatisticsEvent
	}

	fun onEvent(event: StatisticsEvent) {
		when (event) {
			is StatisticsEvent.ToggleColumnVisibility -> toggleColumnVisibility(event.column)
			is StatisticsEvent.ColumnHeaderClicked -> handleColumnHeaderClick(event.column)
			is StatisticsEvent.ToggleDifficultyFilter -> toggleDifficultyFilter(event.difficulty)
			is StatisticsEvent.ToggleGridSizeFilter -> toggleGridSizeFilter(event.gridSize)
			is StatisticsEvent.SetHintsUsedRangeFilter -> setHintsUsedRangeFilter(
				event.min,
				event.max,
			)

			is StatisticsEvent.SetSolveTimeRangeFilter -> setSolveTimeRangeFilter(
				event.min,
				event.max,
			)

			is StatisticsEvent.UpdateHintsUsedRangeEnabled -> updateHintsUsedRangeEnabled(event.value)
			is StatisticsEvent.UpdateSolveTimeRangeEnabled -> updateSolveTimeRangeEnabled(event.value)
		}
	}

	private fun loadInitialData() {
		viewModelScope.launch {
			_insightsUiState.update { it.copy(isLoading = true) }

			val results = statisticsRepository.getAllGameResults().toPersistentList()
			val totalGamesPlayed = statisticsRepository.getTotalGameResults()
			val totalTimeSpent = statisticsRepository.getTotalTimeSpent()
			val maxHintsUsed = results.maxOfOrNull { it.hintsUsed } ?: 0
			val longestGame = results.maxOfOrNull { it.timeInSeconds } ?: 0L

			_insightsUiState.update { state ->
				state.copy(
					gameResults = results,
					totalGamesPlayed = totalGamesPlayed,
					totalTimeSpent = totalTimeSpent,
					isLoading = false,
				)
			}

			_filterUiState.update { state ->
				state.copy(
					longestGame = longestGame,
					maxHintsUsed = maxHintsUsed,
				)
			}
		}
	}

	private fun updateGameResults() {
		viewModelScope.launch {
			_insightsUiState.update {
				it.copy(isLoading = true)
			}
			val currentFilter = _gameResultFilter.value
			val currentSortState = _insightsUiState.value.sortState

			val results =
				applySorting(
					statisticsRepository.getFilteredGameResults(filter = currentFilter),
					currentSortState,
				).toPersistentList()

			_insightsUiState.update { it.copy(gameResults = results, isLoading = false) }
		}
	}

	private fun toggleColumnVisibility(column: StatisticsColumn) {
		_visibleColumns.update {
			it.toggleItem(column).toPersistentSet()
		}
	}

	private fun toggleDifficultyFilter(difficulty: GameDifficulty) {
		_gameResultFilter.update {
			it.copy(
				difficulties = it.difficulties.toggleItem(difficulty),
			)
		}
	}

	private fun toggleGridSizeFilter(size: SudokuGridSize) {
		_gameResultFilter.update {
			it.copy(
				gridSizes = it.gridSizes.toggleItem(size),
			)
		}
	}

	private fun applySorting(results: List<GameResult>, sortState: SortState): List<GameResult> {
		if (sortState.column == null || sortState.direction == SortDirection.NONE) {
			return results
		}

		val sortedResults = when (sortState.column) {
			StatisticsColumn.Date -> results.sortedBy { it.completionDate }
			StatisticsColumn.Difficulty -> results.sortedBy { it.difficulty }
			StatisticsColumn.Size -> results.sortedBy { it.gridSize }
			StatisticsColumn.Time -> results.sortedBy { it.timeInSeconds }
			StatisticsColumn.HintsUsed -> results.sortedBy { it.hintsUsed }
		}

		return if (sortState.direction == SortDirection.ASC) sortedResults else sortedResults.reversed()
	}

	private fun handleColumnHeaderClick(column: StatisticsColumn) {
		viewModelScope.launch {
			val currentSortState = _insightsUiState.value.sortState
			val newSortState = when {
				currentSortState.column != column -> SortState(column, SortDirection.ASC)
				currentSortState.direction == SortDirection.ASC -> SortState(
					column,
					SortDirection.DESC,
				)

				else -> SortState(null, SortDirection.NONE)
			}

			val currentResults = _insightsUiState.value.gameResults
			val sortedResults = applySorting(
				results = currentResults,
				sortState = newSortState,
			).toPersistentList()

			_insightsUiState.update { uiState ->
				uiState.copy(
					sortState = newSortState,
					gameResults = sortedResults,
				)
			}
		}
	}

	private fun updateHintsUsedRangeEnabled(value: Boolean) {
		_filterUiState.update { state ->
			state.copy(
				isHintsUsedRangeEnabled = value,
			)
		}
	}

	private fun updateSolveTimeRangeEnabled(value: Boolean) {
		_filterUiState.update { state ->
			state.copy(
				isSolveTimeRangeEnabled = value,
			)
		}
	}

	private fun setHintsUsedRangeFilter(min: Int?, max: Int?) {
		_gameResultFilter.update {
			it.copy(
				minHintsUsed = if (min == max) null else min,
				maxHintsUsed = max,
			)
		}
	}

	private fun setSolveTimeRangeFilter(min: Long?, max: Long?) {
		_gameResultFilter.update {
			it.copy(
				minCompletionTime = if (min == max) null else min,
				maxCompletionTime = max,
			)
		}
	}
}

private fun <T> Set<T>.toggleItem(item: T): Set<T> = if (item in this) {
	this - item
} else {
	this + item
}
