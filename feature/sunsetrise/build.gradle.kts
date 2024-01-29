plugins {
    id("plugin.android.feature")
}

android {
    namespace = "io.github.pknujsp.everyweather.feature.sunsetrise"
}

dependencies {
    implementation(project(":core:domain"))
    implementation(project(":core:data"))
    implementation(project(":feature:permoptimize"))

    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.constraintlayout.compose)
}