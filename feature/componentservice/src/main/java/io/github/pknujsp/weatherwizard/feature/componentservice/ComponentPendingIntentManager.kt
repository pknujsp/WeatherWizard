package io.github.pknujsp.weatherwizard.feature.componentservice

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import io.github.pknujsp.weatherwizard.core.common.enum.pendingIntentRequestFactory
import io.github.pknujsp.weatherwizard.core.widgetnotification.model.ComponentServiceAction
import io.github.pknujsp.weatherwizard.core.widgetnotification.model.ComponentServiceArgument

object ComponentPendingIntentManager {
    fun <A : ComponentServiceAction<out ComponentServiceArgument>> getRefreshPendingIntent(
        context: Context, flags: Int, notificationAction: A
    ): PendingIntent = PendingIntent.getBroadcast(context,
        pendingIntentRequestFactory.requestId(notificationAction::class),
        Intent(context, NotificationServiceReceiver::class.java).apply {
            action = NotificationService.ACTION_PROCESS
            putExtras(notificationAction.argument.toBundle())
        },
        flags)
}