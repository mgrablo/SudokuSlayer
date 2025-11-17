package com.example.sudokuslayer.feature.game.components.snackbar

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarData
import androidx.compose.material3.SnackbarDefaults
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarVisuals
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.dropShadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.shadow.Shadow
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.dp
import com.example.sudokuslayer.feature.game.theme.SudokuGameTheme
import com.example.sudokuslayer.feature.uicore.theme.LocalPadding

@Composable
internal fun GameSnackbar(
	snackbarData: SnackbarData,
	modifier: Modifier = Modifier,
	containerColor: Color = MaterialTheme.colorScheme.surfaceContainerHigh,
	contentColor: Color = MaterialTheme.colorScheme.onSurface,
	actionContentColor: Color = MaterialTheme.colorScheme.primary,
) {
	Row(
		verticalAlignment = Alignment.CenterVertically,
		modifier = modifier
			.padding(LocalPadding.current.small)
			.fillMaxWidth()
			.height(48.dp)
			.dropShadow(shape = SnackbarDefaults.shape, shadow = Shadow(8.dp, alpha = 0.4f))
			.background(containerColor, shape = SnackbarDefaults.shape)
			.padding(horizontal = 16.dp),
	) {
		Text(snackbarData.visuals.message, color = contentColor)
		if (snackbarData.visuals.actionLabel != null) {
			Spacer(Modifier.weight(1f))
			TextButton(
				onClick = { snackbarData.performAction() },
				content = { Text(snackbarData.visuals.actionLabel!!) },
				shape = SnackbarDefaults.shape,
				colors = ButtonColors(
					contentColor = actionContentColor,
					containerColor = Color.Transparent,
					disabledContentColor = Color.Transparent,
					disabledContainerColor = Color.Transparent,
				),
				contentPadding = PaddingValues(0.dp),
			)
		}
	}
}

@PreviewLightDark
@Composable
private fun GameSnackbarPreview(
	@PreviewParameter(ActionLabelPreviewParameterProvider::class) actionLabel: String?,
) {
	val snackbarData = object : SnackbarData {
		override val visuals = object : SnackbarVisuals {
			override val message = "No mistakes found!"
			override val actionLabel: String? = actionLabel
			override val withDismissAction: Boolean = false
			override val duration: SnackbarDuration = SnackbarDuration.Short
		}

		override fun performAction() {}
		override fun dismiss() {}
	}
	SudokuGameTheme {
		Column(
			modifier = Modifier.background(MaterialTheme.colorScheme.background),
		) {
			GameSnackbar(snackbarData)
		}
	}
}

private class ActionLabelPreviewParameterProvider : PreviewParameterProvider<String?> {
	override val values: Sequence<String?> = sequenceOf(null, "Show")
}
