import com.android.build.api.dsl.CommonExtension
import org.gradle.api.Project
import org.gradle.kotlin.dsl.dependencies
import org.gradle.kotlin.dsl.provideDelegate
import org.gradle.kotlin.dsl.withType
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

fun Project.configureKotlinAndroid(
    commonExtension: CommonExtension<*, *, *, *, *>,
) {

    commonExtension.apply {
        compileSdk = libs.compileSdk

        defaultConfig {
            minSdk = libs.minSdk
            testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        }

        viewBinding.enable = true

        compileOptions {
            sourceCompatibility = SharedProperty.javaCompatibility
            targetCompatibility = SharedProperty.javaCompatibility
            isCoreLibraryDesugaringEnabled = true
        }

    }

    configureKotlin()

    dependencies {
        add("coreLibraryDesugaring", libs.findLibrary("android.desugarjdklibs").get())
        add("implementation", libs.findBundle("kotlin").get())
        add("implementation", libs.findLibrary("androidx.work.ktx").get())
    }
}

private fun Project.configureKotlin() {
    tasks.withType<KotlinCompile>().configureEach {
        kotlinOptions {
            val warningsAsErrors: String? by project
            allWarningsAsErrors = warningsAsErrors.toBoolean()

            freeCompilerArgs = freeCompilerArgs + listOf(
                "-opt-in=kotlin.RequiresOptIn",
                "-opt-in=kotlinx.coroutines.ExperimentalCoroutinesApi",
                "-opt-in=kotlinx.coroutines.FlowPreview",
                "-opt-in=kotlin.Experimental",
            )

            jvmTarget = SharedProperty.jvmTarget
        }
    }
}

fun Project.applyCompose(
    commonExtension: CommonExtension<*, *, *, *, *>,
) {

    commonExtension.apply {
        buildFeatures.compose = true

        composeOptions {
            kotlinCompilerExtensionVersion = libs.findVersion("kotlin.compiler.extension").get().toString()
            useLiveLiterals = true
        }

    }
}