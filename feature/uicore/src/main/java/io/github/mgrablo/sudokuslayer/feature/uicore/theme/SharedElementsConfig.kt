package io.github.mgrablo.sudokuslayer.feature.uicore.theme

import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.runtime.compositionLocalOf

sealed interface SharedElementKey {
	object BoardLoadingIndicator : SharedElementKey
}

@OptIn(ExperimentalSharedTransitionApi::class)
val LocalSharedTransitionScope = compositionLocalOf<SharedTransitionScope> {
	throw IllegalArgumentException("CompositionLocal No SharedTransitionScope provided")
}
