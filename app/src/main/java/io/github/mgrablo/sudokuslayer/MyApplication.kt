package io.github.mgrablo.sudokuslayer

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.LocaleListCompat
import io.github.mgrablo.sudokuslayer.domain.settings.SettingsRepository
import io.github.mgrablo.sudokuslayer.domain.settings.models.Language
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.koin.android.ext.android.inject
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin

class MyApplication : Application() {
	override fun onCreate() {
		super.onCreate()

		startKoin {
			modules(appModule)
			androidContext(this@MyApplication)
			androidLogger()
		}

		val settingsRepository: SettingsRepository by inject()
		runBlocking {
			val language = settingsRepository.language.first()
			val tag = if (language == Language.SYSTEM) "" else language.tag
			val localeList = if (tag.isEmpty()) {
				LocaleListCompat.getEmptyLocaleList()
			} else {
				LocaleListCompat.forLanguageTags(tag)
			}

			AppCompatDelegate.setApplicationLocales(localeList)
		}
	}
}
