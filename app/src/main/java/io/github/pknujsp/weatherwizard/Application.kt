package io.github.pknujsp.weatherwizard

import android.app.Application
import androidx.hilt.work.HiltWorkerFactory
import androidx.work.Configuration
import dagger.hilt.android.HiltAndroidApp
import io.github.pknujsp.weatherwizard.core.common.coroutines.CoDispatcher
import io.github.pknujsp.weatherwizard.core.common.coroutines.CoDispatcherType
import io.github.pknujsp.weatherwizard.core.data.settings.SettingsRepository
import io.github.pknujsp.weatherwizard.widget.WidgetStarter
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltAndroidApp
class Application : Application(), Configuration.Provider {
    @Inject lateinit var workerFactory: HiltWorkerFactory
    @Inject @CoDispatcher(CoDispatcherType.SINGLE) lateinit var dispatcher: CoroutineDispatcher
    @Inject lateinit var appSettingsRepository: SettingsRepository
    @Inject lateinit var widgetStarter: WidgetStarter

    override val workManagerConfiguration: Configuration
        get() = Configuration.Builder().setWorkerFactory(workerFactory).build()

    override fun onCreate() {
        super.onCreate()

        CoroutineScope(dispatcher).launch {
            appSettingsRepository.init()
            widgetStarter.start(this@Application)
        }
    }
}