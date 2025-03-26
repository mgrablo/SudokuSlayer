plugins {
	id("DataModuleConvention")
}

dependencies {
	implementation(projects.data.core)

	implementation(libs.androidx.proto.datastore)
	implementation(libs.koin.android)
}
