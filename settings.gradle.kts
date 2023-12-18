pluginManagement {
    includeBuild("build-logic")
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
        maven { url = uri("https://www.jitpack.io") }
    }
}

@Suppress("UnstableApiUsage")
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        maven { url = uri("https://www.jitpack.io") }
    }
}

rootProject.name = "WeatherWizard"
include(":app")
include(":core")
include(":feature")
include(":core:data")
include(":core:model")
include(":core:network")
include(":core:ui")
include(":core:common")
include(":feature:weather")
include(":feature:main")
include(":feature:favorite")
include(":feature:settings")
include(":core:database")
include(":core:domain")
include(":feature:flickr")
include(":feature:map")
include(":feature:airquality")
include(":feature:sunsetrise")
include(":feature:notification")
include(":feature:widget")
include(":feature:searchlocation")
include(":core:resource")
include(":core:widgetnotification")