package io.github.mgrablo.sudokuslayer.data.core.proto

import kotlinx.coroutines.flow.Flow

interface ProtoStorage<T> {
	fun getData(): Flow<T>

	suspend fun updateData(transform: (T) -> T)
}
