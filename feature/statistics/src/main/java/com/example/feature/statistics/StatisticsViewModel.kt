package com.example.feature.statistics

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.domain.core.GameResult
import com.example.domain.statistics.StatisticsRepository
import kotlinx.collections.immutable.PersistentList
import kotlinx.collections.immutable.PersistentSet
import kotlinx.collections.immutable.minus
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.plus
import kotlinx.collections.immutable.toPersistentList
import kotlinx.collections.immutable.toPersistentSet
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
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
internal data class StatisticsUiState(
	val columnsToShow: PersistentSet<StatisticsColumn>,
	val sortState: SortState,
	val gameResults: PersistentList<GameResult> = persistentListOf(),
	val isLoading: Boolean = false,
	val totalGamesPlayed: Long = 0,
	val totalTimeSpent: Long = 0,
)

internal class StatisticsViewModel(private val statisticsRepository: StatisticsRepository) :
	ViewModel() {
	private val _uiState = MutableStateFlow(
		StatisticsUiState(
			columnsToShow = StatisticsColumn.entries.toPersistentSet(),
			sortState = SortState(null, SortDirection.NONE),
		),
	)
	val uiState: StateFlow<StatisticsUiState> = _uiState

	init {
		loadGameResults()
	}

	sealed interface Event {
		data class ToggleColumnVisibility(val column: StatisticsColumn) : Event
		data class ColumnHeaderClicked(val column: StatisticsColumn) : Event
	}

	fun onEvent(event: Event) {
		when (event) {
			is Event.ToggleColumnVisibility -> toggleColumnVisibility(event.column)
			is Event.ColumnHeaderClicked -> handleColumnHeaderClick(event.column)
		}
	}

	private fun loadGameResults() {
		viewModelScope.launch {
			_uiState.update { it.copy(isLoading = true) }

			val results = statisticsRepository.getAllGameResults().toPersistentList()
			val totalGamesPlayed = statisticsRepository.getTotalGameResults()
			val totalTimeSpent = statisticsRepository.getTotalTimeSpent()

			_uiState.update { state ->
				state.copy(
					gameResults = results,
					totalGamesPlayed = totalGamesPlayed,
					totalTimeSpent = totalTimeSpent,
					isLoading = false,
				)
			}
		}
	}

	private fun toggleColumnVisibility(column: StatisticsColumn) {
		viewModelScope.launch {
			_uiState.update { uiState ->
				uiState.copy(
					columnsToShow = if (column in uiState.columnsToShow) {
						uiState.columnsToShow - column
					} else {
						uiState.columnsToShow + column
					},
				)
			}
		}
	}

	private fun applySorting(results: List<GameResult>, sortState: SortState): List<GameResult> {
		if (sortState.column == null || sortState.direction == SortDirection.NONE) {
			return results
		}

		val sorted = when (sortState.column) {
			StatisticsColumn.Date -> results.sortedBy { it.completedAt }
			StatisticsColumn.Difficulty -> results.sortedBy { it.difficulty }
			StatisticsColumn.Size -> results.sortedBy { it.gridSize }
			StatisticsColumn.Time -> results.sortedBy { it.timeInSeconds }
			StatisticsColumn.HintsUsed -> results.sortedBy { it.hintsUsed }
		}

		return if (sortState.direction == SortDirection.ASC) sorted else sorted.reversed()
	}

	private fun handleColumnHeaderClick(column: StatisticsColumn) {
		viewModelScope.launch {
			val currentSortState = _uiState.value.sortState
			val newSortState = when {
				currentSortState.column != column -> SortState(column, SortDirection.ASC)
				currentSortState.direction == SortDirection.ASC -> SortState(
					column,
					SortDirection.DESC,
				)
				else -> SortState(null, SortDirection.NONE)
			}

			val currentResults = _uiState.value.gameResults
			val sortedResults = applySorting(
				results = currentResults,
				sortState = newSortState,
			).toPersistentList()

			_uiState.update { uiState ->
				uiState.copy(
					sortState = newSortState,
					gameResults = sortedResults,
				)
			}
		}
	}
}
