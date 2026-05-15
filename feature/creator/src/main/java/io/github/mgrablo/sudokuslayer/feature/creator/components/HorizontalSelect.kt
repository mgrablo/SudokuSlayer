package io.github.mgrablo.sudokuslayer.feature.creator.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PageSize
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import io.github.mgrablo.sudokuslayer.feature.creator.R
import kotlinx.collections.immutable.PersistentList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.coroutines.launch

@Composable
internal fun HorizontalSelect(
	options: PersistentList<String>,
	onChange: (Int) -> Unit,
	modifier: Modifier = Modifier,
) {
	val onChangeState by rememberUpdatedState(onChange)
	val pagerState = rememberPagerState(initialPage = 0) { options.size }
	val coroutineScope = rememberCoroutineScope()

	LaunchedEffect(pagerState) {
		snapshotFlow { pagerState.settledPage }.collect {
			onChangeState(it)
		}
	}

	Row(
		modifier = modifier,
		verticalAlignment = Alignment.CenterVertically,
		horizontalArrangement = Arrangement.Center,
	) {
		IconButton(
			onClick = {
				coroutineScope.launch {
					pagerState.animateScrollToPage(pagerState.currentPage - 1)
				}
			},
		) {
			Icon(
				Icons.AutoMirrored.Default.KeyboardArrowLeft,
				stringResource(R.string.previous_content_desc),
			)
		}
		HorizontalPager(
			state = pagerState,
			pageSize = PageSize.Fill,
			modifier = Modifier.weight(1f),
		) { page ->
			Row(
				verticalAlignment = Alignment.CenterVertically,
				horizontalArrangement = Arrangement.Center,
				modifier = Modifier.fillMaxWidth(),
			) {
				Text(
					text = options[page],
				)
			}
		}
		IconButton(
			onClick = {
				coroutineScope.launch {
					pagerState.animateScrollToPage(pagerState.currentPage + 1)
				}
			},
		) {
			Icon(
				Icons.AutoMirrored.Default.KeyboardArrowRight,
				stringResource(R.string.next_content_desc),
			)
		}
	}
}

@Preview
@Composable
private fun HorizontalSelectPreview() {
	HorizontalSelect(
		options = persistentListOf("Easy", "Medium", "Hard", "Expert"),
		onChange = { },
		modifier = Modifier,
	)
}
