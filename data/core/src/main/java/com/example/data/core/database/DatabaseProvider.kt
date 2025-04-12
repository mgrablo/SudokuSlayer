package com.example.data.core.database

import android.content.Context
import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.android.AndroidSqliteDriver
import com.example.data.core.AppDatabase

object DatabaseProvider {
	private lateinit var driver: SqlDriver
	private lateinit var database: AppDatabase

	fun initialize(context: Context) {
		driver = AndroidSqliteDriver(AppDatabase.Schema, context, "app_database.db")
	}

	fun getDatabase(): AppDatabase = database
}
