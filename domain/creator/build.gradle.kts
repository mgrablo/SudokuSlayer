plugins {
	id("DomainModuleConvention")
}

dependencies {
	implementation(projects.sudokuCore)
	api(projects.domain.core)
}
