plugins {
	id("DataModuleConvention")
}

dependencies {
	implementation(libs.koin.android)
	implementation(libs.androidx.datastorePreferences)
	implementation(libs.androidx.proto.datastore)
}
