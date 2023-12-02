plugins {
    id("plugin.android.library")
    id("plugin.android.hilt")
}

android {
    namespace = "io.github.pknujsp.weatherwizard.core.data-abstract"
}

dependencies {
    implementation(libs.bundles.ktx)
    implementation(libs.androidx.appcompat)
    implementation(project(":core:common"))
    implementation(project(":core:model"))
    implementation(project(":core:data"))
}