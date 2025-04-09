plugins {
	id("DomainModuleConvention")
}

dependencies {
	api(projects.domain.core)
	implementation(libs.kotlinx.datetime)
}
