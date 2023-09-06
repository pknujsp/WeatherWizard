plugins {
    id("plugin.android.library")
    id("plugin.android.hilt")
}

android {
    namespace = "io.github.pknujsp.weatherwizard.core.network"
}

dependencies {
    implementation(project(":core:model"))
    implementation(project(":core:common"))
    implementation(libs.bundles.ktx)
    implementation(libs.bundles.retrofit)
    implementation(libs.jsoup)
}