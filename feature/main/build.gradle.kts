plugins {
    id("plugin.android.feature")
}

android {
    namespace = "io.github.pknujsp.weatherwizard.feature.main"
}

dependencies {
    implementation(libs.ksealedbinding.annotation)
    ksp(libs.ksealedbinding.compiler)
    implementation(project(":core:data"))
    implementation(project(":feature:weather"))
    implementation(project(":feature:settings"))
    implementation(project(":feature:favorite"))
    implementation(project(":feature:notification"))
}