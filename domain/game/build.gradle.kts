plugins {
	id("DomainModuleConvention")
}

dependencies {
	api(projects.sudokuCore)
	api(projects.domain.core)
	implementation(projects.domain.statistics)
	implementation(projects.domain.settings)
}
