plugins {
	id("DataModuleConvention")
	alias(libs.plugins.protobuf)
}

dependencies {
	implementation(project(":data:core"))
	implementation(project(":sudoku-core"))
	implementation(project(":domain:game"))

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
