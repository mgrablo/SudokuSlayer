package com.example.data.coreandroid

import androidx.datastore.core.DataStore
import com.example.data.core.proto.ProtoStorage
import java.io.IOException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch

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
