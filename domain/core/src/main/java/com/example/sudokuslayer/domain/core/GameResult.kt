package com.example.sudokuslayer.domain.core

import kotlinx.datetime.LocalDateTime
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

data class GameResult
@OptIn(ExperimentalUuidApi::class)
constructor(
	val id: String = Uuid.Companion.random().toString(),
	val timeInSeconds: Long,
	val difficulty: GameDifficulty,
	val gridSize: SudokuGridSize,
	val hintsUsed: Int,
	val completionDate: LocalDateTime,
	val seed: Long?,
)
