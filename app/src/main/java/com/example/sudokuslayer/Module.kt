package com.example.sudokuslayer

import com.example.data.core_android.coreAndroidModule
import org.koin.dsl.module

val appModule =
	module {
		includes(coreAndroidModule)
	}
