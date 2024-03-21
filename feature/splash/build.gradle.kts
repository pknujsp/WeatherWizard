plugins {
    id("plugin.android.feature")
}

android {
    namespace = "io.github.pknujsp.everyweather.feature.splash"
}

dependencies {
    implementation(libs.androidx.activity)
    implementation(project(":feature:permoptimize"))
    implementation(project(":core:data"))
}
