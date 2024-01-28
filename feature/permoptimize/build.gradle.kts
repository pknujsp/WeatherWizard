plugins {
    id("plugin.android.feature")
}

android {
    namespace = "io.github.pknujsp.everyweather.feature.permoptimize"
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
}