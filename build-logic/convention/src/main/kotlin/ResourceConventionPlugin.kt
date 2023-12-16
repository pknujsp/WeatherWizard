import com.android.build.gradle.LibraryExtension
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.dependencies

class ResourceConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            with(pluginManager) {
                apply("org.jetbrains.kotlin.android")
                apply("com.android.library")
                apply("org.jetbrains.kotlin.kapt")
            }

            extensions.configure<LibraryExtension> {
                configureKotlinAndroid(this)
                defaultConfig.targetSdk = libs.targetSdk
            }

            dependencies {
                TEST_IMPLEMENTATION(libs.findBundle("test.local").get())
                ANDROID_TEST_IMPLEMENTATION(libs.findBundle("test.android").get())
                IMPLEMENTATION(libs.findLibrary("androidx.appcompat").get())
                IMPLEMENTATION(libs.findLibrary("material").get())
            }
        }
    }
}