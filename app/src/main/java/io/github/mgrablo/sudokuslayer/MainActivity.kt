package io.github.mgrablo.sudokuslayer

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.LocaleListCompat
import androidx.core.view.WindowCompat
import androidx.lifecycle.lifecycleScope
import io.github.mgrablo.sudokuslayer.domain.settings.SettingsRepository
import io.github.mgrablo.sudokuslayer.domain.settings.models.Language
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.koin.android.ext.android.inject

class MainActivity : AppCompatActivity() {

	val settingsRepository: SettingsRepository by inject()

	override fun onCreate(savedInstanceState: Bundle?) {
		runBlocking {
			val storedLanguage = settingsRepository.language.first()
			val storedTag = storedLanguage.tag

			if (storedTag.isNotEmpty()) {
				val locale = LocaleListCompat.forLanguageTags(storedTag)
				AppCompatDelegate.setApplicationLocales(locale)
			}
		}

		super.onCreate(savedInstanceState)
		enableEdgeToEdge()
		WindowCompat.setDecorFitsSystemWindows(window, false)

		val systemLocales = AppCompatDelegate.getApplicationLocales()
		val systemTag = systemLocales.toLanguageTags()
		lifecycleScope.launch {
			val storedLanguage = settingsRepository.language.first()
			val storedTag = storedLanguage.tag

			// If the system value is different from stored value,
			// it means user changed language from system app settings,
			// so update value in datastore
			if (systemTag != storedTag) {
				val newLang = Language.fromTag(systemTag)
				settingsRepository.setLanguage(newLang)
			}
		}

		setContent {
			AppContent()
		}
	}
}
