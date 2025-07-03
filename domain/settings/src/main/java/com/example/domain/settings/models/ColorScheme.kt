package com.example.domain.settings.models

import com.example.domain.settings.models.DarkMode.entries
import kotlinx.collections.immutable.toPersistentSet

enum class DarkMode(val displayName: String) {
	SYSTEM("system"),
	LIGHT("light"),
	DARK("dark"),
	;

	companion object {
		fun fromName(value: String): DarkMode =
			entries.find { it.displayName == value.lowercase() } ?: SYSTEM

		fun all() = entries.toPersistentSet()
	}
}

sealed class ColorScheme(open val name: String, open val isDark: Boolean) {
	data class Mocha(override val name: String = "mocha", override val isDark: Boolean = true) :
		ColorScheme(name, isDark)

	data class Macchiato(
		override val name: String = "macchiato",
		override val isDark: Boolean = true,
	) : ColorScheme(name, isDark)

	data class Latte(override val name: String = "latte", override val isDark: Boolean = false) :
		ColorScheme(name, isDark)

	data class Frappe(override val name: String = "frappe", override val isDark: Boolean = false) :
		ColorScheme(name, isDark)

	companion object {
		fun fromName(name: String): ColorScheme = when (name.lowercase()) {
			"mocha" -> Mocha()
			"macchiato" -> Macchiato()
			"latte" -> Latte()
			"frappe" -> Frappe()
			else -> Mocha()
		}

		fun getAvailableColorSchemes(): List<ColorScheme> = listOf(
			Mocha(),
			Macchiato(),
			Latte(),
			Frappe(),
		)

		fun getDarkColorSchemes(): List<ColorScheme> = getAvailableColorSchemes().filter { it.isDark }

		fun getLightColorSchemes(): List<ColorScheme> = getAvailableColorSchemes().filter { !it.isDark }
	}
}
