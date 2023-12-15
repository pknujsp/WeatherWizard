package io.github.pknujsp.weatherwizard

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import io.github.pknujsp.weatherwizard.core.data.notification.daily.DailyNotificationRepository
import io.github.pknujsp.weatherwizard.core.data.notification.ongoing.OngoingNotificationRepository
import io.github.pknujsp.weatherwizard.core.data.widget.WidgetRepository
import io.github.pknujsp.weatherwizard.feature.main.notification.NotificationStarter
import io.github.pknujsp.weatherwizard.feature.main.notification.NotificationStarterImpl
import io.github.pknujsp.weatherwizard.widget.WidgetStarter
import io.github.pknujsp.weatherwizard.widget.WidgetStarterImpl

@Module
@InstallIn(SingletonComponent::class)
class AppStartModule {

    @Provides
    fun providesNotificationStarter(
        ongoingNotificationRepository: OngoingNotificationRepository, dailyNotificationRepository: DailyNotificationRepository
    ): NotificationStarter = NotificationStarterImpl(ongoingNotificationRepository, dailyNotificationRepository)

    @Provides
    fun providesWidgetStarter(
        widgetRepository: WidgetRepository
    ): WidgetStarter = WidgetStarterImpl(widgetRepository)

}