package io.github.pknujsp.weatherwizard

import android.app.Application
import androidx.hilt.work.HiltWorkerFactory
import androidx.work.Configuration
import dagger.hilt.android.HiltAndroidApp
import io.github.pknujsp.weatherwizard.feature.map.MapInitializer
import javax.inject.Inject

@HiltAndroidApp
class Application : Application(), Configuration.Provider {
    @Inject lateinit var workerFactory: HiltWorkerFactory

    override fun onCreate() {
        super.onCreate()
        MapInitializer.initialize(applicationContext)

        /*
        SoLoader.init(this, false)

        if (BuildConfig.DEBUG && FlipperUtils.shouldEnableFlipper(this)) {
            val client = AndroidFlipperClient.getInstance(this)

            ComponentsConfiguration.isDebugModeEnabled = true
            LithoFlipperDescriptors.add( DescriptorMapping.withDefaults())

            client.addPlugin(InspectorFlipperPlugin(this, DescriptorMapping.withDefaults()))
            client.addPlugin(CrashReporterPlugin.getInstance())
            client.start()
        }

         */
    }


    override fun getWorkManagerConfiguration(): Configuration {
        return Configuration.Builder()
            .setWorkerFactory(workerFactory)
            .build()
    }
}