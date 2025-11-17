package com.example.sudokuslayer.domain.game

import com.example.sudokuslayer.domain.game.usecases.game.CalculateGridChangesUseCase
import com.example.sudokuslayer.domain.game.usecases.game.ClearActiveGameUseCase
import com.example.sudokuslayer.domain.game.usecases.game.FindMistakesUseCase
import com.example.sudokuslayer.domain.game.usecases.game.GetGameUseCase
import com.example.sudokuslayer.domain.game.usecases.game.ResetGameUseCase
import com.example.sudokuslayer.domain.game.usecases.game.SaveGameUseCase
import com.example.sudokuslayer.domain.game.usecases.hint.GenerateHintLogUseCase
import com.example.sudokuslayer.domain.game.usecases.hint.ProvideHintUseCase
import com.example.sudokuslayer.domain.game.usecases.hint.RevealHintOnGridUseCase
import com.example.sudokuslayer.domain.game.usecases.hint.RevealLastHintLogUseCase
import com.example.sudokuslayer.domain.game.usecases.input.SelectCellUseCase

data class GameManagementUseCases(
	val getGame: GetGameUseCase,
	val saveGame: SaveGameUseCase,
	val selectCell: SelectCellUseCase,
	val resetGame: ResetGameUseCase,
	val clearActiveGame: ClearActiveGameUseCase,
	val calculateGridChanges: CalculateGridChangesUseCase,
	val findMistakes: FindMistakesUseCase,
)

data class HintUseCases(
	val provideHint: ProvideHintUseCase,
	val revealOnGrid: RevealHintOnGridUseCase,
	val revealLastLog: RevealLastHintLogUseCase,
	val generateLog: GenerateHintLogUseCase,
)
