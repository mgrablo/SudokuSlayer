package com.example.data.core_android

import com.example.data.core.proto.ProtoStorageFactory
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val coreAndroidModule =
	module {
		single<ProtoStorageFactory> { ProtoStorageFactoryImpl(androidContext()) }
	}
