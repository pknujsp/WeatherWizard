import org.jetbrains.kotlin.konan.properties.Properties

plugins {
    id("plugin.android.library")
}

android {
    namespace = "io.github.pknujsp.everyweather.core.ads"
    applyCompose(this)

    buildFeatures {
        buildConfig = true
    }

    viewBinding.enable = true

    defaultConfig {
        val properties = Properties()
        properties.load(project.rootProject.file("/local.properties").bufferedReader())
        buildConfigField("String", "ADMOB_APP_ID", "\"${properties["admob_app_id"]}\"")
        buildConfigField("String", "ADMOB_NATIVE_AD_ID", "\"${properties["admob_native_id"]}\"")
        buildConfigField("String", "ADMOB_BANNER_AD_ID", "\"${properties["admob_banner_id"]}\"")
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pgcfg")
            consumerProguardFiles("proguard-rules.pgcfg", "admob-proguard-rules.pro")
        }
    }

}


dependencies {
    implementation(project(":core:common"))
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.foundation)
    implementation(libs.android.gms.play.services.ads)
    implementation(libs.google.errorprone.annotations)
    implementation(libs.androidx.constraintlayout)

}