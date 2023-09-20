plugins {
    id("plugin.android.library")
}

android {
    namespace = "io.github.pknujsp.weatherwizard.core.ui"
    applyCompose(this)
}

dependencies {
    implementation(project(":core:model"))
    implementation(project(":core:common"))
    implementation(libs.bundles.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    debugImplementation(libs.bundles.compose.debug)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.navigation.ui.ktx)
    implementation(libs.androidx.navigation.compose)
    implementation(libs.bundles.compose.bom)
    implementation(libs.bundles.lifecycle)
    implementation(libs.lottie)
    implementation(libs.coil)
}