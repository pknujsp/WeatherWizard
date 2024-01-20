plugins {
    id("plugin.android.feature")
}

android {
    namespace = "io.github.pknujsp.weatherwizard.feature.weather"
}

dependencies {
    implementation(project(":core:domain"))
    implementation(project(":core:data"))
    implementation(project(":feature:flickr"))
    implementation(project(":feature:map"))
    implementation(project(":feature:airquality"))
    implementation(project(":feature:sunsetrise"))
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.constraintlayout.compose)
    implementation(libs.google.generativeai)
    implementation(libs.compose.markdown)
}