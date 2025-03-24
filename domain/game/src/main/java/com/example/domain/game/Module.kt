package com.example.domain.game

import com.example.domain.game.usecases.ClearHighlightedNumbersUseCase
import com.example.domain.game.usecases.ClearHighlightedRowAndColumnUseCase
import com.example.domain.game.usecases.FocusOnHintCellsUseCase
import com.example.domain.game.usecases.GenerateHintLogUseCase
import com.example.domain.game.usecases.GetElapsedTimeUseCase
import com.example.domain.game.usecases.GetGameUseCase
import com.example.domain.game.usecases.HighlightMatchingNumbersUseCase
import com.example.domain.game.usecases.HighlightRowAndColumnUseCase
import com.example.domain.game.usecases.InputNumberUseCase
import com.example.domain.game.usecases.ProvideHintUseCase
import com.example.domain.game.usecases.RedoOperationUseCase
import com.example.domain.game.usecases.ResetGameUseCase
import com.example.domain.game.usecases.RevealHintOnGridUseCase
import com.example.domain.game.usecases.RevealLastHintLogUseCase
import com.example.domain.game.usecases.SaveElapsedTimeUseCase
import com.example.domain.game.usecases.SaveGameUseCase
import com.example.domain.game.usecases.SelectCellUseCase
import com.example.domain.game.usecases.UndoOperationUseCase
import com.example.sudoku.sudokuModule
import org.koin.dsl.module

val domainGameModule =
	module {
		includes(sudokuModule)

		factory { GetGameUseCase(get()) }
		factory { GetElapsedTimeUseCase(get()) }
		factory { SaveGameUseCase(get()) }
		factory { SaveElapsedTimeUseCase(get()) }

		factory { SelectCellUseCase(get(), get(), get(), get()) }
		factory { HighlightMatchingNumbersUseCase() }
		factory { ClearHighlightedNumbersUseCase(get()) }
		factory { HighlightRowAndColumnUseCase() }
		factory { ClearHighlightedRowAndColumnUseCase() }

		factory { InputNumberUseCase() }
		factory { ResetGameUseCase(get()) }

		factory { ProvideHintUseCase(get()) }
		factory { FocusOnHintCellsUseCase() }
		factory { GenerateHintLogUseCase() }
		factory { RevealHintOnGridUseCase(get()) }
		factory { RevealLastHintLogUseCase() }

		factory { UndoOperationUseCase(get(), get()) }
		factory { RedoOperationUseCase(get(), get()) }
	}
