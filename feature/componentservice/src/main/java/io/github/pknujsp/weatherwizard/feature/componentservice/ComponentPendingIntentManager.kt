package io.github.pknujsp.weatherwizard.feature.componentservice

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import io.github.pknujsp.weatherwizard.core.common.enum.pendingIntentRequestFactory
import io.github.pknujsp.weatherwizard.core.widgetnotification.model.ComponentServiceAction
import io.github.pknujsp.weatherwizard.core.widgetnotification.model.ComponentServiceArgument

object ComponentPendingIntentManager {

    val mainActivityIntent: Intent
        get() = Intent().apply {
            val packageName = "io.github.pknujsp.wyther"
            val className = "io.github.pknujsp.weatherwizard.feature.main.MainActivity"
            setClassName(packageName, className)
            flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
        }

    //io.github.pknujsp.weatherwizard.feature.main.MainActivity@fc02654,
    //package: io.github.pknujsp.wyther,
    //componentName: ComponentInfo{io.github.pknujsp.wyther/io.github.pknujsp.weatherwizard.feature.main.MainActivity}

    fun <A : ComponentServiceAction<out ComponentServiceArgument>> getRefreshPendingIntent(
        context: Context, flags: Int, action: A
    ): PendingIntent? = PendingIntent.getBroadcast(context,
        pendingIntentRequestFactory.requestId(action::class),
        Intent(context, AppComponentServiceReceiver::class.java).apply {
            this.action = AppComponentServiceReceiver.ACTION_PROCESS
            putExtras(action.argument.toBundle())
        },
        flags)


    fun <A : ComponentServiceArgument> getIntent(
        context: Context, argument: A
    ): Intent = Intent(context, AppComponentServiceReceiver::class.java).apply {
        this.action = AppComponentServiceReceiver.ACTION_PROCESS
        putExtras(argument.toBundle())
    }

}