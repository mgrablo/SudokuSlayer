package com.example.sudokuslayer.data.core.preferences

interface PreferenceStorageFactory {
	fun create(name: String): PreferenceStorage
}
