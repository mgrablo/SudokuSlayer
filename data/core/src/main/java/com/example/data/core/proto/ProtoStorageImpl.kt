package com.example.data.core.proto

import androidx.datastore.core.DataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import java.io.IOException

class ProtoStorageImpl<T>(private val dataStore: DataStore<T>, private val defaultValue: T) :
	ProtoStorage<T> {
	override fun getData(): Flow<T> = dataStore.data
		.catch { exception ->
			if (exception is IOException) {
				emit(defaultValue)
			} else {
				throw exception
			}
		}

	override suspend fun updateData(transform: (T) -> T) {
		dataStore.updateData(transform)
	}
}
