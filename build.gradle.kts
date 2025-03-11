// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
	alias(libs.plugins.android.application) apply false
	alias(libs.plugins.kotlin.android) apply false
	alias(libs.plugins.kotlin.compose)
	alias(libs.plugins.kotlin.serialization) apply false
	alias(libs.plugins.protobuf) apply false
	alias(libs.plugins.jetbrains.kotlin.jvm) apply false
	alias(libs.plugins.ktlint)
	alias(libs.plugins.android.library) apply false
}
subprojects {
	tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile>().configureEach {
		compilerOptions {
			if (project.findProperty("enableComposeCompilerReports") == "true") {
				freeCompilerArgs.addAll(
					"-P",
					"plugin:androidx.compose.compiler.plugins.kotlin:reportsDestination=" +
						project.projectDir.toPath().toString() + "/build/compose_metrics",
				)
				freeCompilerArgs.addAll(
					"-P",
					"plugin:androidx.compose.compiler.plugins.kotlin:metricsDestination=" +
						project.projectDir.toPath().toString() + "/build/compose_metrics",
				)
			}
		}
	}
}
