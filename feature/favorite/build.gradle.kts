plugins {
    id("plugin.android.feature")
}

android {
    namespace = "io.github.pknujsp.everyweather.feature.favorite"
}

dependencies {
    implementation(project(":core:data"))
    implementation(project(":core:domain"))
    implementation(project(":feature:map"))
    implementation(project(":feature:permoptimize"))

    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.constraintlayout.compose)

    ksp(libs.ksealedbinding.compiler)
    implementation(libs.ksealedbinding.annotation)
}
