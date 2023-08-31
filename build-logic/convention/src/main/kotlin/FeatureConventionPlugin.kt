import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.dependencies

class FeatureConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            pluginManager.apply {
                apply("plugin.android.library")
                apply("plugin.android.hilt")
                apply("androidx.navigation.safeargs.kotlin")
            }

            dependencies {
                "implementation"(libs.findBundle("navigation").get())
                "implementation"(libs.findBundle("lifecycle").get())
                "implementation"(platform(libs.findLibrary("androidx.compose.bom").get()))
                "implementation"(libs.findBundle("compose.bom").get())
                "debugImplementation"(libs.findBundle("compose.debug").get())
                "implementation"(libs.findBundle("ktx").get())
                "kapt"(libs.findLibrary("androidx.lifecycle.compilerKapt").get())
            }
        }
    }
}