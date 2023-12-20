package io.github.pknujsp.weatherwizard.feature.componentservice.widget.worker

import android.app.PendingIntent
import android.content.Context
import androidx.work.ListenableWorker
import dagger.hilt.android.qualifiers.ApplicationContext
import io.github.pknujsp.weatherwizard.core.common.FeatureType
import io.github.pknujsp.weatherwizard.core.common.manager.FeatureState
import io.github.pknujsp.weatherwizard.core.common.manager.FeatureStatusManager
import io.github.pknujsp.weatherwizard.core.common.manager.WidgetManager
import io.github.pknujsp.weatherwizard.core.data.widget.WidgetRepository
import io.github.pknujsp.weatherwizard.core.data.widget.WidgetSettingsEntity
import io.github.pknujsp.weatherwizard.core.domain.weather.WeatherResponseState
import io.github.pknujsp.weatherwizard.core.model.RemoteViewUiModel
import io.github.pknujsp.weatherwizard.core.model.coordinate.LocationType
import io.github.pknujsp.weatherwizard.core.resource.R
import io.github.pknujsp.weatherwizard.core.widgetnotification.model.AppComponentBackgroundService
import io.github.pknujsp.weatherwizard.core.widgetnotification.model.ComponentServiceAction
import io.github.pknujsp.weatherwizard.core.widgetnotification.model.IWorker
import io.github.pknujsp.weatherwizard.core.widgetnotification.model.LoadWidgetDataArgument
import io.github.pknujsp.weatherwizard.core.widgetnotification.model.WidgetUpdatedArgument
import io.github.pknujsp.weatherwizard.core.widgetnotification.remoteview.DefaultRemoteViewCreator
import io.github.pknujsp.weatherwizard.core.widgetnotification.remoteview.RemoteViewCreator
import io.github.pknujsp.weatherwizard.core.widgetnotification.remoteview.RemoteViewsCreatorManager
import io.github.pknujsp.weatherwizard.core.widgetnotification.remoteview.UiStateRemoteViewCreator
import io.github.pknujsp.weatherwizard.core.widgetnotification.widget.remoteview.WidgetRemoteViewsCreator
import io.github.pknujsp.weatherwizard.feature.componentservice.ComponentPendingIntentManager
import io.github.pknujsp.weatherwizard.feature.componentservice.widget.WidgetActivity
import javax.inject.Inject

class WidgetUpdateBackgroundService @Inject constructor(
    @ApplicationContext context: Context, private val widgetRepository: WidgetRepository,
    private val featureStatusManager: FeatureStatusManager,
    private val widgetManager: WidgetManager
) : AppComponentBackgroundService<WidgetUpdatedArgument>(context) {

    override val id: Int = "WidgetUpdateBackgroundService".hashCode()

    companion object : IWorker {
        override val name: String = "WidgetCoroutineService"
        override val requiredFeatures: Array<FeatureType> = arrayOf(FeatureType.NETWORK)
        override val workerId: Int = name.hashCode()
    }

    override suspend fun doWork(context: Context, argument: LoadWidgetDataArgument): ListenableWorker.Result {
        start(context, argument)
        return ListenableWorker.Result.success()
    }

    private suspend fun start(context: Context, argument: LoadWidgetDataArgument) {
        val action = argument.action
        val appWidgetIds = argument.widgetIds
        val widgetEntityList = widgetRemoteViewModel.loadWidgets()

        // 네트워크 연결 상태 확인, 연결이 안되어 있다면 위젯에 네트워크 연결 상태를 표시
        if (!checkFeatureStateAndUpdateWidgets(requiredFeatures, appWidgetIds, context)) {
            return
        }

        var excludeLocationType: LocationType? = null
        if (action == LoadWidgetDataArgument.UPDATE_ONLY_ON_CURRENT_LOCATION) {
            excludeLocationType = LocationType.CustomLocation
        }

        val excludeWidgets = mutableSetOf<WidgetSettingsEntity>()

        // 위젯이 활성화되어 있지 않다면 DB에서 삭제
        widgetEntityList.widgetSettings.forEach {
            if (!widgetManager.isBind(it.id)) {
                excludeWidgets.add(it)
            }
        }

        if (widgetEntityList.locationTypeGroups.getValue(LocationType.CurrentLocation).isNotEmpty()) {
            if (!checkFeatureStateAndUpdateWidgets(arrayOf(FeatureType.LOCATION_PERMISSION, FeatureType.LOCATION_SERVICE),
                    widgetEntityList.locationTypeGroups.getValue(LocationType.CurrentLocation).map {
                        it.id
                    }.toTypedArray(),
                    context)) {
                widgetEntityList.locationTypeGroups.getValue(LocationType.CurrentLocation).forEach {
                    excludeWidgets.add(it)
                }
            }
        }

        with(widgetRemoteViewModel.load(excludeWidgets, excludeLocationType)) {
            val failedWidgetIds = filter { it.state is WeatherResponseState.Failure }.map { it.widget.id }.toTypedArray()
            val retryPendingIntent = if (failedWidgetIds.isNotEmpty()) {
                io.github.pknujsp.weatherwizard.feature.componentservice.ComponentPendingIntentManager.getRefreshPendingIntent(context,
                    android.app.PendingIntent.FLAG_IMMUTABLE or android.app.PendingIntent.FLAG_UPDATE_CURRENT,
                    io.github.pknujsp.weatherwizard.core.widgetnotification.model.ComponentServiceAction.LoadWidgetData(
                        LoadWidgetDataArgument(io.github.pknujsp.weatherwizard.core.widgetnotification.model.LoadWidgetDataArgument.UPDATE_ONLY_SPECIFIC_WIDGETS,
                        failedWidgetIds)))
            } else {
                null
            }

            forEach { model ->
                val remoteView = when (model.state) {
                    is WeatherResponseState.Success -> {
                        val creator: WidgetRemoteViewsCreator<RemoteViewUiModel> =
                            io.github.pknujsp.weatherwizard.core.widgetnotification.remoteview.RemoteViewsCreatorManager.getByWidgetType(model.widget.widgetType)

                        creator.createContentView(model.map(widgetRemoteViewModel.units),
                            io.github.pknujsp.weatherwizard.core.widgetnotification.remoteview.DefaultRemoteViewCreator.Header(model.state.location.address, model.updatedTime),
                            context)
                    }

                    else -> {
                        io.github.pknujsp.weatherwizard.core.widgetnotification.remoteview.UiStateRemoteViewCreator.createView(context,
                            io.github.pknujsp.weatherwizard.core.resource.R.string.title_failed_to_load_data,
                            io.github.pknujsp.weatherwizard.core.resource.R.string.failed_to_load_data,
                            io.github.pknujsp.weatherwizard.core.resource.R.string.refresh,
                            io.github.pknujsp.weatherwizard.core.widgetnotification.remoteview.RemoteViewCreator.ContainerType.WIDGET,
                            pendingIntent = retryPendingIntent!!)
                    }
                }

                widgetManager.updateWidget(model.widget.id, remoteView, context, io.github.pknujsp.weatherwizard.feature.componentservice.widget.WidgetActivity::class)
            }
        }
    }


    private fun checkFeatureStateAndUpdateWidgets(featureTypes: Array<FeatureType>, widgetIds: Array<Int>, context: Context): Boolean {
        return when (val state = featureStatusManager.status(context, featureTypes)) {
            is FeatureState.Unavailable -> {
                val remoteViews = UiStateRemoteViewCreator.createView(context,
                    state.featureType.failedReason,
                    RemoteViewCreator.ContainerType.WIDGET,
                    pendingIntent = state.featureType.getPendingIntent(context),
                    visibilityOfCompleteButton = true)
                widgetIds.forEach {
                    widgetManager.updateWidget(it, remoteViews, context, WidgetActivity::class)
                }
                false
            }

            else -> true
        }
    }
}