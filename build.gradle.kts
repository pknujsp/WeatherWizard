plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.android.library) apply false
    alias(libs.plugins.gms) apply false
    alias(libs.plugins.firebase.crashlytics) apply false
    alias(libs.plugins.daggerhilt) apply false
    alias(libs.plugins.kotlin.serialization) apply false
    alias(libs.plugins.kotlin.jvm) apply false
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.nav.safeargs.kotlin) apply false
    alias(libs.plugins.ksp) apply false
    alias(libs.plugins.kapt) apply false
    alias(libs.plugins.kotlin.parcelize) apply false
    alias(libs.plugins.ktlint) apply false
    alias(libs.plugins.detekt) apply false
    alias(libs.plugins.com.android.test) apply false
    // alias(libs.plugins.benchmark) apply false
}

/*
gradle.allprojects {
    tasks.withType<JavaCompile>().configureEach {
        options.compilerArgs = options.compilerArgs + "-Xmaxerrs" + "5000"
    }
}
*/

/*
allprojects {
    apply(plugin = "org.jlleitschuh.gradle.ktlint")
    apply(plugin = "io.gitlab.arturbosch.detekt")

    afterEvaluate {
        detekt {
            parallel = true
            buildUponDefaultConfig = true
            config.setFrom(files("$rootDir/detekt-config.yml"))
            debug = true
        }

        ktlint {
            debug = true
            verbose = true
        }
    }
}*/