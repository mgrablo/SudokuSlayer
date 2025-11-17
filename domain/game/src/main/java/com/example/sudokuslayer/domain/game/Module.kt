package com.example.sudokuslayer.domain.game

import com.example.sudoku.sudokuModule
import com.example.sudokuslayer.domain.game.usecases.game.AutoClearNotesUseCase
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
import com.example.sudokuslayer.domain.game.usecases.input.InputNumberUseCase
import com.example.sudokuslayer.domain.game.usecases.input.RecordUndoOperationUseCase
import com.example.sudokuslayer.domain.game.usecases.input.RedoOperationUseCase
import com.example.sudokuslayer.domain.game.usecases.input.SelectCellUseCase
import com.example.sudokuslayer.domain.game.usecases.input.UndoOperationUseCase
import com.example.sudokuslayer.domain.game.usecases.time.GetBestTimeUseCase
import com.example.sudokuslayer.domain.game.usecases.time.GetElapsedTimeUseCase
import com.example.sudokuslayer.domain.game.usecases.time.SaveElapsedTimeUseCase
import com.example.sudokuslayer.domain.game.usecases.visuals.ClearHighlightedRowAndColumnUseCase
import com.example.sudokuslayer.domain.game.usecases.visuals.ClearRuleBreakingCellsUseCase
import com.example.sudokuslayer.domain.game.usecases.visuals.HighlightMatchingNumbersUseCase
import com.example.sudokuslayer.domain.game.usecases.visuals.HighlightRowAndColumnUseCase
import com.example.sudokuslayer.domain.game.usecases.visuals.MarkRuleBreakingCellsUseCase
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
		factory {
			MarkRuleBreakingCellsUseCase(
				settingsRepository = get(),
				clearRuleBreakingCellsUseCase = get(),
			)
		}
		factory { ClearRuleBreakingCellsUseCase() }
		factory { FindMistakesUseCase() }

		factory { InputNumberUseCase(get()) }
		factory { ResetGameUseCase(get()) }
		factory { AutoClearNotesUseCase(get()) }
		factory {
			CalculateGridChangesUseCase(
				inputNumber = get(),
				highlightMatching = get(),
				autoClearNotes = get(),
			)
		}

		factory { ProvideHintUseCase(get()) }
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
		factory { RecordUndoOperationUseCase(get()) }

		factory {
			HintUseCases(
				provideHint = get(),
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
				resetGame = get(),
				clearActiveGame = get(),
				calculateGridChanges = get(),
				findMistakes = get(),
			)
		}
	}
