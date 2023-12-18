plugins {
    id("plugin.android.library")
}

android {
    namespace = "io.github.pknujsp.weatherwizard.core.widgetnotification"
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.bundles.workmanager)
}