plugins {
    id("plugin.android.library")
    id("plugin.android.hilt")
    alias(libs.plugins.ksp)
}

android {
    namespace = "io.github.pknujsp.weatherwizard.core.database"
}

dependencies {
    implementation(project(":core:model"))
    implementation(project(":core:common"))
    implementation(libs.bundles.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.bundles.room)
    ksp(libs.androidx.room.compiler)
}