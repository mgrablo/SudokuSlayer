pluginManagement {
	repositories {
		google {
			content {
				includeGroupByRegex("com\\.android.*")
				includeGroupByRegex("com\\.google.*")
				includeGroupByRegex("androidx.*")
			}
		}
		mavenCentral()
		gradlePluginPortal()
	}
}

dependencyResolutionManagement {
	repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
	repositories {
		google()
		mavenCentral()
		maven { setUrl("https://jitpack.io") }
	}
}
enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")
enableFeaturePreview("STABLE_CONFIGURATION_CACHE")

rootProject.name = "SudokuSlayer"

includeBuild("build-logic")
include(":app")
include(":sudoku-core")
include(":data:core")
include(":data:settings")
include(":data:game")
include(":domain:game")
include(":domain:creator")
include(":domain:core")
include(":domain:settings")
include(":feature:uicore")
include(":feature:settings")
include(":feature:creator")
include(":feature:game")
include(":feature:statistics")
include(":domain:statistics")
include(":data:statistics")
include(":core")
