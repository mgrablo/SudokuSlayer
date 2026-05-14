plugins {
	id("DataModuleConvention")
	alias(libs.plugins.protobuf)
}

dependencies {
	implementation(projects.data.core)
	implementation(projects.sudokuCore)
	implementation(projects.domain.game)

	implementation(libs.androidx.proto.datastore)
	implementation(libs.protobuf.kotlin.lite)
	implementation(libs.protobuf.javalite)
}

android {
	defaultConfig {
		consumerProguardFiles("consumer-rules.pro")
	}
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
				create("java") {
					option("lite")
				}
				create("kotlin") {
					option("lite")
				}
			}
		}
	}
}
