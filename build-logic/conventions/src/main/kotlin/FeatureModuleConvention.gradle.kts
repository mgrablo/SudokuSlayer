
plugins {
	id("AndroidLibraryConvention")
	id("KtlintConvention")

	alias(libs.plugins.kotlin.compose)
	alias(libs.plugins.kotlin.serialization)
	alias(libs.plugins.android.junit5)
}

tasks.withType<Test> {
	useJUnitPlatform()
}

dependencies {
	implementation(platform(libs.androidx.compose.bom))
	implementation(libs.bundles.koin.compose)
	implementation(libs.androidx.ui)
	implementation(libs.androidx.core.ktx)
	implementation(libs.androidx.material3)
	implementation(libs.androidx.navigation.compose)
	implementation(libs.androidx.lifecycle.runtime.ktx)
	implementation(libs.androidx.activity.compose)
	implementation(libs.androidx.ui.graphics)
	implementation(libs.androidx.ui.tooling.preview)
	implementation(libs.kotlinx.collections.immutable)

	testImplementation(platform(libs.junit.bom))
	testImplementation(libs.junit.jupiter)
	testRuntimeOnly(libs.junit.platform.launcher)
	androidTestImplementation(platform(libs.androidx.compose.bom))
	androidTestImplementation(libs.androidx.ui.test.android)
	androidTestImplementation(libs.junit.jupiter.api)
	androidTestImplementation(libs.junit5.android.test.compose)
	debugImplementation(libs.androidx.ui.test.manifest)
	debugImplementation(libs.androidx.ui.tooling)
	testImplementation(libs.koin.test)
	testImplementation(libs.koin.test.junit5)

	ktlintRuleset(libs.ktlint.ruleset.compose)
}
