plugins {
	id("AndroidLibraryConvention")
}

dependencies {
	implementation(libs.kotlinx.coroutines.core)
	implementation(libs.kotlinx.collections.immutable)
	implementation(libs.koin.core)
}
