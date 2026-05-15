package io.github.mgrablo.sudokuslayer.domain.settings.models

enum class Language(val tag: String) {
	SYSTEM("system"),
	ENGLISH("en"),
	POLISH("pl");

	companion object {
		fun fromTag(tag: String): Language {
			return entries.find { it.tag == tag } ?: SYSTEM
		}

		fun getAvailableLanguages(): List<Language> = entries
	}
}

