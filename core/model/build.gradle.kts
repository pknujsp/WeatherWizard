import org.jetbrains.kotlin.konan.properties.Properties

plugins {
    id("plugin.android.library")
    alias(libs.plugins.ksp)
    alias(libs.plugins.kotlin.parcelize)
}

android {
    namespace = "io.github.pknujsp.weatherwizard.core.model"
    applyCompose(this)

    @Suppress("UnstableApiUsage")
    buildFeatures {
        buildConfig = true
    }

    defaultConfig {
        val properties = Properties()
        properties.load(project.rootProject.file("/local.properties").bufferedReader())
        buildConfigField("String", "FLICKR_KEY", "\"${properties["flickr_key"]}\"")
        buildConfigField("String", "AQICN_KEY", "\"${properties["aqicn_key"]}\"")
    }
}

dependencies {
    implementation(project(":core:common"))
    implementation(libs.bundles.ktx)
    ksp(libs.ksealedbinding.compiler)
    implementation(libs.ksealedbinding.annotation)
    implementation(libs.bundles.room)
    ksp(libs.androidx.room.compiler)
    implementation(libs.sunrisesunsetcalculator)

    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.foundation)
}