package com.example.data.core.preferences

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.doublePreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map

class DataStorePreferencesManager private constructor(
	private val dataStore: DataStore<Preferences>,
) : PreferencesManager {
	override fun <T> getAsFlow(key: PreferencesManager.Key<T>): Flow<T?> =
		dataStore.data
			.map { preferences ->
				preferences[getDataStoreKey(key)] ?: key.defaultValue
			}

	override suspend fun <T> get(key: PreferencesManager.Key<T>): T? = getAsFlow(key).firstOrNull() ?: key.defaultValue

	override suspend fun <T> writeValue(
		key: PreferencesManager.Key<T>,
		value: T?,
	) {
		dataStore.edit { preferences ->
			val dataStoreKey = getDataStoreKey(key)
			if (value == null) {
				preferences.remove(dataStoreKey)
			} else {
				preferences[dataStoreKey] = value
			}
		}
	}

	@Suppress("UNCHECKED_CAST")
	private fun <T> getDataStoreKey(key: PreferencesManager.Key<T>): Preferences.Key<T> =
		when (key) {
			is PreferencesManager.Key.BooleanKey -> booleanPreferencesKey(key.name)
			is PreferencesManager.Key.DoubleKey -> doublePreferencesKey(key.name)
			is PreferencesManager.Key.FloatKey -> floatPreferencesKey(key.name)
			is PreferencesManager.Key.IntKey -> intPreferencesKey(key.name)
			is PreferencesManager.Key.LongKey -> longPreferencesKey(key.name)
			is PreferencesManager.Key.StringKey -> stringPreferencesKey(key.name)
		} as Preferences.Key<T>

	companion object {
		@Volatile
		private var instance: DataStorePreferencesManager? = null

		fun getInstance(context: Context): DataStorePreferencesManager =
			instance ?: synchronized(this) {
				instance ?: DataStorePreferencesManager(createDataStore(context)).also {
					instance = it
				}
			}
	}
}
