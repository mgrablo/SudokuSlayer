package com.example.feature.settings

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
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
import com.example.domain.settings.models.ColorScheme
import com.example.domain.settings.models.DarkMode
import com.example.feature.settings.components.SettingDropDownMenu
import com.example.feature.settings.components.SettingSwitchItem
import com.example.feature.settings.components.SettingsCategory
import com.example.feature.uicore.theme.LocalPadding
import com.example.feature.uicore.theme.SudokuSlayerTheme
import kotlinx.collections.immutable.PersistentSet
import kotlinx.collections.immutable.toPersistentSet
import org.koin.androidx.compose.koinViewModel

@Composable
internal fun SettingsScreen(
	openDrawer: () -> Unit,
	modifier: Modifier = Modifier,
	viewModel: SettingsViewModel = koinViewModel<SettingsViewModel>(),
) {
	val uiState by viewModel.uiState.collectAsStateWithLifecycle()

	SettingsScreenContent(
		openDrawer = openDrawer,
		onEvent = viewModel::onEvent,
		themeOptions = DarkMode.all(),
		lightColorSchemes = viewModel.lightColorSchemes,
		darkColorSchemes = viewModel.darkColorSchemes,
		uiState = uiState,
		modifier = modifier.fillMaxSize(),
	)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SettingsScreenContent(
	openDrawer: () -> Unit,
	uiState: SettingsUiState,
	onEvent: (SettingsViewModel.Event) -> Unit,
	themeOptions: PersistentSet<DarkMode>,
	lightColorSchemes: PersistentSet<ColorScheme>,
	darkColorSchemes: PersistentSet<ColorScheme>,
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
				.padding(paddingValues)
				.padding(LocalPadding.current.normal)
				.verticalScroll(rememberScrollState()),
		) {
			var themeExpanded by remember { mutableStateOf(false) }
			var lightColorSchemeExpanded by remember { mutableStateOf(false) }
			var darkColorSchemeExpanded by remember { mutableStateOf(false) }

			SettingsCategory("Appearance") {
				SettingDropDownMenu(
					title = "Theme",
					description = if (uiState.darkMode == DarkMode.SYSTEM) {
						"Follow system theme"
					} else {
						null
					},
					isExpanded = themeExpanded,
					onExpandedChange = { themeExpanded = it },
					selectedValue = uiState.darkMode,
					onSelect = {
						onEvent(SettingsViewModel.Event.SetDarkMode(it))
					},
					optionToString = { it.displayName },
					options = themeOptions,
					modifier = Modifier.fillMaxWidth(),
				)

				SettingDropDownMenu(
					title = "Light color scheme",
					isExpanded = lightColorSchemeExpanded,
					onExpandedChange = { lightColorSchemeExpanded = it },
					onSelect = { onEvent(SettingsViewModel.Event.SetLightColorScheme(it)) },
					selectedValue = uiState.lightColorScheme,
					options = lightColorSchemes,
					optionToString = {
						it.name
					},
					modifier = Modifier.fillMaxWidth(),
				)

				SettingDropDownMenu(
					title = "Dark color scheme",
					isExpanded = darkColorSchemeExpanded,
					onExpandedChange = { darkColorSchemeExpanded = it },
					selectedValue = uiState.darkColorScheme,
					onSelect = { onEvent(SettingsViewModel.Event.SetDarkColorScheme(it)) },
					options = darkColorSchemes,
					optionToString = {
						it.name
					},
					modifier = Modifier.fillMaxWidth(),
				)

				SettingSwitchItem(
					title = "Compact Insights summaries",
					value = uiState.insightsSummaryCompactLayout,
					onValueChange = {
						onEvent(SettingsViewModel.Event.ToggleInsightsSummaryCompactLayout(it))
					},
					modifier = Modifier.fillMaxWidth(),
				)
			}

			SettingsCategory("Accessibility") {
				SettingSwitchItem(
					title = "Left hand mode",
					description = "Swap the layout of the keypad",
					value = uiState.leftHandMode,
					onValueChange = { onEvent(SettingsViewModel.Event.ToggleLeftHandMode(it)) },
					modifier = Modifier.fillMaxWidth(),
				)

				SettingSwitchItem(
					title = "Show action buttons on top",
					description = "Move the action buttons to the top of the screen",
					value = uiState.actionButtonsOnTop,
					onValueChange = { onEvent(SettingsViewModel.Event.ToggleActionButtonsOnTop(it)) },
					modifier = Modifier.fillMaxWidth(),
				)
			}

			SettingsCategory("Gameplay") {
				SettingSwitchItem(
					title = "Auto clear notes",
					value = uiState.autoClearNotes,
					description = "Clear notes when a number is input",
					onValueChange = { onEvent(SettingsViewModel.Event.ToggleAutoClearNotes(it)) },
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
			modifier = Modifier.fillMaxSize(),
			uiState = SettingsUiState(),
			themeOptions = DarkMode.all(),
			lightColorSchemes = ColorScheme.getLightColorSchemes().toPersistentSet(),
			darkColorSchemes = ColorScheme.getDarkColorSchemes().toPersistentSet(),
		)
	}
}
