package io.github.mgrablo.sudokuslayer.feature.statistics.filter.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Button
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import io.github.mgrablo.sudokuslayer.feature.statistics.R
import io.github.mgrablo.sudokuslayer.feature.uicore.theme.LocalPadding

@Composable
fun FilterActionButtons(
	onClearClick: () -> Unit,
	onApplyClick: () -> Unit,
	activeFilterCount: Int,
	modifier: Modifier = Modifier,
) {
	Surface(
		modifier = modifier.fillMaxWidth(),
		color = MaterialTheme.colorScheme.surfaceContainer,
	) {
		Column {
			HorizontalDivider(Modifier.fillMaxWidth())
			Row(
				Modifier.padding(LocalPadding.current.normal),
				horizontalArrangement = Arrangement.spacedBy(LocalPadding.current.normal),
			) {
				OutlinedButton(
					onClick = onClearClick,
					modifier = Modifier.weight(1f),
				) {
					BadgedBox(
						badge =
						{
							if (activeFilterCount > 0) {
								Badge(
									modifier = Modifier.offset(LocalPadding.current.normal),
									containerColor = MaterialTheme.colorScheme.errorContainer,
									contentColor = MaterialTheme.colorScheme.onError,
								) {
									Text(
										text = activeFilterCount.toString(),
										style = MaterialTheme.typography.labelSmall,
										color = MaterialTheme.colorScheme.onErrorContainer,
									)
								}
							}
						},
					) {
						Row(
							horizontalArrangement = Arrangement.Center,
							verticalAlignment = Alignment.CenterVertically,
						) {
							Icon(Icons.Default.Clear, contentDescription = null)
							Text(
								stringResource(R.string.clear_filters_button),
								color = MaterialTheme.colorScheme.tertiary,
							)
						}
					}
				}
				Button(
					onClick = onApplyClick,
					modifier = Modifier.weight(1f),
				) {
					Icon(Icons.Default.Check, contentDescription = null)
					Text(
						stringResource(R.string.apply_filters_button),
						color = MaterialTheme.colorScheme.onPrimary,
					)
				}
			}
		}
	}
}
