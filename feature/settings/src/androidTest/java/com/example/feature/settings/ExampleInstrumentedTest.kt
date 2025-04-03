package com.example.feature.settings

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.onRoot
import androidx.compose.ui.test.printToLog
import de.mannodermaus.junit5.compose.createComposeExtension
import kotlinx.collections.immutable.persistentSetOf
import org.junit.Test
import org.junit.jupiter.api.extension.RegisterExtension

class ExampleInstrumentedTest {
	@JvmField
	@RegisterExtension
	@OptIn(ExperimentalTestApi::class)
	val extension = createComposeExtension()

	@Test
	fun test() {
		println("aasdafdsghasdhgfadshgdsfjdka")
		extension.use {
			setContent {
				SettingsScreenContent(
					openDrawer = { },
					onEvent = { },
					lightColorSchemes = persistentSetOf(),
					darkColorSchemes = persistentSetOf(),
					selectedLightColorScheme = "",
					selectedDarkColorScheme = "",
					leftHandMode = false,
					actionButtonsOnTop = false,
					darkMode = "System",
					modifier = Modifier.fillMaxSize(),
				)
			}
			onNodeWithText("Settindfasdfadsgs").assertExists().assertIsDisplayed()
			onNodeWithText("adsasads").assertDoesNotExist()
			onRoot().printToLog("TAG")
		}
		extension.use {}
		println("hjdasfasfafsjdlkafsjdlafsljdkljafsl")
	}
}
