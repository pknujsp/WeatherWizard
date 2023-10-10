plugins {
    id("plugin.android.library")
    id("plugin.android.hilt")
}

android {
    namespace = "io.github.pknujsp.weatherwizard.core.common"
    applyCompose(this)
}

dependencies {
    implementation(libs.bundles.ktx)
    implementation(libs.androidx.appcompat)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.foundation)
    implementation(libs.sunrisesunsetcalculator)
    implementation(libs.android.gms.play.services.location)
}