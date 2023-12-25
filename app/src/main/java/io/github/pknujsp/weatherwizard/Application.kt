package io.github.pknujsp.weatherwizard

import android.app.Application
import androidx.hilt.work.HiltWorkerFactory
import androidx.work.Configuration
import dagger.hilt.android.HiltAndroidApp
import io.github.pknujsp.weatherwizard.core.data.RepositoryInitializer
import io.github.pknujsp.weatherwizard.core.data.module.ScopeRepositoryModule
import io.github.pknujsp.weatherwizard.core.data.settings.SettingsRepository
import java.util.concurrent.Executors
import javax.inject.Inject
import javax.inject.Named

@HiltAndroidApp
class Application : Application(), Configuration.Provider {
    @Inject lateinit var workerFactory: HiltWorkerFactory

    @Inject @Named(ScopeRepositoryModule.SETTINGS_REPOSITORY) lateinit var settingsRepository: RepositoryInitializer

    override val workManagerConfiguration: Configuration
        get() = Configuration.Builder().setWorkerFactory(workerFactory).build()

    override fun onCreate() {
        super.onCreate()
        Executors.newSingleThreadExecutor().execute {
            suspend {
                settingsRepository.init()
            }
        }
    }
}