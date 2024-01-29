plugins {
    id("plugin.android.feature")
}

android {
    namespace = "io.github.pknujsp.everyweather.feature.flickr"
}

dependencies {
    implementation(project(":core:domain"))
    implementation(project(":core:data"))
    implementation(project(":feature:permoptimize"))

    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.constraintlayout.compose)
    implementation(libs.androidx.browser)
}