plugins {
    id("plugin.android.library")
    id("plugin.android.hilt")
}

android {
    namespace = "io.github.pknujsp.everyweather.core.startup_impl"
}

dependencies {
    implementation(project(":core:startup-api"))
    implementation(project(":core:data"))
    implementation(libs.androidx.startup)
    implementation(libs.androidx.work.ktx)
}