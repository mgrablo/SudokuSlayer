plugins {
	alias(libs.plugins.ktlint)
}

ktlint {
	version = "1.7.1"
	ignoreFailures = false
	android = false
	filter {
		exclude { element ->
			val path = element.file.path
			path.contains("\\generated\\") ||
				path.contains("/generated/") ||
				element.file.relativeTo(projectDir).startsWith(File("build"))
		}
		exclude { it.file.path.contains("build/generated-sources/typesafe-conventions") }
	}
}
