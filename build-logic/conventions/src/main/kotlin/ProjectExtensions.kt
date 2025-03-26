import org.gradle.api.Project

val Project.modulePackageName get() = path
	.split(":")
	.filter { it.isNotBlank() }
	.joinToString(".") { it.lowercase() }
