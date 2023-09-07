plugins {
    id("plugin.android.feature")
}

android {
    namespace = "io.github.pknujsp.weatherwizard.feature.flickr"
}

dependencies {
    implementation(project(":core:domain"))
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.constraintlayout.compose)
}