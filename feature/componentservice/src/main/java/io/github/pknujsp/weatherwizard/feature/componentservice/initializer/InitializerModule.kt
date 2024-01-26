package io.github.pknujsp.weatherwizard.feature.componentservice.initializer

import android.appwidget.AppWidgetManager
import android.content.Context
import android.content.Intent
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import io.github.pknujsp.weatherwizard.core.common.NotificationType
import io.github.pknujsp.weatherwizard.core.common.manager.AppAlarmManager
import io.github.pknujsp.weatherwizard.core.common.manager.AppNotificationManager
import io.github.pknujsp.weatherwizard.core.common.manager.WidgetManager
import io.github.pknujsp.weatherwizard.core.data.notification.daily.DailyNotificationRepository
import io.github.pknujsp.weatherwizard.core.data.notification.ongoing.OngoingNotificationRepository
import io.github.pknujsp.weatherwizard.core.data.settings.SettingsRepository
import io.github.pknujsp.weatherwizard.core.widgetnotification.model.OngoingNotificationServiceArgument
import io.github.pknujsp.weatherwizard.feature.componentservice.AppComponentServiceReceiver
import io.github.pknujsp.weatherwizard.feature.componentservice.ComponentPendingIntentManager
import io.github.pknujsp.weatherwizard.feature.componentservice.manager.DailyNotificationAlarmManager
import io.github.pknujsp.weatherwizard.feature.componentservice.manager.OngoingNotificationAlarmManager
import io.github.pknujsp.weatherwizard.feature.componentservice.widget.worker.WidgetAlarmManager
import kotlinx.coroutines.flow.firstOrNull
import javax.inject.Named

@Module
@InstallIn(SingletonComponent::class)
object InitializerModule {

    const val NOTIFICATION_INITIALIZER = "NOTIFICATION_INITIALIZER"
    const val WIDGET_INITIALIZER = "WIDGET_INITIALIZER"

    @Provides
    @Named(NOTIFICATION_INITIALIZER)
    fun providesNotificationInitializer(
        @ApplicationContext context: Context,
        ongoingNotificationRepository: OngoingNotificationRepository,
        dailyNotificationRepository: DailyNotificationRepository,
    ): AppComponentServiceIntializer = NotificationIntializerImpl(context,
        ongoingNotificationRepository,
        dailyNotificationRepository,
        AppNotificationManager.getInstance(context),
        OngoingNotificationAlarmManager.getInstance(context),
        DailyNotificationAlarmManager.getInstance(context))

    @Provides
    @Named(WIDGET_INITIALIZER)
    fun providesWidgetInitializer(
        @ApplicationContext context: Context, settingsRepository: SettingsRepository
    ): AppComponentServiceIntializer =
        WidgetStarterImpl(context, WidgetManager.getInstance(context), WidgetAlarmManager.getInstance(context), settingsRepository)

}

private class NotificationIntializerImpl(
    context: Context,
    private val ongoingNotificationRepository: OngoingNotificationRepository,
    private val dailyNotificationRepository: DailyNotificationRepository,
    private val appNotificationManager: AppNotificationManager,
    private val ongoingNotificationAlarmManager: OngoingNotificationAlarmManager,
    private val dailyNotificationAlarmManager: DailyNotificationAlarmManager,
) : AppComponentServiceIntializer(context) {

    private suspend fun getOngoingNotification() = ongoingNotificationRepository.getOngoingNotification().let {
        if (it.isInitialized) it else null
    }

    private suspend fun getDailyNotifications() = dailyNotificationRepository.getDailyNotifications().firstOrNull()

    private suspend fun initOngoingNotification() {
        getOngoingNotification()?.let {
            if (it.enabled && !appNotificationManager.isActiveNotification(NotificationType.ONGOING)) {
                context.sendBroadcast(ComponentPendingIntentManager.getIntent(context,
                    OngoingNotificationServiceArgument(),
                    AppComponentServiceReceiver.ACTION_AUTO_REFRESH))
                ongoingNotificationAlarmManager.scheduleAutoRefresh(context, it.data.refreshInterval)
            }
        }
    }

    private suspend fun initDailyNotifications() {
        getDailyNotifications()?.forEach { dailyNotification ->
            if (dailyNotification.enabled && !dailyNotificationAlarmManager.isScheduled(context, dailyNotification.id)) {
                dailyNotificationAlarmManager.schedule(context,
                    dailyNotification.id,
                    dailyNotification.data.hour,
                    dailyNotification.data.minute)
            }
        }
    }

    override suspend fun initialize() {
        initOngoingNotification()
        initDailyNotifications()
    }
}

private class WidgetStarterImpl(
    context: Context,
    private val widgetManager: WidgetManager,
    private val widgetAutoRefreshScheduler: WidgetAlarmManager,
    private val settingsRepository: SettingsRepository,
) : AppComponentServiceIntializer(context) {

    override suspend fun initialize() {
        val installedWidgetIds = widgetManager.installedAllWidgetIds
        if (installedWidgetIds.isEmpty()) {
            return
        }

        widgetManager.getProviderByWidgetId(installedWidgetIds.first())?.let { widgetProvider ->
            // 위젯 뷰 새로고침
            Intent().apply {
                action = AppWidgetManager.ACTION_APPWIDGET_UPDATE
                component = widgetProvider
                putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, installedWidgetIds.toIntArray())
                context.sendBroadcast(this)
            }

            // 위젯 자동 업데이트 예약
            val refreshInterval = settingsRepository.settings.replayCache.last().widgetAutoRefreshInterval
            if (widgetAutoRefreshScheduler.getScheduleState(context) is AppAlarmManager.ScheduledState.NotScheduled) {
                widgetAutoRefreshScheduler.scheduleAutoRefresh(context, refreshInterval)
            }
        }
    }

}