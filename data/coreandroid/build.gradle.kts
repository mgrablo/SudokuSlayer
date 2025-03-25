plugins {
	id("DataModuleConvention")
}

dependencies {
	implementation(project(":data:core"))

	implementation(libs.androidx.proto.datastore)
}
