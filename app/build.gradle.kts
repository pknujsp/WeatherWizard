plugins {
    id("plugin.android.application")
    alias(libs.plugins.gms)
    alias(libs.plugins.firebase.crashlytics)
    alias(libs.plugins.kotlin.android)
}

android {
    namespace = "io.github.pknujsp.weatherwizard"

    defaultConfig {
        applicationId = "io.github.pknujsp.wyther"
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

    /*  with(aaptOptions) {
          noCompress.add("qz")
          noCompress.add("png")
      }

      with(packagingOptions) {
          setPickFirsts(setOf("lib/armeabi-v7a/libc++_shared.so",
              "lib/arm64-v8a/libc++_shared.so",
              "lib/x86_64/libc++_shared.so",
              "lib/x86/libc++_shared.so"))
      }

      flavorDimensions.addAll(listOf("coreversion", "abi"))
      productFlavors {
          register("armonly") {
              dimension = "abi"
              ndk {
                  abiFilters.addAll(listOf("arm64-v8a", "armeabi-v7a"))
              }
          }
          register("fat") {
              dimension = "abi"
              ndk {
                  abiFilters.addAll(listOf("arm64-v8a", "x86", "x86_64", "armeabi-v7a"))
              }
          }
          register("legacy") {
              dimension = "coreversion"
          }
          register("opengl") {
              dimension = "coreversion"
          }
      }*/

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
    implementation(project(":core:data"))
    implementation(project(":core:resource"))
    implementation(project(":core:widgetnotification"))

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
}