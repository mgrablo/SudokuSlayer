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
		data class UpdateSorting(val column: StatisticsColumn) : Event
	}

	fun onEvent(event: Event) {
		when (event) {
			is Event.ToggleColumnVisibility -> toggleColumnVisibility(event.column)
			is Event.UpdateSorting -> updateSorting(event.column)
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

	private suspend fun sortResults(sortState: SortState): List<GameResult> {
		if (sortState.column == null || sortState.direction == SortDirection.NONE) {
			return statisticsRepository.getAllGameResults()
		}
		TODO()
	}

	private fun updateSorting(column: StatisticsColumn) {
		viewModelScope.launch {
			_uiState.update { uiState ->
				val sortState = when {
					uiState.sortState.column != column -> SortState(column, SortDirection.ASC)
					uiState.sortState.direction == SortDirection.ASC -> SortState(
						column,
						SortDirection.DESC,
					)
					else -> SortState(null, SortDirection.NONE)
				}
				uiState.copy(sortState = sortState)
			}
		}
	}
}
