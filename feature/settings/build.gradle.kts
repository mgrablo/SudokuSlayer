
plugins {
	id("FeatureModuleConvention")
}

dependencies {
	implementation(projects.core)
	implementation(projects.feature.uicore)
	implementation(projects.domain.settings)
}
