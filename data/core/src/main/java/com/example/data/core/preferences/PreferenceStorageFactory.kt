package com.example.data.core.preferences

interface PreferenceStorageFactory {
	fun create(name: String): PreferenceStorage
}
