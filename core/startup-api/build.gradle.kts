plugins {
    id("plugin.android.library")
    id("plugin.android.hilt")
}

android {
    namespace = "io.github.pknujsp.everyweather.core.startup_api"
}

dependencies {
    implementation(libs.androidx.startup)
    implementation(libs.androidx.work.ktx)
}