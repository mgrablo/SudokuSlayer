package io.github.mgrablo.sudokuslayer.domain.settings.models

enum class Language(val tag: String) {
	SYSTEM("system"),
	ENGLISH("en"),
	POLISH("pl"),
	;

	companion object {
		fun fromTag(tag: String): Language {
			if (tag.isEmpty()) return SYSTEM

			return entries.find { it.tag == tag }
				?: entries.find { it.tag == tag.split("-")[0] }
				?: SYSTEM
		}

		fun getAvailableLanguages(): List<Language> = entries
	}
}
