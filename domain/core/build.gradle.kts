plugins {
	id("DomainModuleConvention")
}

dependencies {
	api(projects.sudokuCore)
	implementation(libs.kotlinx.datetime)
}
