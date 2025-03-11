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

rootProject.name = "SudokuSlayer"
include(":app")
include(":sudoku-core")
include(":data:core")
include(":data:settings")
include(":data:preferences")
include(":data:game")
