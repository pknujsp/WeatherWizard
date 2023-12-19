package io.github.pknujsp.weatherwizard.core.widgetnotification.model

import android.content.Context
import io.github.pknujsp.weatherwizard.core.common.FeatureType

interface IWorker {
    val name: String
    val requiredFeatures: Array<FeatureType>
    val workerId: Int
}

interface AppComponentService<T : ComponentServiceArgument> {

    suspend fun start(context: Context, argument: T)

}