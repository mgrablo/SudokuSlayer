plugins {
	id("DomainModuleConvention")
}

dependencies {
	implementation(projects.domain.core)
	implementation(libs.kotlinx.datetime)
}
