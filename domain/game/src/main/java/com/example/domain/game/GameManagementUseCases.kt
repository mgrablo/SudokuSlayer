package com.example.domain.game

import com.example.domain.game.usecases.AutoClearNotesUseCase
import com.example.domain.game.usecases.ClearActiveGameUseCase
import com.example.domain.game.usecases.FocusOnHintCellsUseCase
import com.example.domain.game.usecases.GenerateHintLogUseCase
import com.example.domain.game.usecases.GetGameUseCase
import com.example.domain.game.usecases.InputNumberUseCase
import com.example.domain.game.usecases.ProvideHintUseCase
import com.example.domain.game.usecases.ResetGameUseCase
import com.example.domain.game.usecases.RevealHintOnGridUseCase
import com.example.domain.game.usecases.RevealLastHintLogUseCase
import com.example.domain.game.usecases.SaveGameUseCase
import com.example.domain.game.usecases.SelectCellUseCase

data class GameManagementUseCases(
	val getGame: GetGameUseCase,
	val saveGame: SaveGameUseCase,
	val selectCell: SelectCellUseCase,
	val inputNumber: InputNumberUseCase,
	val resetGame: ResetGameUseCase,
	val clearActiveGame: ClearActiveGameUseCase,
	val autoClearNotes: AutoClearNotesUseCase,
)

data class HintUseCases(
	val provideHint: ProvideHintUseCase,
	val focusOnCells: FocusOnHintCellsUseCase,
	val revealOnGrid: RevealHintOnGridUseCase,
	val revealLastLog: RevealLastHintLogUseCase,
	val generateLog: GenerateHintLogUseCase,
)
