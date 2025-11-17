plugins {
	alias(libs.plugins.android.library)
	alias(libs.plugins.kotlin.android)
}

android {
	namespace = "io.github.mgrablo.sudokuslayer.$modulePackageName"
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
}
