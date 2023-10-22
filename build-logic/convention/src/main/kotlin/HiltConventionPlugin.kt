import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.dependencies

class HiltConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            with(pluginManager) {
                apply("org.jetbrains.kotlin.kapt")
                apply("com.google.devtools.ksp")
                apply("com.google.dagger.hilt.android")
            }

            dependencies {
                "implementation"(libs.findBundle("hilt").get())
                "kapt"(libs.findLibrary("androidx.hilt.work.compilerKapt").get())
                "kapt"(libs.findLibrary("androidx.hilt.compilerKapt").get())
                //"ksp"(libs.findLibrary("dagger.compiler").get())
                //"ksp"(libs.findLibrary("hilt.compiler").get())
                "androidTestImplementation"(libs.findLibrary("androidx.hilt.android.testandroid").get())
            }
        }
    }

}