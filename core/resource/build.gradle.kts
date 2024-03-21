plugins {
    id("plugin.android.resource")
}

android {
    namespace = "io.github.pknujsp.everyweather.core.resource"
}

dependencies {
    implementation(libs.google.guava)
}
