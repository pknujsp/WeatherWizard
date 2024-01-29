plugins {
    id("plugin.android.feature")
    alias(libs.plugins.kotlin.android)
}

android {
    namespace = "io.github.pknujsp.everyweather.feature.searchlocation"
}

dependencies {
    implementation(project(":core:domain"))
    implementation(project(":core:data"))
    implementation(project(":feature:favorite"))
    implementation(project(":feature:permoptimize"))

    implementation(libs.androidx.appcompat)
    ksp(libs.ksealedbinding.compiler)
    implementation(libs.ksealedbinding.annotation)
}