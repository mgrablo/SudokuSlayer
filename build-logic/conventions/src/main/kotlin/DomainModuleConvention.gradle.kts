plugins {
	id("KtlintConvention")
	alias(libs.plugins.jetbrains.kotlin.jvm)
}

dependencies {
	implementation(libs.koin.core)
	implementation(libs.kotlinx.coroutines.core)
	implementation(libs.kotlinx.coroutines.test)
	implementation(libs.kotlinx.collections.immutable)

	testImplementation(platform(libs.junit.bom))
	testImplementation(libs.junit.jupiter)
	testRuntimeOnly(libs.junit.platform.launcher)
	testImplementation(libs.mockk)
}

tasks.test {
	useJUnitPlatform()
	testLogging {
		events("passed", "skipped", "failed")
	}
}
