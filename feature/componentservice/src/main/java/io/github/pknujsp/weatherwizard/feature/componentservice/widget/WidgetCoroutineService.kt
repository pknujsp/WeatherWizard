package io.github.pknujsp.weatherwizard.feature.componentservice.widget

import android.content.Context
import android.util.Log
import android.widget.RemoteViews
import androidx.hilt.work.HiltWorker
import androidx.work.WorkerParameters
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import io.github.pknujsp.weatherwizard.core.FeatureStateManager
import io.github.pknujsp.weatherwizard.core.common.FeatureType
import io.github.pknujsp.weatherwizard.core.common.coroutines.CoDispatcher
import io.github.pknujsp.weatherwizard.core.common.coroutines.CoDispatcherType
import io.github.pknujsp.weatherwizard.core.common.manager.WidgetManager
import io.github.pknujsp.weatherwizard.core.common.module.KtJson
import io.github.pknujsp.weatherwizard.core.data.settings.SettingsRepository
import io.github.pknujsp.weatherwizard.core.data.widget.WidgetRepository
import io.github.pknujsp.weatherwizard.core.model.JsonParser
import io.github.pknujsp.weatherwizard.core.model.coordinate.LocationType
import io.github.pknujsp.weatherwizard.core.model.widget.WidgetStatus
import io.github.pknujsp.weatherwizard.core.resource.R
import io.github.pknujsp.weatherwizard.core.widgetnotification.model.AppComponentCoroutineService
import io.github.pknujsp.weatherwizard.core.widgetnotification.model.IWorker
import io.github.pknujsp.weatherwizard.core.widgetnotification.model.LoadWidgetDataArgument
import io.github.pknujsp.weatherwizard.core.widgetnotification.model.WidgetUpdatedArgument
import io.github.pknujsp.weatherwizard.feature.componentservice.widget.worker.AppWidgetViewUpdater
import io.github.pknujsp.weatherwizard.feature.componentservice.widget.worker.WidgetRemoteViewModel
import io.github.pknujsp.weatherwizard.feature.componentservice.widget.worker.WidgetViewCacheManagerFactory
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.serialization.json.Json

@HiltWorker
class WidgetCoroutineService @AssistedInject constructor(
    @Assisted val context: Context,
    @Assisted params: WorkerParameters,
    private val widgetRemoteViewModel: WidgetRemoteViewModel,
    private val widgetManager: WidgetManager,
    @KtJson json: Json,
    appSettingsRepository: SettingsRepository,
    private val widgetRepository: WidgetRepository,
    @CoDispatcher(CoDispatcherType.SINGLE) private val dispatcher: CoroutineDispatcher,
) : AppComponentCoroutineService<LoadWidgetDataArgument>(context, params, Companion) {

    private val jsonParser by lazy { JsonParser(json) }

    companion object : IWorker {
        override val name: String = "WidgetCoroutineService"
        override val requiredFeatures: Array<FeatureType> = arrayOf(FeatureType.NETWORK)
        override val workerId: Int = name.hashCode()
    }

    private val updateAppWidgetViews = AppWidgetViewUpdater(widgetManager,
        widgetRepository,
        featureStateManager,
        appSettingsRepository,
        WidgetViewCacheManagerFactory.getInstance(dispatcher))

    override suspend fun doWork(context: Context, argument: LoadWidgetDataArgument): Result {
        try {
            setLoadingViewToAllWidgets(context)
            start(context, argument)
        } catch (e: Exception) {
            e.printStackTrace()
            updateAppWidgetViews(WidgetUpdatedArgument(WidgetUpdatedArgument.UPDATE_ALL), context)
        } finally {

        }
        return Result.success()
    }

    private suspend fun start(context: Context, argument: LoadWidgetDataArgument) {
        val widgetEntityList = widgetRemoteViewModel.loadWidgets(argument.widgetId, argument.action)

        if (widgetEntityList.widgetSettings.isEmpty()) {
            return
        }

        if (featureStateManager.retrieveFeaturesState(requiredFeatures, context) is FeatureStateManager.FeatureState.Unavailable) {
            for (widget in widgetEntityList.widgetSettings) {
                widgetRemoteViewModel.updateResponseData(widget.id, WidgetStatus.RESPONSE_FAILURE)
            }
            val failedWidgetIds = widgetEntityList.widgetSettings.map { it.id }
            updateWidgetViews(failedWidgetIds)
            return
        }

        val failedWidgetIds = mutableListOf<Int>()

        if (LocationType.CurrentLocation in widgetEntityList.locationTypeGroups && featureStateManager.retrieveFeaturesState(arrayOf(
                FeatureType.LOCATION_PERMISSION,
                FeatureType.LOCATION_SERVICE,
                FeatureType.BACKGROUND_LOCATION_PERMISSION), context) is FeatureStateManager.FeatureState.Unavailable) {
            widgetEntityList.locationTypeGroups.getValue(LocationType.CurrentLocation).forEach {
                widgetRemoteViewModel.updateResponseData(it.id, WidgetStatus.RESPONSE_FAILURE)
                failedWidgetIds.add(it.id)
            }
            updateWidgetViews(failedWidgetIds)

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

        val completedWidgetIds = responses.map { it.widget.id }
        updateWidgetViews(completedWidgetIds)
    }

    private suspend fun updateWidgetViews(widgetIds: List<Int>) {/*widgetManager.getProviderByWidgetId(widgetIds.first())?.let { widgetProvider ->
            Intent(context, widgetProvider.javaClass).apply {
                action = AppWidgetManager.ACTION_APPWIDGET_UPDATE
                putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, widgetIds.toTypedArray())
                context.sendBroadcast(this)
            }
        }*/

        Log.d("WidgetCoroutineService", "updateAppWidgetViews: $widgetIds")
        updateAppWidgetViews(WidgetUpdatedArgument(WidgetUpdatedArgument.UPDATE_ONLY_SPECIFIC_WIDGETS, widgetIds.toTypedArray()), context)
    }

    private fun setLoadingViewToAllWidgets(context: Context) {
        val installedWidgets = widgetManager.installedAllWidgetIds
        if (installedWidgets.isNotEmpty()) {
            widgetManager.appWidgetManager.updateAppWidget(installedWidgets.toIntArray(),
                RemoteViews(context.packageName, R.layout.view_loading_widget))
        }
    }
}