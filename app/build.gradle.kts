plugins {
    id("plugin.android.application")
    alias(libs.plugins.gms)
    alias(libs.plugins.firebase.crashlytics)
}

android {
    namespace = "io.github.pknujsp.weatherwizard"

    defaultConfig {
        applicationId = "io.github.pknujsp.weatherwizard"
        versionCode = 1
        versionName = "1.0"

        vectorDrawables {
            useSupportLibrary = true
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            isShrinkResources = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
            signingConfig = signingConfigs.getByName("debug")
        }
        create("benchmark") {
            initWith(buildTypes.getByName("release"))
            signingConfig = signingConfigs.getByName("debug")
            matchingFallbacks += listOf("release")
        }
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }

    lint {
        checkDependencies = true
        ignoreTestSources = true
    }

    hilt {
        enableAggregatingTask = true
    }
}


dependencies {
    implementation(project(":core:ui"))
    implementation(project(":core:common"))
    implementation(project(":core:model"))
    implementation(project(":feature:main"))
    implementation(project(":feature:map"))
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
}