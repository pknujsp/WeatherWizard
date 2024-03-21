plugins {
    id("plugin.android.feature")
}

android {
    namespace = "io.github.pknujsp.everyweather.feature.weather"
}

dependencies {
    implementation(project(":core:domain"))
    implementation(project(":core:data"))
    implementation(project(":core:ads"))
    implementation(project(":feature:flickr"))
    implementation(project(":feature:map"))
    implementation(project(":feature:airquality"))
    implementation(project(":feature:sunsetrise"))
    implementation(project(":feature:permoptimize"))

    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.constraintlayout.compose)
    implementation(libs.google.generativeai)
    implementation(libs.compose.markdown)
}
