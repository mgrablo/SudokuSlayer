package com.example.domain.statistics

import com.example.domain.core.GameDifficulty
import com.example.domain.core.SudokuGridSize
import kotlinx.datetime.LocalDateTime
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

data class FinishedGame
@OptIn(ExperimentalUuidApi::class)
constructor(
	val id: String = Uuid.random().toString(),
	val timeInSeconds: Long,
	val difficulty: GameDifficulty,
	val gridSize: SudokuGridSize,
	val hintsUsed: Int,
	val completedAt: LocalDateTime,
)
