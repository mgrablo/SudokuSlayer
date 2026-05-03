package io.github.mgrablo.sudokucore

import io.github.mgrablo.sudokucore.hints.HintProvider
import io.github.mgrablo.sudokucore.hints.strategies.ClaimingCandidateStrategy
import io.github.mgrablo.sudokucore.hints.strategies.HiddenSingleStrategy
import io.github.mgrablo.sudokucore.hints.strategies.NakedSingleStrategy
import io.github.mgrablo.sudokucore.hints.strategies.PointingCandidateStrategy
import org.koin.dsl.module

val sudokuModule =
	module {
		single { NakedSingleStrategy() }
		single { HiddenSingleStrategy() }
		single { PointingCandidateStrategy() }
		single { ClaimingCandidateStrategy() }
		single { HintProvider(get(), get(), get(), get()) }
	}
