package io.github.mgrablo.sudokuslayer.domain.game

import io.github.mgrablo.sudokucore.sudokuModule
import io.github.mgrablo.sudokuslayer.domain.game.usecases.game.AutoClearNotesUseCase
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
import io.github.mgrablo.sudokuslayer.domain.game.usecases.input.InputNumberUseCase
import io.github.mgrablo.sudokuslayer.domain.game.usecases.input.RecordUndoOperationUseCase
import io.github.mgrablo.sudokuslayer.domain.game.usecases.input.RedoOperationUseCase
import io.github.mgrablo.sudokuslayer.domain.game.usecases.input.SelectCellUseCase
import io.github.mgrablo.sudokuslayer.domain.game.usecases.input.UndoOperationUseCase
import io.github.mgrablo.sudokuslayer.domain.game.usecases.time.GetBestTimeUseCase
import io.github.mgrablo.sudokuslayer.domain.game.usecases.time.GetElapsedTimeUseCase
import io.github.mgrablo.sudokuslayer.domain.game.usecases.time.SaveElapsedTimeUseCase
import io.github.mgrablo.sudokuslayer.domain.game.usecases.visuals.ClearHighlightedRowAndColumnUseCase
import io.github.mgrablo.sudokuslayer.domain.game.usecases.visuals.ClearRuleBreakingCellsUseCase
import io.github.mgrablo.sudokuslayer.domain.game.usecases.visuals.HighlightMatchingNumbersUseCase
import io.github.mgrablo.sudokuslayer.domain.game.usecases.visuals.HighlightRowAndColumnUseCase
import io.github.mgrablo.sudokuslayer.domain.game.usecases.visuals.MarkRuleBreakingCellsUseCase
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
