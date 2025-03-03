package com.example.sudokuslayer.presentation.screen.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.sudokuslayer.presentation.ui.theme.LocalPadding
import com.example.sudokuslayer.presentation.ui.theme.SudokuSlayerTheme

@Composable
fun SettingsScreen(
	openDrawer: () -> Unit,
	modifier: Modifier = Modifier,
	viewModel: SettingsViewModel = viewModel(factory = SettingsViewModel.Factory),
) {
	SettingsScreenContent(
		openDrawer = openDrawer,
	)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SettingsScreenContent(
	openDrawer: () -> Unit,
	modifier: Modifier = Modifier,
) {
	Scaffold(
		topBar = {
			CenterAlignedTopAppBar(
				title = { Text("Settings") },
				navigationIcon = {
					IconButton(onClick = openDrawer) {
						Icon(Icons.Default.Menu, contentDescription = "Open menu")
					}
				},
			)
		},
	) { paddingValues ->
		Column(
			modifier = Modifier.fillMaxSize().padding(paddingValues).padding(LocalPadding.current.normal),
		) {
			var selectedLanguage by remember { mutableStateOf("English") }
			var languageExpanded by remember { mutableStateOf(false) }
			val languages = listOf("English", "Spanish", "German", "French")
			var leftHandMode by remember { mutableStateOf(false) }

			var selectedTheme by remember { mutableStateOf("Latte (Light)") }
			var themeExpanded by remember { mutableStateOf(false) }
			val themes =
				listOf("Latte (Light)", "Frappe (Light)", "Macchiatto (Dark)", "Mocha (Dark)")

			SettingsCategory("Appearance") {
				SettingItem(
					title = "Language",
					content = {
						ExposedDropdownMenuBox(
							expanded = languageExpanded,
							onExpandedChange = { languageExpanded = it },
						) {
							OutlinedTextField(
								value = selectedLanguage,
								onValueChange = {},
								readOnly = true,
								singleLine = true,
								trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = languageExpanded) },
								modifier = Modifier
									.menuAnchor(type = MenuAnchorType.SecondaryEditable)
									.width(200.dp),
							)

							ExposedDropdownMenu(
								expanded = languageExpanded,
								onDismissRequest = { languageExpanded = false },
								containerColor = MaterialTheme.colorScheme.surface,
							) {
								languages.forEach { language ->
									DropdownMenuItem(
										text = { Text(language) },
										onClick = {
											selectedLanguage = language
											languageExpanded = false
										},
									)
								}
							}
						}
					}
				)

				SettingItem(
					title = "Theme",
					content = {
						ExposedDropdownMenuBox(
							expanded = themeExpanded,
							onExpandedChange = { themeExpanded = it },
						) {
							OutlinedTextField(
								value = selectedTheme,
								onValueChange = {},
								readOnly = true,
								singleLine = true,
								trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = themeExpanded) },
								modifier =
									Modifier
										.menuAnchor(type = MenuAnchorType.SecondaryEditable)
										.width(200.dp),
							)

							ExposedDropdownMenu(
								expanded = themeExpanded,
								onDismissRequest = { themeExpanded = false },
								containerColor = MaterialTheme.colorScheme.surface,
							) {
								themes.forEach { theme ->
									DropdownMenuItem(
										text = { Text(theme) },
										onClick = {
											selectedTheme = theme
											themeExpanded = false
										},
									)
								}
							}
						}
					},
				)

			}

			SettingsCategory("Accessibility") {
				SettingItem(
					title = "Left hand mode",
					content = {
						Switch(
							checked = leftHandMode,
							onCheckedChange = { leftHandMode = it },
						)
					},
				)
			}
		}
	}
}

@Composable
private fun SettingsCategory(
	title: String,
	content: @Composable () -> Unit,
) {
	Column(
		modifier = Modifier.fillMaxWidth().padding(bottom = LocalPadding.current.small),
		verticalArrangement = Arrangement.spacedBy(12.dp),
	) {
		Text(
			text = title,
			style = MaterialTheme.typography.titleLarge,
			color = MaterialTheme.colorScheme.primary,
		)
		content()
	}
}

@Composable
private fun SettingItem(
	title: String,
	content: @Composable () -> Unit,
) {
	Row(
		modifier = Modifier.fillMaxWidth(),
		horizontalArrangement = Arrangement.SpaceBetween,
		verticalAlignment = Alignment.CenterVertically,
	) {
		Text(
			text = title,
			style = MaterialTheme.typography.bodyLarge,
			color = MaterialTheme.colorScheme.onSurface,
		)
		content()
	}
}

@Preview
@Composable
private fun SettingsScreenPreview() {
	SudokuSlayerTheme {
		SettingsScreenContent(
			openDrawer = { },
		)
	}
}
