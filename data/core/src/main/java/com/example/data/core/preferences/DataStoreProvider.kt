package com.example.data.core.preferences

import android.content.Context

object DataStoreProvider {
	@Volatile
	private var preferencesManager: PreferencesManager? = null

	fun providePreferencesManager(context: Context): PreferencesManager =
		preferencesManager ?: synchronized(this) {
			preferencesManager ?: DataStorePreferencesManager.getInstance(context).also {
				preferencesManager = it
			}
		}
}
