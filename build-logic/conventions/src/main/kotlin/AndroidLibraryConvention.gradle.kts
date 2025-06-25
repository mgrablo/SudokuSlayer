import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
	alias(libs.plugins.android.library)
	alias(libs.plugins.kotlin.android)
}

android {
	namespace = "com.example.sudokuslayer.$modulePackageName"
	compileSdk =
		libs.versions.android.compileSdk
			.get()
			.toInt()

	defaultConfig {
		minSdk =
			libs.versions.android.minSdk
				.get()
				.toInt()
		testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
	}

	buildTypes {
		release {
			isMinifyEnabled = false
		}
	}
	compileOptions {
		sourceCompatibility = JavaVersion.VERSION_11
		targetCompatibility = JavaVersion.VERSION_11
	}
	kotlin {
		compilerOptions {
			jvmTarget = JvmTarget.JVM_11
		}
	}
}
