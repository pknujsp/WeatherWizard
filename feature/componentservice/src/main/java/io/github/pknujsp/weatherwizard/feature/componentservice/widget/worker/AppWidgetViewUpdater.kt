package io.github.pknujsp.weatherwizard.feature.componentservice.widget.worker

import android.app.PendingIntent
import android.content.Context
import android.widget.RemoteViews
import io.github.pknujsp.weatherwizard.core.FeatureStateManager
import io.github.pknujsp.weatherwizard.core.common.FeatureType
import io.github.pknujsp.weatherwizard.core.common.manager.FailedReason
import io.github.pknujsp.weatherwizard.core.common.manager.WidgetManager
import io.github.pknujsp.weatherwizard.core.data.cache.CacheManager
import io.github.pknujsp.weatherwizard.core.data.settings.SettingsRepository
import io.github.pknujsp.weatherwizard.core.data.widget.SavedWidgetContentState
import io.github.pknujsp.weatherwizard.core.data.widget.WidgetRepository
import io.github.pknujsp.weatherwizard.core.model.RemoteViewUiModel
import io.github.pknujsp.weatherwizard.core.model.coordinate.LocationType
import io.github.pknujsp.weatherwizard.core.model.widget.WidgetType
import io.github.pknujsp.weatherwizard.core.widgetnotification.model.ComponentServiceAction
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


class AppWidgetViewUpdater(
    private val widgetManager: WidgetManager,
    private val widgetRepository: WidgetRepository,
    private val featureStateManager: FeatureStateManager,
    private val appSettingsRepository: SettingsRepository,
    private val remoteViewsCacheManager: CacheManager<Int, RemoteViews>
) {
    private val requiredFeatures: Array<FeatureType> = arrayOf(FeatureType.NETWORK)

    suspend operator fun invoke(argument: WidgetUpdatedArgument, context: Context) {
        val widgets = argument.run {
            val widgetsIds = if (argument.action == WidgetUpdatedArgument.UPDATE_ONLY_SPECIFIC_WIDGETS) {
                argument.widgetIds.toList()
            } else {
                val unLoadedWidgetIds = widgetManager.installedAllWidgetIds.filterNot { id ->
                    when (val cache = remoteViewsCacheManager.get(id)) {
                        is CacheManager.CacheState.Hit -> {
                            widgetManager.updateWidget(id, cache.value, context, WidgetActivity::class)
                            true
                        }

                        else -> false
                    }
                }
                unLoadedWidgetIds
            }
            widgetsIds.filterNot { isInProgress(it) }.run { widgetRepository.get(this) }.filterNot { it is SavedWidgetContentState.Pending }
        }

        if (!widgets.checkPrimaryRequiredFeatures(requiredFeatures, context)) {
            return
        }

        val updatedWidgetsCompletely = mutableSetOf<SavedWidgetContentState>()
        widgets.filter { it.locationType is LocationType.CurrentLocation }.let { filtered ->
            if (!filtered.checkPrimaryRequiredFeatures(arrayOf(FeatureType.LOCATION_PERMISSION,
                    FeatureType.LOCATION_SERVICE,
                    FeatureType.BACKGROUND_LOCATION_PERMISSION), context)) {
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
        val units = appSettingsRepository.settings.replayCache.last().units

        for (widget in widgets) {
            if (widget in updatedWidgetsCompletely) {
                continue
            }
            val remoteView = if (widget is SavedWidgetContentState.Success) {
                val uiModelMapper = RemoteViewUiModelMapperManager.getByWidgetType(widget.widgetType)

                remoteViewsCreatorMap.getValue(widget.widgetType).createContentView(uiModelMapper.mapToUiModel(widget, units),
                    DefaultRemoteViewCreator.Header(widget.address, widget.updatedAt),
                    context).also { finalRemoteView ->
                    remoteViewsCacheManager.put(widget.id, finalRemoteView)
                }
            } else {
                val pendingIntentToRefresh = ComponentPendingIntentManager.getPendingIntent(context,
                    PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
                    ComponentServiceAction.LoadWidgetData(LoadWidgetDataArgument(
                        LoadWidgetDataArgument.UPDATE_ONLY_FAILED,
                    ))).pendingIntent
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

    private fun List<SavedWidgetContentState>.checkPrimaryRequiredFeatures(
        requiredFeatures: Array<FeatureType>, context: Context
    ): Boolean {
        if (isEmpty()) {
            return true
        }

        return when (val status = featureStateManager.retrieveFeaturesState(requiredFeatures, context)) {
            is FeatureStateManager.FeatureState.Unavailable -> {
                val featurePendingIntent = status.featureType.getPendingIntent(context)
                val refreshPendingIntent = ComponentPendingIntentManager.getPendingIntent(context,
                    PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
                    ComponentServiceAction.LoadWidgetData(LoadWidgetDataArgument(
                        LoadWidgetDataArgument.UPDATE_ONLY_FAILED,
                    ))).pendingIntent

                for (widget in this) {
                    val failedRemoteView = UiStateRemoteViewCreator.createView(context,
                        status.featureType,
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