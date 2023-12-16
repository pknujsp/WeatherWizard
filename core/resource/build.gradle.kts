plugins {
    id("plugin.android.library")
}

android {
    namespace = "io.github.pknujsp.weatherwizard.core.resource"
}

dependencies {
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
}