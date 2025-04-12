plugins {
	id("DataModuleConvention")

	alias(libs.plugins.sqldelight)
}

dependencies {
	implementation(projects.domain.core)
	implementation(libs.koin.android)
	implementation(libs.androidx.datastorePreferences)
	implementation(libs.androidx.proto.datastore)
	implementation(libs.android.driver)
}

sqldelight {
	databases {
		create("AppDatabase") {
			packageName.set("com.example.data.core")
		}
	}
}
