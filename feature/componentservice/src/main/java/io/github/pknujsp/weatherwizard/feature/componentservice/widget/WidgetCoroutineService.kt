package io.github.pknujsp.weatherwizard.feature.componentservice.widget

import android.appwidget.AppWidgetManager
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.hilt.work.HiltWorker
import androidx.work.WorkerParameters
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import io.github.pknujsp.weatherwizard.core.common.FeatureType
import io.github.pknujsp.weatherwizard.core.common.manager.FeatureState
import io.github.pknujsp.weatherwizard.core.common.manager.FeatureStatusManager
import io.github.pknujsp.weatherwizard.core.common.manager.WidgetManager
import io.github.pknujsp.weatherwizard.core.common.module.KtJson
import io.github.pknujsp.weatherwizard.core.model.JsonParser
import io.github.pknujsp.weatherwizard.core.model.coordinate.LocationType
import io.github.pknujsp.weatherwizard.core.model.widget.WidgetStatus
import io.github.pknujsp.weatherwizard.core.widgetnotification.model.AppComponentCoroutineService
import io.github.pknujsp.weatherwizard.core.widgetnotification.model.IWorker
import io.github.pknujsp.weatherwizard.core.widgetnotification.model.LoadWidgetDataArgument
import io.github.pknujsp.weatherwizard.feature.componentservice.widget.worker.WidgetRemoteViewModel
import kotlinx.serialization.json.Json

@HiltWorker
class WidgetCoroutineService @AssistedInject constructor(
    @Assisted val context: Context,
    @Assisted params: WorkerParameters,
    private val widgetRemoteViewModel: WidgetRemoteViewModel,
    private val featureStatusManager: FeatureStatusManager,
    private val widgetManager: WidgetManager,
    @KtJson json: Json
) : AppComponentCoroutineService<LoadWidgetDataArgument>(context, params, Companion) {

    private val jsonParser by lazy { JsonParser(json) }

    companion object : IWorker {
        override val name: String = "WidgetCoroutineService"
        override val requiredFeatures: Array<FeatureType> = arrayOf(FeatureType.NETWORK)
        override val workerId: Int = name.hashCode()
    }

    override suspend fun doWork(context: Context, argument: LoadWidgetDataArgument): Result {
        start(context, argument)
        return Result.success()
    }

    private suspend fun start(context: Context, argument: LoadWidgetDataArgument) {
        val widgetEntityList = widgetRemoteViewModel.loadWidgets(argument.widgetId, argument.action)

        if (widgetEntityList.widgetSettings.isEmpty()) {
            return
        }

        if (featureStatusManager.status(context, requiredFeatures) is FeatureState.Unavailable) {
            for (widget in widgetEntityList.widgetSettings) {
                widgetRemoteViewModel.updateResponseData(widget.id, WidgetStatus.RESPONSE_FAILURE)
            }
            sendBroadcast(widgetEntityList.widgetSettings.map { it.id })
            return
        }

        val failedWidgetIds = mutableListOf<Int>()

        if (LocationType.CurrentLocation in widgetEntityList.locationTypeGroups && featureStatusManager.status(context,
                arrayOf(FeatureType.LOCATION_PERMISSION, FeatureType.LOCATION_SERVICE)) is FeatureState.Unavailable) {
            widgetEntityList.locationTypeGroups.getValue(LocationType.CurrentLocation).forEach {
                widgetRemoteViewModel.updateResponseData(it.id, WidgetStatus.RESPONSE_FAILURE)
                failedWidgetIds.add(it.id)
            }
            sendBroadcast(failedWidgetIds)

            if (widgetEntityList.widgetSettings.size == failedWidgetIds.size) {
                return
            }
        }

        val responses = widgetRemoteViewModel.load(failedWidgetIds)
        responses.forEach { state ->
            if (state.isSuccessful) {
                widgetRemoteViewModel.updateResponseData(state.widget.id,
                    WidgetStatus.RESPONSE_SUCCESS,
                    state.toWidgetResponseDBModel(jsonParser).toByteArray(jsonParser))
            } else {
                widgetRemoteViewModel.updateResponseData(state.widget.id, WidgetStatus.RESPONSE_FAILURE)
            }
        }

        sendBroadcast(responses.map { it.widget.id })
    }

    private fun sendBroadcast(widgetIds: List<Int>) {
        widgetManager.getProviderByWidgetId(widgetIds.first())?.let { widgetProvider ->
            Intent(context, widgetProvider.javaClass).apply {
                action = AppWidgetManager.ACTION_APPWIDGET_UPDATE
                putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, widgetIds.toTypedArray())
                context.sendBroadcast(this)
                Log.d("WidgetCoroutineService", "sendBroadcast: $widgetIds")
            }
        }
    }
}