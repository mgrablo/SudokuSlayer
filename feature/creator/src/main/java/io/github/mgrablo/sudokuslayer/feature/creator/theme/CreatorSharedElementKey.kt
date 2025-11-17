package io.github.mgrablo.sudokuslayer.feature.creator.theme

sealed interface CreatorSharedElementKey {
	object BoardPreview : CreatorSharedElementKey
	object CreatorScreenContent : CreatorSharedElementKey
}
