package io.github.pknujsp.weatherwizard

import android.app.Application
import androidx.hilt.work.HiltWorkerFactory
import androidx.work.Configuration
import dagger.hilt.android.HiltAndroidApp
import io.github.pknujsp.weatherwizard.core.data.settings.SettingsRepository
import io.github.pknujsp.weatherwizard.feature.map.MapInitializer
import javax.inject.Inject

@HiltAndroidApp
class Application : Application(), Configuration.Provider {
    @Inject lateinit var workerFactory: HiltWorkerFactory
    @Inject lateinit var appSettingsRepository: SettingsRepository

    override fun onCreate() {
        super.onCreate()
        MapInitializer.initialize(applicationContext)
        suspend {
            appSettingsRepository.init()
        }
    }


    override fun getWorkManagerConfiguration(): Configuration {
        return Configuration.Builder()
            .setWorkerFactory(workerFactory)
            .build()
    }
}