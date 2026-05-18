// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
	alias(libs.plugins.android.application) apply false
	alias(libs.plugins.kotlin.android) apply false
	alias(libs.plugins.kotlin.compose) apply false
	alias(libs.plugins.kotlin.serialization) apply false
	alias(libs.plugins.protobuf) apply false
	alias(libs.plugins.jetbrains.kotlin.jvm) apply false
	alias(libs.plugins.android.library) apply false
	alias(libs.plugins.ktlint) apply false
}

subprojects {
	plugins.withId("org.jetbrains.kotlin.android") {
		extensions.configure<org.jetbrains.kotlin.gradle.dsl.KotlinAndroidProjectExtension> {
			jvmToolchain(21)
		}
	}
	plugins.withId("org.jetbrains.kotlin.jvm") {
		extensions.configure<org.jetbrains.kotlin.gradle.dsl.KotlinJvmProjectExtension> {
			jvmToolchain(21)
		}
	}

	tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile>().configureEach {
		compilerOptions {
			if (project.findProperty("enableComposeCompilerReports") == "true") {
				val metricsDir = project.layout.buildDirectory.dir("compose_metrics").get().asFile.path
				freeCompilerArgs.addAll(
					"-P",
					"plugin:androidx.compose.compiler.plugins.kotlin:reportsDestination=$metricsDir",
					"-P",
					"plugin:androidx.compose.compiler.plugins.kotlin:metricsDestination=$metricsDir",
				)
			}
		}
	}
}
