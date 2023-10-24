plugins {
    id("plugin.android.feature")
    alias(libs.plugins.kotlin.android)
}

android {
    namespace = "io.github.pknujsp.weatherwizard.feature.widget"
    buildFeatures {
        viewBinding = true
    }
}

dependencies {
    implementation(project(":core:domain"))
    implementation(project(":core:data"))
    implementation(project(":feature:searchlocation"))
    implementation(project(":feature:main"))
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.bundles.workmanager)
    implementation(libs.material)
    implementation(libs.constraintlayout)

    ksp(libs.ksealedbinding.compiler)
    implementation(libs.ksealedbinding.annotation)
}