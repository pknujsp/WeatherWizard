package io.github.pknujsp.weatherwizard.feature.componentservice.widget.worker

import android.app.PendingIntent
import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import io.github.pknujsp.weatherwizard.core.common.FeatureType
import io.github.pknujsp.weatherwizard.core.common.coroutines.CoDispatcher
import io.github.pknujsp.weatherwizard.core.common.coroutines.CoDispatcherType
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
import io.github.pknujsp.weatherwizard.core.model.widget.WidgetStatus
import io.github.pknujsp.weatherwizard.core.model.widget.WidgetType
import io.github.pknujsp.weatherwizard.core.widgetnotification.model.AppComponentBackgroundService
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
import kotlinx.coroutines.CoroutineDispatcher
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class WidgetUpdateBackgroundService @Inject constructor(
    @ApplicationContext context: Context,
    @CoDispatcher(CoDispatcherType.SINGLE) private val dispatcher: CoroutineDispatcher,
    private val widgetRepository: WidgetRepository,
    private val featureStatusManager: FeatureStatusManager,
    private val widgetManager: WidgetManager,
    appSettingsRepository: SettingsRepository,
) : AppComponentBackgroundService<WidgetUpdatedArgument>(context) {

    private val remoteViewsCacheManager = WidgetViewCacheManagerFactory.getInstance(dispatcher)
    private val units = appSettingsRepository.currentUnits.value
    override val id: Int get() = workerId

    companion object : IWorker {
        override val name: String = "WidgetUpdateBackgroundService"
        override val requiredFeatures: Array<FeatureType> = arrayOf(FeatureType.NETWORK)
        override val workerId: Int = name.hashCode()
    }

    override suspend fun doWork(argument: WidgetUpdatedArgument): Result<Unit> {
        val widgets = if (argument.action == WidgetUpdatedArgument.UPDATE_ONLY_SPECIFIC_WIDGETS) {
            var bindedWidgetIds = arrayOf<Int>()
            for (widgetId in argument.widgetIds) {
                if (widgetManager.isBind(widgetId)) {
                    bindedWidgetIds = bindedWidgetIds.plus(widgetId)
                } else {
                    widgetRepository.delete(widgetId)
                }
            }

            if (bindedWidgetIds.isEmpty()) {
                return Result.success(Unit)
            }

            widgetRepository.get(bindedWidgetIds, false)
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

        if (!widgets.checkPrimaryRequiredFeatures(requiredFeatures)) {
            return Result.success(Unit)
        }

        val updatedWidgetsCompletely = mutableSetOf<WidgetResponseDBEntity>()
        widgets.filter { it.locationType is LocationType.CurrentLocation }.let { filtered ->
            val areEnabledFeatures = filtered.checkPrimaryRequiredFeatures(
                arrayOf(FeatureType.LOCATION_PERMISSION, FeatureType.LOCATION_SERVICE),
            )
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
                val refreshPendingIntent = ComponentPendingIntentManager.getRefreshPendingIntent(context,
                    PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
                    ComponentServiceAction.LoadWidgetData(LoadWidgetDataArgument(
                        LoadWidgetDataArgument.UPDATE_ONLY_FAILED,
                    )))
                UiStateRemoteViewCreator.createView(context,
                    FailedReason.SERVER_ERROR,
                    RemoteViewCreator.ContainerType.WIDGET,
                    UiStateRemoteViewCreator.ViewSizeType.BIG,
                    visibilityOfActionButton = false,
                    visibilityOfCompleteButton = true).apply {
                    setOnClickPendingIntent(io.github.pknujsp.weatherwizard.core.resource.R.id.complete_button, refreshPendingIntent)
                }
            }

            widgetManager.updateWidget(widget.id, remoteView, context, WidgetActivity::class)
        }
        return Result.success(Unit)
    }

    private fun List<WidgetResponseDBEntity>.checkPrimaryRequiredFeatures(
        requiredFeatures: Array<FeatureType>
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