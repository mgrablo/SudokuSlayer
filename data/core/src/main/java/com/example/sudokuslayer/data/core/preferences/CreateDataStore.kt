package com.example.sudokuslayer.data.core.preferences

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStoreFile

fun createDataStore(name: String, context: Context): DataStore<Preferences> =
	PreferenceDataStoreFactory.create(
		produceFile = { context.preferencesDataStoreFile(name) },
	)
