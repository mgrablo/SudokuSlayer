plugins {
	id("DataModuleConvention")
}

dependencies {
	implementation(project(":data:core"))
	implementation(project(":domain:settings"))
}
