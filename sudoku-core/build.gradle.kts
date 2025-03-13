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

dependencies {
	implementation(libs.annotations)
	implementation(libs.kotlinx.coroutines.core)
	implementation(libs.kotlinx.collections.immutable)
	implementation(libs.koin.core)
	testImplementation(libs.junit.jupiter)
	testImplementation(libs.junit.jupiter.api)
	compileOnly(libs.junit.jupiter.params)
	testRuntimeOnly(libs.junit.jupiter.engine)
}

tasks.withType<Test> {
	useJUnitPlatform()
}
