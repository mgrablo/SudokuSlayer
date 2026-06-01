package io.github.mgrablo.sudokuslayer.feature.game.hints

import android.content.Context
import io.github.mgrablo.sudokucore.hints.Hint

internal interface HintExplainer<in T : Hint> {
	fun explain(context: Context, hint: T): HintExplanation
}
