package com.example.sudokuslayer

import org.junit.jupiter.api.Test
import org.koin.core.annotation.KoinExperimentalAPI
import org.koin.test.KoinTest
import org.koin.test.verify.definition
import org.koin.test.verify.injectedParameters
import org.koin.test.verify.verify

class CheckModulesTest : KoinTest {
	@OptIn(KoinExperimentalAPI::class)
	@Test
	fun checkAllModules() {
		appModule.verify(
			injections = injectedParameters(
				definition<com.example.data.core.database.AndroidDatabaseDriverFactory>(
					android.content.Context::class,
				),
			),
		)
	}
}
