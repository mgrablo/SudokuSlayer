plugins {
	id("DataModuleConvention")
}

dependencies {
	implementation(libs.androidx.datastorePreferences)
	implementation(libs.koin.android)
}
