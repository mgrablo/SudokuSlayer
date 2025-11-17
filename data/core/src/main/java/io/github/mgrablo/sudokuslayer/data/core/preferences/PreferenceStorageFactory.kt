package io.github.mgrablo.sudokuslayer.data.core.preferences

interface PreferenceStorageFactory {
	fun create(name: String): PreferenceStorage
}
