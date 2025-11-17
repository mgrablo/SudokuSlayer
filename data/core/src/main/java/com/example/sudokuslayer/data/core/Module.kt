package com.example.sudokuslayer.data.core

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import com.example.data.core.AppDatabase
import com.example.sudokuslayer.data.core.database.AndroidDatabaseDriverFactory
import com.example.sudokuslayer.data.core.database.DatabaseProvider
import com.example.sudokuslayer.data.core.preferences.DataStorePreferenceStorage
import com.example.sudokuslayer.data.core.preferences.PreferenceStorage
import com.example.sudokuslayer.data.core.preferences.createDataStore
import com.example.sudokuslayer.data.core.proto.ProtoStorageFactory
import com.example.sudokuslayer.data.core.proto.ProtoStorageFactoryImpl
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.dsl.bind
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

val dataCoreModule = module {
	single<DataStore<Preferences>> { createDataStore("settings", androidContext()) }
	singleOf(::DataStorePreferenceStorage) {
		bind<PreferenceStorage>()
	}

	single<ProtoStorageFactory> { ProtoStorageFactoryImpl(androidContext()) }
	single { AndroidDatabaseDriverFactory(androidContext()) }
	single { DatabaseProvider(get()) }
	single<AppDatabase> {
		val databaseProvider: DatabaseProvider = get()
		databaseProvider.getDatabase()
	}
}
