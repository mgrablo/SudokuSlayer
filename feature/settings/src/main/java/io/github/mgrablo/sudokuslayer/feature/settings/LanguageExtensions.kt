package io.github.mgrablo.sudokuslayer.feature.settings

import androidx.annotation.StringRes
import io.github.mgrablo.sudokuslayer.domain.settings.models.Language

@StringRes
fun Language.getTitleRes(): Int {
	return when (this) {
		Language.SYSTEM -> R.string.language_system
		Language.ENGLISH -> R.string.language_english
		Language.POLISH -> R.string.language_polish
	}
}

