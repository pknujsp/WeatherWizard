plugins {
    id("plugin.android.feature")
    alias(libs.plugins.kotlin.android)
}

android {
    namespace = "io.github.pknujsp.weatherwizard.feature.notification"
}

dependencies {
    implementation(project(":core:domain"))
    implementation(project(":core:data"))
    implementation(project(":core:widgetnotification"))
    implementation(project(":feature:searchlocation"))
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.bundles.workmanager)
    ksp(libs.ksealedbinding.compiler)
    implementation(libs.ksealedbinding.annotation)
}