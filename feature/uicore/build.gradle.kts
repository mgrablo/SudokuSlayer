plugins {
	id("FeatureModuleConvention")
}

dependencies {
	implementation(projects.domain.settings)

	implementation(libs.koin.core)
	implementation(libs.catppuccin.palette)
	implementation(libs.catppuccin.compose)
}
