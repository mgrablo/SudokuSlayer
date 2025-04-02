package com.example.feature.settings.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.width
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.MenuItemColors
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.feature.uicore.SudokuSlayerTheme
import kotlinx.collections.immutable.PersistentSet

@Composable
internal fun SettingItem(
	title: String,
	modifier: Modifier = Modifier,
	description: String? = null,
	content: @Composable () -> Unit,
) {
	Row(
		modifier = modifier,
		horizontalArrangement = Arrangement.SpaceBetween,
		verticalAlignment = Alignment.CenterVertically,
	) {
		Column(
			modifier = Modifier.weight(1f),
		) {
			Text(
				text = title,
				style = MaterialTheme.typography.bodyLarge,
				color = MaterialTheme.colorScheme.onSurface,
			)
			description?.let {
				Text(
					text = it,
					style = MaterialTheme.typography.bodySmall,
					color = MaterialTheme.colorScheme.onSurfaceVariant,
				)
			}
		}
		content()
	}
}

@Composable
internal fun SettingSwitchItem(
	title: String,
	value: Boolean,
	onValueChange: (Boolean) -> Unit,
	modifier: Modifier = Modifier,
	description: String? = null,
) {
	SettingItem(
		title = title,
		description = description,
		modifier = modifier,
		content = {
			Switch(
				checked = value,
				onCheckedChange = { onValueChange(it) },
			)
		},
	)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun SettingDropDownMenu(
	title: String,
	isExpanded: Boolean,
	onExpandedChange: (Boolean) -> Unit,
	onSelect: (String) -> Unit,
	selectedValue: String,
	options: PersistentSet<String>,
	modifier: Modifier = Modifier,
	description: String? = null,
) {
	SettingItem(
		title = title,
		description = description,
		modifier = modifier,
		content = {
			ExposedDropdownMenuBox(
				expanded = isExpanded,
				onExpandedChange = onExpandedChange,
			) {
				OutlinedTextField(
					value = selectedValue,
					onValueChange = {},
					readOnly = true,
					singleLine = true,
					trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = isExpanded) },
					modifier =
					Modifier
						.menuAnchor(type = MenuAnchorType.PrimaryNotEditable)
						.width(200.dp),
				)

				ExposedDropdownMenu(
					expanded = isExpanded,
					onDismissRequest = { onExpandedChange(false) },
					containerColor = MaterialTheme.colorScheme.surface,
				) {
					options.forEach { option ->
						DropdownMenuItem(
							text = { Text(option) },
							colors =
							MenuItemColors(
								textColor = MaterialTheme.colorScheme.onSurface,
								leadingIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
								trailingIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
								disabledTextColor = MaterialTheme.colorScheme.onSurface,
								disabledLeadingIconColor = MaterialTheme.colorScheme.onSurface,
								disabledTrailingIconColor = MaterialTheme.colorScheme.onSurface,
							),
							onClick = {
								onSelect(option)
								onExpandedChange(false)
							},
						)
					}
				}
			}
		},
	)
}

@Preview
@Composable
private fun SettingSwitchItemPreview() {
	SudokuSlayerTheme {
		Surface(
			color = MaterialTheme.colorScheme.background,
		) {
			SettingSwitchItem(
				title = "Test title",
				description = "Test Description",
				value = true,
				onValueChange = { },
				modifier = Modifier.width(200.dp),
			)
		}
	}
}
