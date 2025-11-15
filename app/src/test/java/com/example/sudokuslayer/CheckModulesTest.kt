package com.example.sudokuslayer

import android.app.Application
import android.content.Context
import com.example.domain.core.SudokuGridSize
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.koin.core.annotation.KoinExperimentalAPI
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.test.KoinTest
import org.koin.test.verify.verify

class CheckModulesTest : KoinTest {

	@BeforeEach
	fun setup() {
		stopKoin()
		startKoin {
			modules(appModule)
		}
	}

	@OptIn(KoinExperimentalAPI::class)
	@Test
	fun checkAllModules() {
		appModule.verify(
			extraTypes = listOf(Context::class, Application::class, SudokuGridSize::class),
		)
	}
}
