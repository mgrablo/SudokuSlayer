package com.example.domain.game

import com.example.domain.game.usecases.game.AutoClearNotesUseCase
import com.example.domain.game.usecases.game.ClearActiveGameUseCase
import com.example.domain.game.usecases.game.GetGameUseCase
import com.example.domain.game.usecases.game.ResetGameUseCase
import com.example.domain.game.usecases.game.SaveGameUseCase
import com.example.domain.game.usecases.hint.FocusOnHintCellsUseCase
import com.example.domain.game.usecases.hint.GenerateHintLogUseCase
import com.example.domain.game.usecases.hint.ProvideHintUseCase
import com.example.domain.game.usecases.hint.RevealHintOnGridUseCase
import com.example.domain.game.usecases.hint.RevealLastHintLogUseCase
import com.example.domain.game.usecases.input.InputNumberUseCase
import com.example.domain.game.usecases.input.RedoOperationUseCase
import com.example.domain.game.usecases.input.SelectCellUseCase
import com.example.domain.game.usecases.input.UndoOperationUseCase
import com.example.domain.game.usecases.time.GetBestTimeUseCase
import com.example.domain.game.usecases.time.GetElapsedTimeUseCase
import com.example.domain.game.usecases.time.SaveElapsedTimeUseCase
import com.example.domain.game.usecases.visuals.ClearHighlightedRowAndColumnUseCase
import com.example.domain.game.usecases.visuals.HighlightMatchingNumbersUseCase
import com.example.domain.game.usecases.visuals.HighlightRowAndColumnUseCase
import com.example.sudoku.sudokuModule
import org.koin.dsl.module

val domainGameModule =
	module {
		includes(sudokuModule)

		factory { GetGameUseCase(get()) }
		factory { GetElapsedTimeUseCase(get()) }
		factory { SaveGameUseCase(get()) }
		factory { SaveElapsedTimeUseCase(get()) }
		factory { ClearActiveGameUseCase(get()) }
		factory { GetBestTimeUseCase(get()) }

		factory {
			SelectCellUseCase(
				highlightRowAndColumnUseCase = get(),
				highlightMatchingNumbersUseCase = get(),
				clearHighlightedRowAndColumnUseCase = get(),
			)
		}
		factory { HighlightMatchingNumbersUseCase(get()) }
		factory { HighlightRowAndColumnUseCase() }
		factory { ClearHighlightedRowAndColumnUseCase() }

		factory { InputNumberUseCase() }
		factory { ResetGameUseCase(get()) }
		factory { AutoClearNotesUseCase() }

		factory { ProvideHintUseCase(get()) }
		factory { FocusOnHintCellsUseCase() }
		factory { GenerateHintLogUseCase() }
		factory { RevealHintOnGridUseCase(get()) }
		factory { RevealLastHintLogUseCase() }

		factory {
			UndoOperationUseCase(
				operationRepository = get(),
				inputNumberUseCase = get(),
				highlightMatchingNumbersUseCase = get(),
			)
		}
		factory {
			RedoOperationUseCase(
				operationRepository = get(),
				inputNumberUseCase = get(),
				highlightMatchingNumbersUseCase = get(),
			)
		}

		factory {
			HintUseCases(
				provideHint = get(),
				focusOnCells = get(),
				revealOnGrid = get(),
				revealLastLog = get(),
				generateLog = get(),
			)
		}

		factory {
			GameManagementUseCases(
				getGame = get(),
				saveGame = get(),
				selectCell = get(),
				inputNumber = get(),
				resetGame = get(),
				clearActiveGame = get(),
				autoClearNotes = get(),
				highlightMatching = get(),
			)
		}
	}
