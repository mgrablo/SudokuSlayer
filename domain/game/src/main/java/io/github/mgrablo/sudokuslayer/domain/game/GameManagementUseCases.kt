package io.github.mgrablo.sudokuslayer.domain.game

import io.github.mgrablo.sudokuslayer.domain.game.usecases.game.CalculateGridChangesUseCase
import io.github.mgrablo.sudokuslayer.domain.game.usecases.game.ClearActiveGameUseCase
import io.github.mgrablo.sudokuslayer.domain.game.usecases.game.FindMistakesUseCase
import io.github.mgrablo.sudokuslayer.domain.game.usecases.game.GetGameUseCase
import io.github.mgrablo.sudokuslayer.domain.game.usecases.game.ResetGameUseCase
import io.github.mgrablo.sudokuslayer.domain.game.usecases.game.SaveGameUseCase
import io.github.mgrablo.sudokuslayer.domain.game.usecases.hint.GenerateHintLogUseCase
import io.github.mgrablo.sudokuslayer.domain.game.usecases.hint.ProvideHintUseCase
import io.github.mgrablo.sudokuslayer.domain.game.usecases.hint.RevealHintOnGridUseCase
import io.github.mgrablo.sudokuslayer.domain.game.usecases.hint.RevealLastHintLogUseCase
import io.github.mgrablo.sudokuslayer.domain.game.usecases.input.SelectCellUseCase

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
