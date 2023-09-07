import org.jetbrains.kotlin.konan.properties.Properties

plugins {
    id("plugin.android.library")
    id("plugin.android.hilt")
}

android {
    namespace = "io.github.pknujsp.weatherwizard.core.network"

    defaultConfig {
        val properties = Properties()
        properties.load(project.rootProject.file("/local.properties").bufferedReader())
        buildConfigField("String", "FLICKR_KEY", "\"${properties["flickr_key"]}\"")
    }
}

dependencies {
    implementation(project(":core:model"))
    implementation(project(":core:common"))
    implementation(libs.bundles.ktx)
    implementation(libs.bundles.retrofit)
    implementation(libs.jsoup)
}