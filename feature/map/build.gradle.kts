plugins {
    id("plugin.android.feature")
}

android {
    namespace = "io.github.pknujsp.weatherwizard.feature.map"


}

dependencies {
    implementation(project(":core:domain"))
    implementation(project(":core:data"))
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.constraintlayout.compose)
    implementation(libs.osmdroid)

    /*  implementation("net.osmand:OsmAnd-java:master-snapshot:android@jar")
      debugImplementation("net.osmand:OsmAnd-java:master-snapshot:debug@jar")
      releaseImplementation("net.osmand:OsmAnd-java:master-snapshot:release@jar")

      implementation(fileTree(baseDir = "libs").apply {
          include("QtAndroid.jar", "QtAndroidBearer.jar")
      })
      implementation("net.osmand:antpluginlib:3.8.0@aar")
      implementation("net.osmand:OsmAndCore_androidNativeRelease:master-snapshot@aar")
      implementation("net.osmand:OsmAndCore_android:master-snapshot@aar")
      implementation(group = "commons-logging", name = "commons-logging", version = "1.2")
      implementation("commons-codec:commons-codec:1.11")
      implementation("org.apache.commons:commons-compress:1.17")
      implementation("com.moparisthebest:junidecode:0.1.1")
      implementation("org.immutables:gson:2.5.0")
      implementation("com.vividsolutions:jts-core:1.14.0")
      implementation("com.google.openlocationcode:openlocationcode:1.0.4")

      implementation(group = "org.mozilla", name = "rhino", version = "1.7.9")

      implementation("com.github.scribejava:scribejava-apis:7.1.1") {
          exclude("com.fasterxml.jackson.core")
      }
      implementation("com.jaredrummler:colorpicker:1.1.0")
      implementation("me.zhanghai.android.materialprogressbar:library:1.4.2")

      implementation("net.osmand:MPAndroidChart:custom-snapshot-debug@aar")
      implementation("net.osmand:MPAndroidChart:custom-snapshot-release@aar")*/
}