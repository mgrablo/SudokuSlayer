package io.github.mgrablo.sudokucore

import io.github.mgrablo.sudokucore.solver.HintProvider
import org.koin.dsl.module

val sudokuModule =
	module {
		single { HintProvider() }
	}
