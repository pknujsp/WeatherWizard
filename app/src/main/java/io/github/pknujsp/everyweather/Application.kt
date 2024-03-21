package io.github.pknujsp.everyweather

import android.app.Application
import androidx.hilt.work.HiltWorkerFactory
import androidx.work.Configuration
import dagger.hilt.android.HiltAndroidApp
import io.github.pknujsp.everyweather.core.common.coroutines.CoDispatcher
import io.github.pknujsp.everyweather.core.common.coroutines.CoDispatcherType
import io.github.pknujsp.everyweather.core.data.RepositoryInitializer
import io.github.pknujsp.everyweather.core.data.module.RepositoryInitializerModule
import io.github.pknujsp.everyweather.feature.componentservice.initializer.AppComponentServiceIntializer
import io.github.pknujsp.everyweather.feature.componentservice.initializer.InitializerModule
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Named

@HiltAndroidApp
class Application : Application(), Configuration.Provider {
    @Inject lateinit var workerFactory: HiltWorkerFactory

    @Inject
    @CoDispatcher(CoDispatcherType.IO)
    lateinit var ioDispatcher: CoroutineDispatcher

    @Inject
    @Named(RepositoryInitializerModule.SETTINGS_REPOSITORY)
    lateinit var settingsRepository: RepositoryInitializer

    @Inject
    @Named(InitializerModule.WIDGET_INITIALIZER)
    lateinit var widgetInitializer: AppComponentServiceIntializer

    @Inject
    @Named(InitializerModule.NOTIFICATION_INITIALIZER)
    lateinit var notificationInitializer: AppComponentServiceIntializer

    override val workManagerConfiguration: Configuration
        get() = Configuration.Builder().setWorkerFactory(workerFactory).build()

    @OptIn(DelicateCoroutinesApi::class)
    override fun onCreate() {
        super.onCreate()
        GlobalScope.launch(ioDispatcher) {
            settingsRepository.initialize()
            widgetInitializer.initialize()
            notificationInitializer.initialize()
        }
    }
}
