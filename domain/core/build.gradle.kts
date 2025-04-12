plugins {
	id("DomainModuleConvention")
}

dependencies {
	implementation(projects.sudokuCore)
	implementation(libs.kotlinx.datetime)
}
