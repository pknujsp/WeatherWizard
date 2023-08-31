plugins {
    id("plugin.android.library")
    id("plugin.android.hilt")
}

android {
    namespace = "io.github.pknujsp.weatherwizard.core.data"
}

dependencies {
    implementation(libs.bundles.ktx)
    implementation(libs.androidx.appcompat)
}