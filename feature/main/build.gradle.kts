import org.jetbrains.kotlin.storage.CacheResetOnProcessCanceled.enabled

plugins {
    id("plugin.android.feature")
}

android {
    namespace = "io.github.pknujsp.weatherwizard.feature.main"
}

dependencies {
    implementation(libs.ksealedbinding.annotation)
    ksp(libs.ksealedbinding.compiler)
    implementation(libs.androidx.activity)
    implementation(project(":core:data"))
    implementation(project(":core:domain"))
    implementation(project(":core:widgetnotification"))
    implementation(project(":core:ads"))
    implementation(project(":feature:weather"))
    implementation(project(":feature:settings"))
    implementation(project(":feature:favorite"))
    implementation(project(":feature:componentservice"))
    implementation(project(":feature:map"))
}