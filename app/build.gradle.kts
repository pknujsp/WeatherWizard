import com.google.firebase.crashlytics.buildtools.gradle.CrashlyticsExtension

plugins {
    id("plugin.android.application")
}

android {
    namespace = "io.github.pknujsp.everyweather"

    defaultConfig {
        applicationId = "io.github.pknujsp.everyweather"
        versionCode = 1
        versionName = "1.0"

        vectorDrawables {
            useSupportLibrary = true
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro",
                "gms-proguard-rules.pro",
            )
            signingConfig = signingConfigs.getByName("debug")
            configure<CrashlyticsExtension> {
                mappingFileUploadEnabled = true
            }
        }
        debug {
            isMinifyEnabled = false
        }
    }

    packaging {
        resources {
            //excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }

    lint {
        //checkDependencies = true
        //ignoreTestSources = true
        //resourcePrefix = "gnt_"
    }

    hilt {
        enableAggregatingTask = true
    }

    /*
        flavorDimensions += "environment"
        productFlavors {
            create("staging") {
                dimension = "environment"
                configure<CrashlyticsExtension> {
                    mappingFileUploadEnabled = false
                }
            }
            create("prod") {
                dimension = "environment"
                configure<CrashlyticsExtension> {
                    mappingFileUploadEnabled = true
                }
            }
        }
    */

}


dependencies {
    implementation(project(":core:ui"))
    implementation(project(":core:common"))
    implementation(project(":core:model"))
    implementation(project(":core:data"))
    implementation(project(":core:resource"))
    implementation(project(":core:widgetnotification"))
    implementation(project(":core:ads"))

    implementation(project(":feature:main"))
    implementation(project(":feature:map"))
    implementation(project(":feature:componentservice"))
    implementation(project(":feature:flickr"))
    implementation(project(":feature:weather"))
    implementation(project(":feature:sunsetrise"))
    implementation(project(":feature:settings"))
    implementation(project(":feature:searchlocation"))
    implementation(project(":feature:favorite"))
    implementation(project(":feature:airquality"))
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.appcompat)
    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.crashlytics)
    implementation(libs.firebase.analytics)
    implementation(libs.google.errorprone.annotations)
}