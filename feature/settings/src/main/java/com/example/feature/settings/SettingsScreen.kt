package com.example.feature.settings

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.displayCutoutPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.PreviewScreenSizes
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.feature.settings.components.SettingDropDownMenu
import com.example.feature.settings.components.SettingSwitchItem
import com.example.feature.settings.components.SettingsCategory
import com.example.feature.uicore.theme.LocalPadding
import com.example.feature.uicore.theme.SudokuSlayerTheme
import kotlinx.collections.immutable.PersistentSet
import kotlinx.collections.immutable.persistentSetOf
import org.koin.androidx.compose.koinViewModel

@Composable
internal fun SettingsScreen(
	openDrawer: () -> Unit,
	modifier: Modifier = Modifier,
	viewModel: SettingsViewModel = koinViewModel<SettingsViewModel>(),
) {
	val darkMode by viewModel.darkMode.collectAsStateWithLifecycle()
	val lightColorScheme by viewModel.lightColorScheme.collectAsStateWithLifecycle()
	val darkColorScheme by viewModel.darkColorScheme.collectAsStateWithLifecycle()
	val leftHandMode by viewModel.leftHandMode.collectAsStateWithLifecycle()
	val actionButtonsOnTop by viewModel.actionButtonsOnTop.collectAsStateWithLifecycle()

	SettingsScreenContent(
		openDrawer = openDrawer,
		onEvent = viewModel::onEvent,
		lightColorSchemes = viewModel.lightColorSchemes,
		darkColorSchemes = viewModel.darkColorSchemes,
		selectedLightColorScheme = lightColorScheme.name,
		selectedDarkColorScheme = darkColorScheme.name,
		darkMode = darkMode.displayName,
		leftHandMode = leftHandMode,
		actionButtonsOnTop = actionButtonsOnTop,
		modifier = modifier.fillMaxSize(),
	)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SettingsScreenContent(
	openDrawer: () -> Unit,
	onEvent: (SettingsViewModel.Event) -> Unit,
	lightColorSchemes: PersistentSet<String>,
	darkColorSchemes: PersistentSet<String>,
	selectedLightColorScheme: String,
	selectedDarkColorScheme: String,
	leftHandMode: Boolean,
	actionButtonsOnTop: Boolean,
	darkMode: String,
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
		modifier = modifier,
	) { paddingValues ->
		Column(
			modifier =
			Modifier
				.fillMaxSize()
				.displayCutoutPadding()
				.systemBarsPadding()
				.padding(paddingValues)
				.padding(LocalPadding.current.normal)
				.verticalScroll(rememberScrollState()),
		) {
			var themeExpanded by remember { mutableStateOf(false) }
			val themeOptions = persistentSetOf("System", "Dark", "Light")

			var lightColorSchemeExpanded by remember { mutableStateOf(false) }
			var darkColorSchemeExpanded by remember { mutableStateOf(false) }

			SettingsCategory("Appearance") {
				SettingDropDownMenu(
					title = "Theme",
					description = if (darkMode == "system") {
						"Follow system theme"
					} else {
						null
					},
					isExpanded = themeExpanded,
					onExpandedChange = { themeExpanded = it },
					onSelect = {
						onEvent(SettingsViewModel.Event.SetDarkMode(it))
					},
					selectedValue = darkMode,
					options = themeOptions,
					modifier = Modifier.fillMaxWidth(),
				)

				SettingDropDownMenu(
					title = "Light color scheme",
					isExpanded = lightColorSchemeExpanded,
					onExpandedChange = { lightColorSchemeExpanded = it },
					onSelect = { onEvent(SettingsViewModel.Event.SetLightColorScheme(it)) },
					selectedValue = selectedLightColorScheme,
					options = lightColorSchemes,
					modifier = Modifier.fillMaxWidth(),
				)

				SettingDropDownMenu(
					title = "Dark color scheme",
					isExpanded = darkColorSchemeExpanded,
					onExpandedChange = { darkColorSchemeExpanded = it },
					onSelect = { onEvent(SettingsViewModel.Event.SetDarkColorScheme(it)) },
					selectedValue = selectedDarkColorScheme,
					options = darkColorSchemes,
					modifier = Modifier.fillMaxWidth(),
				)
			}

			SettingsCategory("Accessibility") {
				SettingSwitchItem(
					title = "Left hand mode",
					description = "Swap the layout of the keypad",
					value = leftHandMode,
					onValueChange = { onEvent(SettingsViewModel.Event.ToggleLeftHandMode(it)) },
					modifier = Modifier.fillMaxWidth(),
				)

				SettingSwitchItem(
					title = "Show action buttons on top",
					description = "Move the action buttons to the top of the screen",
					value = actionButtonsOnTop,
					onValueChange = { onEvent(SettingsViewModel.Event.ToggleActionButtonsOnTop(it)) },
					modifier = Modifier.fillMaxWidth(),
				)
			}
		}
	}
}

@PreviewScreenSizes
@Composable
private fun SettingsScreenPreview() {
	SudokuSlayerTheme {
		SettingsScreenContent(
			openDrawer = { },
			onEvent = { },
			lightColorSchemes = persistentSetOf("Latte", "Frappe"),
			darkColorSchemes = persistentSetOf("Mocha", "Macchiato"),
			selectedLightColorScheme = "Latte",
			selectedDarkColorScheme = "Mocha",
			leftHandMode = true,
			darkMode = "System",
			actionButtonsOnTop = false,
			modifier = Modifier.fillMaxSize(),
		)
	}
}
