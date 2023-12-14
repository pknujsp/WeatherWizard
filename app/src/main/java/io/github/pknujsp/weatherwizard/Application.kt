package io.github.pknujsp.weatherwizard

import android.app.Application
import androidx.hilt.work.HiltWorkerFactory
import androidx.work.Configuration
import dagger.hilt.android.HiltAndroidApp
import io.github.pknujsp.weatherwizard.core.common.coroutines.CoDispatcher
import io.github.pknujsp.weatherwizard.core.common.coroutines.CoDispatcherType
import io.github.pknujsp.weatherwizard.core.data.settings.SettingsRepository
import io.github.pknujsp.weatherwizard.feature.map.MapInitializer
import io.github.pknujsp.weatherwizard.notification.NotificationStarter
import io.github.pknujsp.weatherwizard.widget.WidgetStarter
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltAndroidApp
class Application : Application(), Configuration.Provider {
    @Inject lateinit var workerFactory: HiltWorkerFactory
    @Inject lateinit var appSettingsRepository: SettingsRepository
    @Inject @CoDispatcher(CoDispatcherType.IO) lateinit var ioDispatcher: CoroutineDispatcher
    @Inject lateinit var widgetStarter: WidgetStarter
    @Inject lateinit var notificationStarter: NotificationStarter

    override val workManagerConfiguration: Configuration
        get() = Configuration.Builder().setWorkerFactory(workerFactory).build()

    override fun onCreate() {
        super.onCreate()

        CoroutineScope(ioDispatcher).launch {
            appSettingsRepository.init()
            widgetStarter.start(this@Application)
            notificationStarter.start(this@Application)
            MapInitializer.initialize(applicationContext)
        }
    }
}