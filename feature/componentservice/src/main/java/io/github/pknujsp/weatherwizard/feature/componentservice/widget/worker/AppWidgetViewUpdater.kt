package io.github.pknujsp.weatherwizard.feature.componentservice.widget.worker

import android.app.PendingIntent
import android.content.Context
import android.widget.RemoteViews
import io.github.pknujsp.weatherwizard.core.common.FeatureType
import io.github.pknujsp.weatherwizard.core.common.manager.AppAlarmManager
import io.github.pknujsp.weatherwizard.core.common.manager.FailedReason
import io.github.pknujsp.weatherwizard.core.common.manager.FeatureState
import io.github.pknujsp.weatherwizard.core.common.manager.FeatureStatusManager
import io.github.pknujsp.weatherwizard.core.common.manager.WidgetManager
import io.github.pknujsp.weatherwizard.core.data.cache.CacheManager
import io.github.pknujsp.weatherwizard.core.data.settings.SettingsRepository
import io.github.pknujsp.weatherwizard.core.data.widget.WidgetRepository
import io.github.pknujsp.weatherwizard.core.data.widget.WidgetResponseDBEntity
import io.github.pknujsp.weatherwizard.core.model.RemoteViewUiModel
import io.github.pknujsp.weatherwizard.core.model.coordinate.LocationType
import io.github.pknujsp.weatherwizard.core.model.notification.enums.RefreshInterval
import io.github.pknujsp.weatherwizard.core.model.widget.WidgetStatus
import io.github.pknujsp.weatherwizard.core.model.widget.WidgetType
import io.github.pknujsp.weatherwizard.core.widgetnotification.model.ComponentServiceAction
import io.github.pknujsp.weatherwizard.core.widgetnotification.model.IWorker
import io.github.pknujsp.weatherwizard.core.widgetnotification.model.LoadWidgetDataArgument
import io.github.pknujsp.weatherwizard.core.widgetnotification.model.RemoteViewUiModelMapperManager
import io.github.pknujsp.weatherwizard.core.widgetnotification.model.WidgetUpdatedArgument
import io.github.pknujsp.weatherwizard.core.widgetnotification.remoteview.DefaultRemoteViewCreator
import io.github.pknujsp.weatherwizard.core.widgetnotification.remoteview.RemoteViewCreator
import io.github.pknujsp.weatherwizard.core.widgetnotification.remoteview.RemoteViewsCreatorManager
import io.github.pknujsp.weatherwizard.core.widgetnotification.remoteview.UiStateRemoteViewCreator
import io.github.pknujsp.weatherwizard.core.widgetnotification.widget.remoteview.WidgetRemoteViewsCreator
import io.github.pknujsp.weatherwizard.feature.componentservice.ComponentPendingIntentManager
import io.github.pknujsp.weatherwizard.feature.componentservice.widget.WidgetActivity
import io.github.pknujsp.weatherwizard.feature.componentservice.widget.isInProgress


class AppWidgetViewUpdater private constructor(
    private val widgetManager: WidgetManager,
    private val widgetRepository: WidgetRepository,
    private val featureStatusManager: FeatureStatusManager,
    private val appSettingsRepository: SettingsRepository,
    private val remoteViewsCacheManager: CacheManager<Int, RemoteViews>
) {
    companion object {
        private val requiredFeatures: Array<FeatureType> = arrayOf(FeatureType.NETWORK)

        @Volatile private var instance: AppWidgetViewUpdater? = null

        fun getInstance(
            widgetManager: WidgetManager,
            widgetRepository: WidgetRepository,
            featureStatusManager: FeatureStatusManager,
            appSettingsRepository: SettingsRepository,
            remoteViewsCacheManager: CacheManager<Int, RemoteViews>
        ): AppWidgetViewUpdater {
            return synchronized(this) {
                instance ?: AppWidgetViewUpdater(widgetManager,
                    widgetRepository,
                    featureStatusManager,
                    appSettingsRepository,
                    remoteViewsCacheManager).also {
                    instance = it
                }
            }
        }
    }

    suspend fun run(argument: WidgetUpdatedArgument, context: Context) {
        val widgets = argument.run {
            val widgets = if (argument.action == WidgetUpdatedArgument.UPDATE_ONLY_SPECIFIC_WIDGETS) {
                widgetRepository.get(argument.widgetIds, false)
            } else {
                val unLoadedWidgetIds = widgetManager.installedAllWidgetIds.filter { id ->
                    val isLoaded = when (val cache = remoteViewsCacheManager.get(id)) {
                        is CacheManager.CacheState.Hit -> {
                            widgetManager.updateWidget(id, cache.value, context, WidgetActivity::class)
                            true
                        }

                        else -> false
                    }
                    !isLoaded
                }.toTypedArray()
                widgetRepository.get(unLoadedWidgetIds, false)
            }
            widgets.filterNot { isInProgress(it.id) }
        }

        if (!widgets.checkPrimaryRequiredFeatures(requiredFeatures, context)) {
            return
        }

        val updatedWidgetsCompletely = mutableSetOf<WidgetResponseDBEntity>()
        widgets.filter { it.locationType is LocationType.CurrentLocation }.let { filtered ->
            val areEnabledFeatures =
                filtered.checkPrimaryRequiredFeatures(arrayOf(FeatureType.LOCATION_PERMISSION, FeatureType.LOCATION_SERVICE), context)
            if (!areEnabledFeatures) {
                updatedWidgetsCompletely.addAll(filtered)
            }
        }

        val remoteViewsCreatorMap: Map<WidgetType, WidgetRemoteViewsCreator<RemoteViewUiModel>> = widgets.filterNot {
            it in updatedWidgetsCompletely
        }.groupBy {
            it.widgetType
        }.mapValues {
            RemoteViewsCreatorManager.getByWidgetType(it.key)
        }
        val units = appSettingsRepository.currentUnits.value

        for (widget in widgets) {
            if (widget in updatedWidgetsCompletely) {
                continue
            }
            val remoteView = if (widget.status == WidgetStatus.RESPONSE_SUCCESS) {
                val uiModelManager = RemoteViewUiModelMapperManager.getByWidgetType(widget.widgetType)

                remoteViewsCreatorMap.getValue(widget.widgetType).createContentView(uiModelManager.mapToUiModel(widget, units),
                    DefaultRemoteViewCreator.Header(widget.address, widget.updatedAt),
                    context).also { finalRemoteView ->
                    remoteViewsCacheManager.put(widget.id, finalRemoteView)
                }
            } else {
                val pendingIntentToRefresh = ComponentPendingIntentManager.getRefreshPendingIntent(context,
                    PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
                    ComponentServiceAction.LoadWidgetData(LoadWidgetDataArgument(
                        LoadWidgetDataArgument.UPDATE_ONLY_FAILED,
                    )))!!
                UiStateRemoteViewCreator.createView(context,
                    FailedReason.SERVER_ERROR,
                    RemoteViewCreator.ContainerType.WIDGET,
                    UiStateRemoteViewCreator.ViewSizeType.BIG,
                    visibilityOfActionButton = false,
                    visibilityOfCompleteButton = true).apply {
                    setOnClickPendingIntent(io.github.pknujsp.weatherwizard.core.resource.R.id.complete_button, pendingIntentToRefresh)
                }
            }

            widgetManager.updateWidget(widget.id, remoteView, context, WidgetActivity::class)
        }

    }

    private fun List<WidgetResponseDBEntity>.checkPrimaryRequiredFeatures(
        requiredFeatures: Array<FeatureType>, context: Context
    ): Boolean {
        if (isEmpty()) {
            return true
        }

        return when (val status = featureStatusManager.status(context, requiredFeatures)) {
            is FeatureState.Unavailable -> {
                val featurePendingIntent = status.featureType.getPendingIntent(context)
                val refreshPendingIntent = ComponentPendingIntentManager.getRefreshPendingIntent(context,
                    PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
                    ComponentServiceAction.LoadWidgetData(LoadWidgetDataArgument(
                        LoadWidgetDataArgument.UPDATE_ONLY_FAILED,
                    )))

                for (widget in this) {
                    val failedRemoteView = UiStateRemoteViewCreator.createView(context,
                        status.featureType.failedReason,
                        RemoteViewCreator.ContainerType.WIDGET,
                        UiStateRemoteViewCreator.ViewSizeType.BIG,
                        pendingIntent = featurePendingIntent,
                        visibilityOfActionButton = true,
                        visibilityOfCompleteButton = true)
                    failedRemoteView.setOnClickPendingIntent(io.github.pknujsp.weatherwizard.core.resource.R.id.complete_button,
                        refreshPendingIntent)
                    widgetManager.updateWidget(widget.id, failedRemoteView, context, WidgetActivity::class)
                }
                false
            }

            else -> true
        }
    }
}