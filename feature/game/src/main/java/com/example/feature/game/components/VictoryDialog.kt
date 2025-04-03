package com.example.feature.game.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.compose.ui.window.SecureFlagPolicy

@Composable
fun VictoryDialog(isVisible: Boolean, onDismissRequest: () -> Unit) {
	if (isVisible) {
		Dialog(
			onDismissRequest = onDismissRequest,
			properties =
			DialogProperties(
				dismissOnBackPress = true,
				dismissOnClickOutside = true,
				securePolicy = SecureFlagPolicy.Inherit,
			),
		) {
			Card(
				modifier =
				Modifier
					.fillMaxWidth()
					.height(200.dp)
					.padding(16.dp),
				shape = RoundedCornerShape(16.dp),
				colors =
				CardDefaults.cardColors(
					containerColor = MaterialTheme.colorScheme.surface,
				),
				elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
			) {
				Column(
					modifier = Modifier.fillMaxSize(),
					verticalArrangement = Arrangement.Center,
					horizontalAlignment = Alignment.CenterHorizontally,
				) {
					Text("Victory!!!")
				}
			}
		}
	}
}
