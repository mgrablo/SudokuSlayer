plugins {
	alias(libs.plugins.android.library)
	alias(libs.plugins.kotlin.android)
	alias(libs.plugins.protobuf)
}

android {
	namespace = "com.example.data.game"
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
		consumerProguardFiles("consumer-rules.pro")
	}

	buildTypes {
		release {
			isMinifyEnabled = false
			proguardFiles(
				getDefaultProguardFile("proguard-android-optimize.txt"),
				"proguard-rules.pro",
			)
		}
	}
	compileOptions {
		sourceCompatibility = JavaVersion.VERSION_11
		targetCompatibility = JavaVersion.VERSION_11
	}
	kotlinOptions {
		jvmTarget = "11"
	}
}

dependencies {
	implementation(project(":data:core"))

	implementation(libs.kotlinx.coroutines.core)
	implementation(libs.androidx.proto.datastore)
	implementation(libs.protobuf.kotlin.lite)
	implementation(libs.protobuf.javalite)
}

protobuf {
	protoc {
		artifact =
			libs.google.protobuf.protoc
				.get()
				.toString()
	}
	generateProtoTasks {
		all().forEach { task ->
			task.builtins {
				create("kotlin") {
					option("lite")
				}
				create("java") {
					option("lite")
				}
			}
		}
	}
}
