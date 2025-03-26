plugins {
	id("java-library")
	alias(libs.plugins.jetbrains.kotlin.jvm)
}

java {
	sourceCompatibility = JavaVersion.VERSION_11
	targetCompatibility = JavaVersion.VERSION_11
}

kotlin {
	compilerOptions {
		jvmTarget = org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_11
	}
}

testing {
	suites {
		named<JvmTestSuite>("test") {
			useJUnitJupiter()
		}
	}
}

dependencies {
	implementation(libs.annotations)
	implementation(libs.kotlinx.coroutines.core)
	implementation(libs.kotlinx.collections.immutable)

	// For stable annotation
	implementation(libs.androidx.runtime)

	implementation(libs.koin.core)

	testImplementation(platform(libs.junit.bom))
}

tasks.withType<Test> {
	useJUnitPlatform()
}
