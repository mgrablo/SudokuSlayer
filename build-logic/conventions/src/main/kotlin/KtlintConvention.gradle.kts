plugins {
	alias(libs.plugins.ktlint)
}

ktlint {
	version = "1.5.0"
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
