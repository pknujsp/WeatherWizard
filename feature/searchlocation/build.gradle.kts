plugins {
    id("plugin.android.feature")
    alias(libs.plugins.kotlin.android)
}

android {
    namespace = "io.github.pknujsp.weatherwizard.feature.searchlocation"
}

dependencies {
    implementation(project(":core:domain"))
    implementation(project(":core:data"))
    implementation(project(":feature:favorite"))
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    ksp(libs.ksealedbinding.compiler)
    implementation(libs.ksealedbinding.annotation)
}