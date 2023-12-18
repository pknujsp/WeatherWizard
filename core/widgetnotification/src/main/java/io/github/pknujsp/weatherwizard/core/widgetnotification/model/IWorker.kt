package io.github.pknujsp.weatherwizard.core.widgetnotification.model

import android.content.Context
import android.content.pm.ServiceInfo
import android.os.Build
import androidx.work.ForegroundInfo
import io.github.pknujsp.weatherwizard.core.common.FeatureType
import io.github.pknujsp.weatherwizard.core.common.NotificationType
import io.github.pknujsp.weatherwizard.core.widgetnotification.widget.WidgetManager
import kotlin.properties.Delegates

interface IWorker {
    val name: String
    val requiredFeatures: Array<FeatureType>
    val workerId: Int
}

interface AppComponentService<T : ComponentServiceArgument> {

    suspend fun start(context: Context, argument: T)

}