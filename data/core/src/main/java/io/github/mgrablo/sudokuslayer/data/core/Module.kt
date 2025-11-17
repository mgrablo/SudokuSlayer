package io.github.mgrablo.sudokuslayer.data.core

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import io.github.mgrablo.sudokuslayer.data.core.database.AndroidDatabaseDriverFactory
import io.github.mgrablo.sudokuslayer.data.core.database.DatabaseProvider
import io.github.mgrablo.sudokuslayer.data.core.preferences.DataStorePreferenceStorage
import io.github.mgrablo.sudokuslayer.data.core.preferences.PreferenceStorage
import io.github.mgrablo.sudokuslayer.data.core.preferences.createDataStore
import io.github.mgrablo.sudokuslayer.data.core.proto.ProtoStorageFactory
import io.github.mgrablo.sudokuslayer.data.core.proto.ProtoStorageFactoryImpl
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
