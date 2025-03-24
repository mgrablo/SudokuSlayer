plugins {
	id("java-library")
	alias(libs.plugins.jetbrains.kotlin.jvm)
}
java {
	sourceCompatibility = JavaVersion.VERSION_11
	targetCompatibility = JavaVersion.VERSION_11
}
dependencies {
	implementation(project(":data:core"))
	implementation(project(":domain:settings"))

	implementation(libs.kotlinx.coroutines.core)
	implementation(libs.koin.core)
}
kotlin {
	compilerOptions {
		jvmTarget = org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_11
	}
}
