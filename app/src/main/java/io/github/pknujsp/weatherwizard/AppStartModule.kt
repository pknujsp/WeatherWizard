package io.github.pknujsp.weatherwizard

import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import io.github.pknujsp.weatherwizard.core.common.manager.AppAlarmManager
import io.github.pknujsp.weatherwizard.core.common.manager.WidgetManager
import io.github.pknujsp.weatherwizard.core.data.notification.daily.DailyNotificationRepository
import io.github.pknujsp.weatherwizard.core.data.notification.ongoing.OngoingNotificationRepository
import io.github.pknujsp.weatherwizard.core.data.widget.WidgetRepository
import io.github.pknujsp.weatherwizard.feature.main.notification.NotificationStarter
import io.github.pknujsp.weatherwizard.feature.main.notification.NotificationStarterImpl
import io.github.pknujsp.weatherwizard.core.widgetnotification.notification.AppNotificationManager
import io.github.pknujsp.weatherwizard.feature.notification.manager.NotificationAlarmManager
import io.github.pknujsp.weatherwizard.widget.WidgetStarter
import io.github.pknujsp.weatherwizard.widget.WidgetStarterImpl

@Module
@InstallIn(SingletonComponent::class)
object AppStartModule {

    @Provides
    fun providesNotificationStarter(
        @ApplicationContext context: Context,
        ongoingNotificationRepository: OngoingNotificationRepository,
        dailyNotificationRepository: DailyNotificationRepository,
        appAlarmManager: AppAlarmManager,
    ): NotificationStarter = NotificationStarterImpl(ongoingNotificationRepository,
        dailyNotificationRepository,
        appAlarmManager,
        AppNotificationManager(context),
        NotificationAlarmManager(appAlarmManager))

    @Provides
    fun providesWidgetStarter(
        widgetRepository: WidgetRepository, widgetManager: WidgetManager
    ): WidgetStarter = WidgetStarterImpl(widgetRepository, widgetManager)

}