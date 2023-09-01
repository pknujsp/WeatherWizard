plugins {
    id("plugin.android.library")
    alias(libs.plugins.ksp)
    alias(libs.plugins.kotlin.parcelize)
}

android {
    namespace = "io.github.pknujsp.weatherwizard.core.model"
}

dependencies {
    implementation(project(":core:common"))
    implementation(libs.bundles.ktx)
    ksp(libs.ksealedbinding.compiler)
    implementation(libs.ksealedbinding.annotation)
    implementation(libs.bundles.room)
    ksp(libs.androidx.room.compiler)
}