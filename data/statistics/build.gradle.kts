plugins {
	id("DataModuleConvention")
}

dependencies {
	implementation(projects.data.core)
	implementation(projects.domain.statistics)
}
