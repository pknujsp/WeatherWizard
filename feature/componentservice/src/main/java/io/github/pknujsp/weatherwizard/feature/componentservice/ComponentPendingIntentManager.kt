package io.github.pknujsp.weatherwizard.feature.componentservice

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import io.github.pknujsp.weatherwizard.core.widgetnotification.model.ComponentServiceAction
import io.github.pknujsp.weatherwizard.core.widgetnotification.model.ComponentServiceArgument

object ComponentPendingIntentManager {

    val mainActivityIntent: Intent
        get() = Intent().apply {
            val packageName = "io.github.pknujsp.weathernet"
            val className = "io.github.pknujsp.weatherwizard.feature.main.MainActivity"
            setClassName(packageName, className)
            flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
        }

    //io.github.pknujsp.weatherwizard.feature.main.MainActivity@fc02654,
    //package: io.github.pknujsp.weathernet,
    //componentName: ComponentInfo{io.github.pknujsp.wyther/io.github.pknujsp.weatherwizard.feature.main.MainActivity}

    fun <A : ComponentServiceAction<out ComponentServiceArgument>> getPendingIntent(
        context: Context, flags: Int, action: A, requestCode: Int = action::class.simpleName.hashCode(), actionString: String
    ): PendingIntentEntity {
        val intent = Intent(context, AppComponentServiceReceiver::class.java).apply {
            this.action = actionString
            putExtras(action.argument.toBundle())
        }
        val pendingIntent = PendingIntent.getBroadcast(context, requestCode, intent, flags)
        return PendingIntentEntity(requestCode = requestCode, intent = intent, pendingIntent = pendingIntent)
    }


    fun <A : ComponentServiceArgument> getIntent(
        context: Context, argument: A, actionString: String
    ): Intent = Intent(context, AppComponentServiceReceiver::class.java).apply {
        this.action = actionString
        putExtras(argument.toBundle())
    }

}

class PendingIntentEntity(
    val requestCode: Int,
    val intent: Intent,
    val pendingIntent: PendingIntent? = null,
)