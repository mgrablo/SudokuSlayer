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
	implementation(projects.feature.statistics)
	implementation(projects.data.core)
	implementation(projects.data.settings)
	implementation(projects.data.game)
	implementation(projects.data.statistics)

	implementation(libs.bundles.koin.compose)
	implementation(libs.androidx.core.ktx)
	implementation(libs.androidx.lifecycle.runtime.ktx)
	implementation(libs.androidx.activity.compose)
	implementation(platform(libs.androidx.compose.bom))
	implementation(libs.androidx.ui)
	implementation(libs.androidx.ui.graphics)
	implementation(libs.androidx.ui.tooling.preview)
	implementation(libs.androidx.material3)
	implementation(libs.androidx.material.icons)
	implementation(libs.androidx.navigation3.ui)
	implementation(libs.androidx.navigation3.runtime)
	implementation(libs.kotlinx.serialization.json)
	implementation(libs.compose.unstyled)
	implementation(libs.catppuccin.palette)
	implementation(libs.catppuccin.compose)
	implementation(libs.kotlinx.collections.immutable)

	ktlintRuleset(libs.ktlint.ruleset.compose)
	testImplementation(platform(libs.junit.bom))
	testImplementation(libs.junit.jupiter)
	testImplementation(libs.androidx.test.core)
	testImplementation(libs.androidx.test.core.ktx)
	testImplementation(libs.androidx.test.runner)
	testRuntimeOnly(libs.junit.platform.launcher)
	androidTestImplementation(platform(libs.androidx.compose.bom))
	androidTestImplementation(libs.junit.jupiter.api)
	androidTestImplementation(libs.androidx.ui.test.android)
	androidTestImplementation(libs.androidx.test.ext.junit)
	androidTestImplementation(libs.androidx.test.ext.junit.ktx)
	androidTestImplementation(libs.junit5.android.test.compose)
	androidTestImplementation(libs.androidx.espresso.core)
	debugImplementation(libs.androidx.ui.tooling)
	debugImplementation(libs.androidx.ui.test.manifest)
	testImplementation(libs.koin.test)
	testImplementation(libs.koin.test.junit5)
}
