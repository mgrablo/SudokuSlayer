plugins {
	id("AndroidLibraryConvention")
	id("KtlintConvention")
}

dependencies {
	implementation(libs.kotlinx.coroutines.core)
	implementation(libs.kotlinx.datetime)
	implementation(libs.kotlinx.collections.immutable)
	implementation(libs.koin.core)
}
