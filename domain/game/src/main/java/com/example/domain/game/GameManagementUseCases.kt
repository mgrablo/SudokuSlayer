package com.example.domain.game

import com.example.domain.game.usecases.game.CalculateGridChangesUseCase
import com.example.domain.game.usecases.game.ClearActiveGameUseCase
import com.example.domain.game.usecases.game.FindMistakesUseCase
import com.example.domain.game.usecases.game.GetGameUseCase
import com.example.domain.game.usecases.game.ResetGameUseCase
import com.example.domain.game.usecases.game.SaveGameUseCase
import com.example.domain.game.usecases.hint.GenerateHintLogUseCase
import com.example.domain.game.usecases.hint.ProvideHintUseCase
import com.example.domain.game.usecases.hint.RevealHintOnGridUseCase
import com.example.domain.game.usecases.hint.RevealLastHintLogUseCase
import com.example.domain.game.usecases.input.SelectCellUseCase

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
