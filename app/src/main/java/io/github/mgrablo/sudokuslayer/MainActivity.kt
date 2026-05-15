package io.github.mgrablo.sudokuslayer

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.LocaleListCompat
import androidx.core.view.WindowCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import io.github.mgrablo.sudokuslayer.domain.settings.SettingsRepository
import io.github.mgrablo.sudokuslayer.domain.settings.models.Language
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject

class MainActivity : AppCompatActivity() {

	private val settingsRepository: SettingsRepository by inject()

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		enableEdgeToEdge()
		WindowCompat.setDecorFitsSystemWindows(window, false)

		lifecycleScope.launch {
			repeatOnLifecycle(Lifecycle.State.STARTED) {
				settingsRepository.language.collect { language ->
					val tag = if (language == Language.SYSTEM) "" else language.tag
					val localeList = if (tag.isEmpty()) {
						LocaleListCompat.getEmptyLocaleList()
					} else {
						LocaleListCompat.forLanguageTags(tag)
					}

					if (AppCompatDelegate.getApplicationLocales() != localeList) {
						AppCompatDelegate.setApplicationLocales(localeList)
					}
				}
			}
		}

		setContent {
			AppContent()
		}
	}
}
