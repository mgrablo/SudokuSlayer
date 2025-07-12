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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.tooling.preview.PreviewScreenSizes
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.domain.settings.models.ColorScheme
import com.example.domain.settings.models.DarkMode
import com.example.feature.settings.components.SettingDropDownMenu
import com.example.feature.settings.components.SettingSwitchItem
import com.example.feature.settings.components.SettingsCategory
import com.example.feature.uicore.theme.LocalPadding
import com.example.feature.uicore.theme.SudokuSlayerTheme
import com.example.sudokuslayer.feature.settings.R
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
				colors = TopAppBarDefaults.topAppBarColors(
					containerColor = MaterialTheme.colorScheme.surfaceContainer,
				),
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
				.verticalScroll(rememberScrollState())
				.padding(paddingValues)
				.padding(LocalPadding.current.normal),
		) {
			var themeExpanded by remember { mutableStateOf(false) }
			var lightColorSchemeExpanded by remember { mutableStateOf(false) }
			var darkColorSchemeExpanded by remember { mutableStateOf(false) }

			SettingsCategory("Appearance") {
				SettingDropDownMenu(
					title = "Theme",
					description = if (uiState.appearance.darkMode == DarkMode.SYSTEM) {
						"Follow system theme"
					} else {
						null
					},
					isExpanded = themeExpanded,
					onExpandedChange = { themeExpanded = it },
					selectedValue = uiState.appearance.darkMode,
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
					selectedValue = uiState.appearance.lightColorScheme,
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
					selectedValue = uiState.appearance.darkColorScheme,
					onSelect = { onEvent(SettingsViewModel.Event.SetDarkColorScheme(it)) },
					options = darkColorSchemes,
					optionToString = {
						it.name
					},
					modifier = Modifier.fillMaxWidth(),
				)

				SettingSwitchItem(
					title = "Compact Insights summaries",
					value = uiState.appearance.insightsSummaryCompactLayout,
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
					value = uiState.accessibility.leftHandMode,
					onValueChange = { onEvent(SettingsViewModel.Event.ToggleLeftHandMode(it)) },
					modifier = Modifier.fillMaxWidth(),
				)

				SettingSwitchItem(
					title = "Show action buttons on top",
					description = "Move the action buttons to the top of the screen",
					value = uiState.accessibility.actionButtonsOnTop,
					onValueChange = { onEvent(SettingsViewModel.Event.ToggleActionButtonsOnTop(it)) },
					modifier = Modifier.fillMaxWidth(),
				)
			}

			SettingsCategory("Gameplay") {
				SettingSwitchItem(
					title = stringResource(R.string.gameplay_hide_in_game_timer),
					value = !uiState.gameplay.timerVisibility,
					description = stringResource(R.string.gameplay_hide_in_game_timer_desc),
					onValueChange = { onEvent(SettingsViewModel.Event.ToggleTimerVisibility(it)) },
					modifier = Modifier.fillMaxWidth(),
				)
				SettingSwitchItem(
					title = "Auto clear notes",
					value = uiState.gameplay.autoClearNotes,
					description = "Clear notes when a number is input",
					onValueChange = { onEvent(SettingsViewModel.Event.ToggleAutoClearNotes(it)) },
					modifier = Modifier.fillMaxWidth(),
				)
				SettingSwitchItem(
					title = stringResource(R.string.gameplay_highlight_matching),
					value = uiState.gameplay.highlightMatching,
					description = stringResource(R.string.gameplay_highlight_matching_desc),
					onValueChange = { onEvent(SettingsViewModel.Event.ToggleHighlightMatching(it)) },
					modifier = Modifier.fillMaxWidth(),
				)
				SettingSwitchItem(
					title = stringResource(R.string.gameplay_mark_invalid),
					value = uiState.gameplay.highlightInvalid,
					description = stringResource(R.string.gameplay_mark_invalid_desc),
					onValueChange = { onEvent(SettingsViewModel.Event.ToggleHighlightInvalid(it)) },
					modifier = Modifier.fillMaxWidth(),
				)
			}
		}
	}
}

@PreviewScreenSizes
@PreviewLightDark
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
