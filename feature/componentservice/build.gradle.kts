plugins {
    id("plugin.android.feature")
}

android {
    namespace = "io.github.pknujsp.weatherwizard.feature.componentservice"
}

dependencies {
    implementation(project(":core:domain"))
    implementation(project(":core:data"))
    implementation(project(":core:widgetnotification"))
    implementation(project(":feature:searchlocation"))
    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.activity)
    implementation(libs.bundles.workmanager)
    ksp(libs.ksealedbinding.compiler)
    implementation(libs.ksealedbinding.annotation)
}