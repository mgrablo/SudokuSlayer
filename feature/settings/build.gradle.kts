
plugins {
	id("FeatureModuleConvention")
}

dependencies {
	implementation(projects.feature.uicore)
	implementation(projects.domain.settings)
}
