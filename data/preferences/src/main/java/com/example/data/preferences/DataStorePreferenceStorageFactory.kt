package com.example.data.preferences

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStoreFile
import com.example.data.core.preferences.PreferenceStorage
import com.example.data.core.preferences.PreferenceStorageFactory

class DataStorePreferenceStorageFactory(
	private val context: Context,
) : PreferenceStorageFactory {
	private val dataStoreInstances = mutableMapOf<String, DataStore<Preferences>>()

	@Synchronized
	override fun create(name: String): PreferenceStorage {
		val dataStore =
			dataStoreInstances.getOrPut(name) {
				createDataStore(name)
			}
		return DataStorePreferenceStorage(dataStore)
	}

	private fun createDataStore(name: String): DataStore<Preferences> =
		PreferenceDataStoreFactory.create(
			produceFile = { context.preferencesDataStoreFile(name) },
		)
}
