plugins {
	alias(libs.plugins.android.application)
	alias(libs.plugins.kotlin.android)
	alias(libs.plugins.kotlin.compose)
	alias(libs.plugins.kotlin.serialization)
	alias(libs.plugins.android.junit5)
	alias(libs.plugins.protobuf)
	id("KtlintConvention")
}

android {
	namespace = "com.example.sudokuslayer"
	compileSdk =
		libs.versions.android.compileSdk
			.get()
			.toInt()

	defaultConfig {
		applicationId = "com.example.sudokuslayer"
		minSdk =
			libs.versions.android.minSdk
				.get()
				.toInt()
		targetSdk =
			libs.versions.android.targetSdk
				.get()
				.toInt()
		versionCode = 1
		versionName = "1.0"

		testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
	}

	buildTypes {
		release {
			isMinifyEnabled = false
			proguardFiles(
				getDefaultProguardFile("proguard-android-optimize.txt"),
				"proguard-rules.pro",
			)
			signingConfig = signingConfigs.getByName("debug")
		}
	}
	compileOptions {
		sourceCompatibility = JavaVersion.VERSION_11
		targetCompatibility = JavaVersion.VERSION_11
	}
	kotlinOptions {
		jvmTarget = "11"
	}
	buildFeatures {
		compose = true
	}
	testOptions {
		unitTests.all {
			it.useJUnitPlatform()
		}
	}
}

dependencies {
	implementation(projects.feature.uicore)
	implementation(projects.feature.game)
	implementation(projects.feature.creator)
	implementation(projects.feature.settings)
	implementation(projects.data.core)
	implementation(projects.data.preferences)
	implementation(projects.data.settings)
	implementation(projects.data.game)
	implementation(projects.data.coreandroid)

	implementation(libs.bundles.koin.compose)
	implementation(libs.androidx.core.ktx)
	implementation(libs.androidx.lifecycle.runtime.ktx)
	implementation(libs.androidx.activity.compose)
	implementation(platform(libs.androidx.compose.bom))
	implementation(libs.androidx.ui)
	implementation(libs.androidx.ui.graphics)
	implementation(libs.androidx.ui.tooling.preview)
	implementation(libs.androidx.material3)
	implementation(libs.androidx.navigation.compose)
	implementation(libs.kotlinx.serialization.json)
	implementation(libs.compose.unstyled)
	implementation(libs.catppuccin.palette)
	implementation(libs.catppuccin.compose)
	implementation(libs.kotlinx.collections.immutable)

	ktlintRuleset(libs.ktlint.ruleset.compose)
	testImplementation(platform(libs.junit.bom))
	androidTestImplementation(libs.junit.jupiter.api)
	androidTestImplementation(libs.androidx.espresso.core)
	androidTestImplementation(platform(libs.androidx.compose.bom))
	debugImplementation(libs.androidx.ui.tooling)
	debugImplementation(libs.androidx.ui.test.manifest)
}
