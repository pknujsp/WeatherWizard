plugins {
    id("plugin.android.feature")
}

android {
    namespace = "io.github.pknujsp.everyweather.feature.settings"
}

dependencies {
    implementation(project(":core:domain"))
    implementation(project(":core:data"))
    implementation(project(":feature:componentservice"))
    implementation(project(":core:widgetnotification"))
    implementation(project(":feature:permoptimize"))

    implementation(libs.androidx.appcompat)
    implementation(libs.bundles.workmanager)
    implementation(libs.androidx.browser)
}