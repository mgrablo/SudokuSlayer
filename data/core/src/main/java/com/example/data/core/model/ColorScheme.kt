package com.example.data.core.model

enum class DarkMode(
	val displayName: String,
) {
	SYSTEM("system"),
	LIGHT("light"),
	DARK("dark"),
	;

	companion object {
		fun fromName(value: String): DarkMode = entries.find { it.displayName == value.lowercase() } ?: SYSTEM
	}
}

sealed class ColorScheme(
	open val name: String,
	open val isDark: Boolean,
) {
	data class Mocha(
		override val name: String = "mocha",
		override val isDark: Boolean = true,
	) : ColorScheme(name, isDark)

	data class Macchiato(
		override val name: String = "macchiato",
		override val isDark: Boolean = true,
	) : ColorScheme(name, isDark)

	data class Latte(
		override val name: String = "latte",
		override val isDark: Boolean = false,
	) : ColorScheme(name, isDark)

	data class Frappe(
		override val name: String = "frappe",
		override val isDark: Boolean = false,
	) : ColorScheme(name, isDark)

	companion object {
		fun fromName(name: String): ColorScheme =
			when (name.lowercase()) {
				"mocha" -> Mocha()
				"macchiato" -> Macchiato()
				"latte" -> Latte()
				"frappe" -> Frappe()
				else -> Mocha()
			}

		fun getAvailableColorSchemes(): List<String> = listOf("mocha", "macchiato", "latte", "frappe")

		fun getDarkColorSchemes(): List<String> = listOf("mocha", "macchiato")

		fun getLightColorSchemes(): List<String> = listOf("latte", "frappe")
	}
}
