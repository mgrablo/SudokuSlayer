package com.example.data.core.preferences

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

internal class DataStorePreferenceStorage(val dataStore: DataStore<Preferences>) :
	PreferenceStorage {
	override fun <T> getAsFlow(key: PreferenceStorage.Key<T>): Flow<T?> =
		dataStore.data.map { preferences ->
			preferences[getDataStoreKey(key)] ?: key.defaultValue
		}

	override suspend fun <T> get(key: PreferenceStorage.Key<T>): T? =
		getAsFlow(key).firstOrNull() ?: key.defaultValue

	override suspend fun <T> set(key: PreferenceStorage.Key<T>, value: T?) {
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
	private fun <T> getDataStoreKey(key: PreferenceStorage.Key<T>): Preferences.Key<T> = when (key) {
		is PreferenceStorage.Key.BooleanKey -> booleanPreferencesKey(key.name)
		is PreferenceStorage.Key.IntKey -> intPreferencesKey(key.name)
		is PreferenceStorage.Key.StringKey -> stringPreferencesKey(key.name)
		is PreferenceStorage.Key.FloatKey -> floatPreferencesKey(key.name)
		is PreferenceStorage.Key.DoubleKey -> doublePreferencesKey(key.name)
		is PreferenceStorage.Key.LongKey -> longPreferencesKey(key.name)
	} as Preferences.Key<T>
}
