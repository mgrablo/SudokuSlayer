package com.example.sudokuslayer

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.data.core.preferences.DataStoreProvider
import com.example.data.core.preferences.PreferencesManager

class MainActivity : ComponentActivity() {
	lateinit var preferencesManager: PreferencesManager
		private set

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		preferencesManager = DataStoreProvider.providePreferencesManager(this)
		enableEdgeToEdge()

		setContent {
			App()
		}
	}
}
