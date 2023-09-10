plugins {
    `kotlin-dsl`
}

group = "io.github.pknujsp.weatherwizard.buildlogic"

java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile>().configureEach {
    kotlinOptions {
        jvmTarget = JavaVersion.VERSION_17.toString()
    }
}

dependencies {
    compileOnly(libs.android.gradle.plugin)
    compileOnly(libs.kotlin.gradle.plugin)
    compileOnly(libs.ksp.gradle.plugin)
}


gradlePlugin {
    plugins {
        register("androidLibrary") {
            id = "plugin.android.library"
            implementationClass = "LibraryConventionPlugin"
        }
        register("androidHilt") {
            id = "plugin.android.hilt"
            implementationClass = "HiltConventionPlugin"
        }
        register("androidApplication") {
            id = "plugin.android.application"
            implementationClass = "ApplicationConventionPlugin"
        }
        register("androidFeature") {
            id = "plugin.android.feature"
            implementationClass = "FeatureConventionPlugin"
        }
    }
}