package io.github.pknujsp.weatherwizard.feature.componentservice

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.content.ContextCompat
import io.github.pknujsp.weatherwizard.core.common.enum.pendingIntentRequestFactory
import io.github.pknujsp.weatherwizard.core.widgetnotification.model.ComponentServiceAction
import io.github.pknujsp.weatherwizard.core.widgetnotification.model.ComponentServiceArgument

object ComponentPendingIntentManager {

    val mainActivityIntent: Intent
        get() = Intent().apply {
            val packageName = "io.github.pknujsp.weatherwizard.feature.main"
            val className = "MainActivity"
            setClassName(packageName, className)
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }


    fun <A : ComponentServiceAction<out ComponentServiceArgument>> getRefreshPendingIntent(
        context: Context, flags: Int, action: A
    ): PendingIntent = PendingIntent.getBroadcast(context,
        pendingIntentRequestFactory.requestId(action::class),
        Intent(context, NotificationServiceReceiver::class.java).apply {
            this.action = NotificationServiceReceiver.ACTION_PROCESS
            putExtras(action.argument.toBundle())
        },
        flags)

    fun <A : ComponentServiceArgument> getIntent(
        context: Context, argument: A
    ): Intent = Intent(context, NotificationServiceReceiver::class.java).apply {
        this.action = NotificationServiceReceiver.ACTION_PROCESS
        putExtras(argument.toBundle())
    }

}