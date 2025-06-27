import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.tasks.KotlinJvmCompile

plugins {
	id("KtlintConvention")
	alias(libs.plugins.jetbrains.kotlin.jvm)
}

java {
	sourceCompatibility = JavaVersion.VERSION_11
	targetCompatibility = JavaVersion.VERSION_11
}
kotlin {
	compilerOptions {
		jvmTarget = JvmTarget.JVM_11
	}
}
dependencies {
	implementation(libs.koin.core)
	implementation(libs.kotlinx.coroutines.core)
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

tasks.withType<JavaCompile>().configureEach {
	options.release.set(11)
}
tasks.withType<KotlinJvmCompile>().configureEach {
	compilerOptions {
		jvmTarget = JvmTarget.JVM_11
	}
}
