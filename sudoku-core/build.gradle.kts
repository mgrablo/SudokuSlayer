plugins {
	id("java-library")
	alias(libs.plugins.jetbrains.kotlin.jvm)
}

testing {
	suites {
		named<JvmTestSuite>("test") {
			useJUnitJupiter()
		}
	}
}

dependencies {
	implementation(libs.androidx.annotations)
	implementation(libs.kotlinx.coroutines.core)
	implementation(libs.kotlinx.collections.immutable)

	// For stable annotation
	implementation(platform(libs.androidx.compose.bom))
	implementation(libs.androidx.runtime)

	implementation(libs.koin.core)

	testImplementation(platform(libs.junit.bom))
}

tasks.withType<Test> {
	useJUnitPlatform()
}
