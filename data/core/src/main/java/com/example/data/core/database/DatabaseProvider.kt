package com.example.data.core.database

import android.content.Context
import app.cash.sqldelight.adapter.primitive.IntColumnAdapter
import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.android.AndroidSqliteDriver
import com.example.data.core.AppDatabase
import com.example.data.core.GameResultEntity

class DatabaseProvider(databaseDriverFactory: AndroidDatabaseDriverFactory) {
	private var database: AppDatabase = AppDatabase(
		driver = databaseDriverFactory.createDriver(),
		GameResultEntityAdapter = GameResultEntity.Adapter(
			difficultyAdapter = gameDifficultyAdapter,
			gridSizeAdapter = sudokuGridSizeAdapter,
			hintsUsedAdapter = IntColumnAdapter,
			completedAtAdapter = localDateTimeAdapter,
		),
	)

	fun getDatabase(): AppDatabase = database
}

class AndroidDatabaseDriverFactory(private val context: Context) {
	fun createDriver(): SqlDriver = AndroidSqliteDriver(AppDatabase.Schema, context, "app_database.db")
}
