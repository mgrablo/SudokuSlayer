plugins {
	id("DomainModuleConvention")
}

dependencies {
	api(projects.sudokuCore)
	api(projects.domain.core)
}
