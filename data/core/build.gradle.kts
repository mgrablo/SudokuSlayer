plugins {
	id("java-library")
	alias(libs.plugins.jetbrains.kotlin.jvm)
}
java {
	sourceCompatibility = JavaVersion.VERSION_11
	targetCompatibility = JavaVersion.VERSION_11
}
dependencies {
	implementation(libs.kotlinx.coroutines.core)
}
kotlin {
	compilerOptions {
		jvmTarget = org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_11
	}
}
