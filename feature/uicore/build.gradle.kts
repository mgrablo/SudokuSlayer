plugins {
	id("FeatureModuleConvention")
}

dependencies {
	api(projects.domain.core)
	api(projects.domain.settings)

	implementation(libs.koin.core)
}
