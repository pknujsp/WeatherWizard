package io.github.pknujsp.weatherwizard

import android.app.Application
import androidx.hilt.work.HiltWorkerFactory
import androidx.lifecycle.viewModelScope
import androidx.work.Configuration
import dagger.hilt.android.HiltAndroidApp
import io.github.pknujsp.weatherwizard.core.data.RepositoryInitializer
import io.github.pknujsp.weatherwizard.core.data.settings.SettingsRepository
import io.github.pknujsp.weatherwizard.feature.map.MapInitializer
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.supervisorScope
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltAndroidApp
class Application : Application(), Configuration.Provider {
    @Inject lateinit var workerFactory: HiltWorkerFactory
    @Inject lateinit var appSettingsRepository: SettingsRepository
    @Inject lateinit var repositoryInitializer: RepositoryInitializer

    private var job: Job? = null

    override val workManagerConfiguration: Configuration
        get() = Configuration.Builder().setWorkerFactory(workerFactory).build()

    @OptIn(DelicateCoroutinesApi::class)
    override fun onCreate() {
        super.onCreate()
        job?.cancel()
        job = GlobalScope.launch(Dispatchers.Default) {
            MapInitializer.initialize(applicationContext)
            appSettingsRepository.init()
            supervisorScope {
                launch {
                    repositoryInitializer.initialize()
                }
            }
        }
    }

}