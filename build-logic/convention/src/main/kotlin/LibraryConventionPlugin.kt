import com.android.build.gradle.LibraryExtension
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.dependencies

class LibraryConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            with(pluginManager) {
                apply("org.jetbrains.kotlin.android")
                apply("com.android.library")
                apply("org.jetbrains.kotlin.kapt")
                apply("com.google.devtools.ksp")
                apply("org.jetbrains.kotlin.plugin.parcelize")
                apply("org.jetbrains.kotlin.plugin.serialization")
            }

            extensions.configure<LibraryExtension> {
                configureKotlinAndroid(this)
                defaultConfig.targetSdk = libs.targetSdk
            }

            dependencies {
                TEST_IMPLEMENTATION(libs.findBundle("test.local").get())
                ANDROID_TEST_IMPLEMENTATION(libs.findBundle("test.android").get())
                IMPLEMENTATION(libs.findBundle("ktx").get())
                IMPLEMENTATION(project(":core:resource"))
                IMPLEMENTATION(libs.findLibrary("androidx.core.remoteviews").get())
                IMPLEMENTATION(libs.findLibrary("google.guava").get())
            }
        }
    }

}