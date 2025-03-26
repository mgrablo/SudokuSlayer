plugins {
	id("DataModuleConvention")
}

dependencies {
	implementation(projects.data.core)

	implementation(libs.androidx.datastorePreferences)
}
