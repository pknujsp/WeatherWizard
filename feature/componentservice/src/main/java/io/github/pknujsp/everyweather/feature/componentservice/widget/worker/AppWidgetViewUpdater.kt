package io.github.pknujsp.everyweather.feature.componentservice.widget.worker

import android.app.PendingIntent
import android.content.Context
import android.util.Log
import io.github.pknujsp.everyweather.core.FeatureStateManager
import io.github.pknujsp.everyweather.core.common.FeatureType
import io.github.pknujsp.everyweather.core.common.FailedReason
import io.github.pknujsp.everyweather.core.common.manager.WidgetManager
import io.github.pknujsp.everyweather.core.data.widget.SavedWidgetContentState
import io.github.pknujsp.everyweather.core.data.widget.WidgetRepository
import io.github.pknujsp.everyweather.core.model.RemoteViewUiModel
import io.github.pknujsp.everyweather.core.model.coordinate.LocationType
import io.github.pknujsp.everyweather.core.model.settings.CurrentUnits
import io.github.pknujsp.everyweather.core.model.widget.WidgetType
import io.github.pknujsp.everyweather.core.widgetnotification.model.ComponentServiceAction
import io.github.pknujsp.everyweather.core.widgetnotification.model.LoadWidgetDataArgument
import io.github.pknujsp.everyweather.core.widgetnotification.model.RemoteViewUiModelMapperManager
import io.github.pknujsp.everyweather.core.widgetnotification.remoteview.DefaultRemoteViewCreator
import io.github.pknujsp.everyweather.core.widgetnotification.remoteview.RemoteViewCreator
import io.github.pknujsp.everyweather.core.widgetnotification.remoteview.RemoteViewsCreatorManager
import io.github.pknujsp.everyweather.core.widgetnotification.remoteview.UiStateRemoteViewCreator
import io.github.pknujsp.everyweather.core.widgetnotification.widget.remoteview.WidgetRemoteViewsCreator
import io.github.pknujsp.everyweather.feature.componentservice.AppComponentServiceReceiver
import io.github.pknujsp.everyweather.feature.componentservice.ComponentPendingIntentManager
import io.github.pknujsp.everyweather.feature.componentservice.widget.WidgetActivity


class AppWidgetViewUpdater(
    private val widgetManager: WidgetManager,
    private val widgetRepository: WidgetRepository,
    private val featureStateManager: FeatureStateManager,
    private val currentUnits: CurrentUnits,
) {
    private val requiredFeatures: Array<FeatureType> = arrayOf(FeatureType.NETWORK)

    suspend operator fun invoke(context: Context, widgetIds: List<Int>?) {
        val widgets = widgetRepository.get(widgetIds ?: widgetManager.installedAllWidgetIds).filterNot {
            it is SavedWidgetContentState.Pending
        }
        if (!widgets.checkPrimaryRequiredFeatures(requiredFeatures, context)) {
            return
        }
        updateViews(context, widgets)
        Log.d("AppWidgetViewUpdater", "updateViews")
    }

    private fun updateViews(context: Context, widgets: List<SavedWidgetContentState>) {
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

        for (widget in widgets) {
            if (widget in updatedWidgetsCompletely) {
                continue
            }
            val remoteView = if (widget is SavedWidgetContentState.Success) {
                val uiModelMapper = RemoteViewUiModelMapperManager.getByWidgetType(widget.widgetType)

                remoteViewsCreatorMap.getValue(widget.widgetType).createContentView(uiModelMapper.mapToUiModel(widget, currentUnits),
                    DefaultRemoteViewCreator.Header(widget.address, widget.updatedAt),
                    context)
            } else {
                val pendingIntentToRefresh = ComponentPendingIntentManager.getPendingIntent(context,
                    PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
                    ComponentServiceAction.LoadWidgetData(LoadWidgetDataArgument(
                        LoadWidgetDataArgument.UPDATE_ONLY_FAILED,
                    )),
                    actionString = AppComponentServiceReceiver.ACTION_REFRESH).pendingIntent
                UiStateRemoteViewCreator.createView(context,
                    FailedReason.SERVER_ERROR,
                    RemoteViewCreator.ContainerType.WIDGET,
                    UiStateRemoteViewCreator.ViewSizeType.BIG,
                    visibilityOfActionButton = false,
                    visibilityOfCompleteButton = true).apply {
                    setOnClickPendingIntent(io.github.pknujsp.everyweather.core.resource.R.id.complete_button, pendingIntentToRefresh)
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
                    )),
                    actionString = AppComponentServiceReceiver.ACTION_REFRESH).pendingIntent

                for (widget in this) {
                    val failedRemoteView = UiStateRemoteViewCreator.createView(context,
                        status.featureType,
                        RemoteViewCreator.ContainerType.WIDGET,
                        UiStateRemoteViewCreator.ViewSizeType.BIG,
                        pendingIntent = featurePendingIntent,
                        visibilityOfActionButton = true,
                        visibilityOfCompleteButton = true)
                    failedRemoteView.setOnClickPendingIntent(io.github.pknujsp.everyweather.core.resource.R.id.complete_button,
                        refreshPendingIntent)
                    widgetManager.updateWidget(widget.id, failedRemoteView, context, WidgetActivity::class)
                }
                false
            }

            else -> true
        }
    }
}