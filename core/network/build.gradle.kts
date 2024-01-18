import org.jetbrains.kotlin.konan.properties.Properties

plugins {
    id("plugin.android.library")
    id("plugin.android.hilt")
    alias(libs.plugins.kotlin.serialization)
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
    implementation(libs.sunrisesunsetcalculator)
    implementation(libs.okhttp.logginginterceptor)
    ksp(libs.ksealedbinding.compiler)
    implementation(libs.ksealedbinding.annotation)
    implementation(libs.kotlinx.serialization.json)

    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.storage)
}