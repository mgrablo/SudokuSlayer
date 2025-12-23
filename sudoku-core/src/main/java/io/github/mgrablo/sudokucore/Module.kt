package io.github.mgrablo.sudokucore

import io.github.mgrablo.sudokucore.hints.HintProvider
import org.koin.dsl.module

val sudokuModule =
	module {
		single { HintProvider() }
	}
