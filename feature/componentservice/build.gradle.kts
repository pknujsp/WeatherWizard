plugins {
    id("plugin.android.feature")
    alias(libs.plugins.kotlin.android)
}

android {
    namespace = "io.github.pknujsp.everyweather.feature.componentservice"
    buildFeatures {
        viewBinding = true
    }
    hilt {
        enableAggregatingTask = true
    }
}

dependencies {
    implementation(project(":core:domain"))
    implementation(project(":core:data"))
    implementation(project(":core:widgetnotification"))
    implementation(project(":feature:searchlocation"))
    implementation(project(":feature:permoptimize"))

    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.activity)
}
