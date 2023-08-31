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
                apply("com.google.devtools.ksp")
                apply("org.jetbrains.kotlin.plugin.parcelize")
            }

            extensions.configure<LibraryExtension> {
                configureKotlinAndroid(this)
                defaultConfig.targetSdk = libs.targetSdk
            }

            dependencies {
                "testImplementation"(libs.findBundle("test.local").get())
                "androidTestImplementation"(libs.findBundle("test.android").get())
            }
        }
    }

}