plugins {
	id("DomainModuleConvention")
}

dependencies {
	implementation(project(":sudoku-core"))
	api(project(":domain:core"))
}
