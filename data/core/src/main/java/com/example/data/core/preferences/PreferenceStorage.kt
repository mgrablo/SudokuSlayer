package com.example.data.core.preferences

import kotlinx.coroutines.flow.Flow

interface PreferenceStorage {
	fun <T> getAsFlow(key: Key<T>): Flow<T?>

	suspend fun <T> get(key: Key<T>): T?

	suspend fun <T> set(
		key: Key<T>,
		value: T?,
	)

	suspend fun <T> clear(key: Key<T>) = set(key, null)

	sealed class Key<T>(
		val name: String,
		val defaultValue: T?,
	) {
		open class StringKey(
			name: String,
			defaultValue: String?,
		) : Key<String>(name, defaultValue)

		open class IntKey(
			name: String,
			defaultValue: Int?,
		) : Key<Int>(name, defaultValue)

		open class BooleanKey(
			name: String,
			defaultValue: Boolean?,
		) : Key<Boolean>(name, defaultValue)

		open class FloatKey(
			name: String,
			defaultValue: Float?,
		) : Key<Float>(name, defaultValue)

		open class LongKey(
			name: String,
			defaultValue: Long?,
		) : Key<Long>(name, defaultValue)

		open class DoubleKey(
			name: String,
			defaultValue: Double?,
		) : Key<Double>(name, defaultValue)
	}
}
