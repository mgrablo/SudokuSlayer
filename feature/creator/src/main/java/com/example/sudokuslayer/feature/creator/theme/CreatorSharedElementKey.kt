package com.example.sudokuslayer.feature.creator.theme

sealed interface CreatorSharedElementKey {
	object BoardPreview : CreatorSharedElementKey
	object CreatorScreenContent : CreatorSharedElementKey
}
