plugins {
	`kotlin-dsl`
}

dependencies {
	implementation(libs.ktlint.gradle.plugin)
	implementation(libs.android.gradle.plugin)
	implementation(libs.kotlinAndroid.gradle.plugin)
}
