plugins {
    id("plugin.android.library")
    id("plugin.android.hilt")
}

android {
    namespace = "io.github.pknujsp.everyweather.core.domain"
}

dependencies {
    ksp(libs.ksealedbinding.compiler)
    implementation(libs.ksealedbinding.annotation)
    implementation(libs.bundles.ktx)
    implementation(libs.androidx.appcompat)
    implementation(project(":core:common"))
    implementation(project(":core:model"))
    implementation(project(":core:data"))
}
