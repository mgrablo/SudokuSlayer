@file:Suppress("ktlint:standard:filename")

package com.example.data.core.preferences

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import okio.Path.Companion.toPath

private fun createDataStore(producePath: () -> String): DataStore<Preferences> =
	PreferenceDataStoreFactory.createWithPath(
		produceFile = { producePath().toPath() },
	)

const val DATASTORE_FILENAME = "sudokuslayer.preferences_pb"

internal fun createDataStore(context: Context): DataStore<Preferences> =
	createDataStore { context.filesDir.resolve(DATASTORE_FILENAME).absolutePath }
