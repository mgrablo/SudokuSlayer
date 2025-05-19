plugins {
	id("FeatureModuleConvention")
}

dependencies {
	implementation(projects.feature.uicore)
	implementation(projects.domain.statistics)
	implementation(libs.kotlinx.datetime)
	implementation(libs.reorderable)
}
