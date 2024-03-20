package io.github.pknujsp.everyweather.feature.componentservice.widget

import android.content.Context
import android.widget.RemoteViews
import androidx.hilt.work.HiltWorker
import androidx.work.WorkerParameters
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import io.github.pknujsp.everyweather.core.FeatureStateManager
import io.github.pknujsp.everyweather.core.common.FeatureType
import io.github.pknujsp.everyweather.core.common.manager.AppComponentManagerFactory
import io.github.pknujsp.everyweather.core.common.manager.WidgetManager
import io.github.pknujsp.everyweather.core.data.mapper.JsonParser
import io.github.pknujsp.everyweather.core.data.settings.SettingsRepository
import io.github.pknujsp.everyweather.core.data.widget.WidgetRepository
import io.github.pknujsp.everyweather.core.model.coordinate.LocationType
import io.github.pknujsp.everyweather.core.model.widget.WidgetStatus
import io.github.pknujsp.everyweather.core.resource.R
import io.github.pknujsp.everyweather.core.widgetnotification.model.AppComponentCoroutineService
import io.github.pknujsp.everyweather.core.widgetnotification.model.IWorker
import io.github.pknujsp.everyweather.core.widgetnotification.model.LoadWidgetDataArgument
import io.github.pknujsp.everyweather.feature.componentservice.widget.worker.AppWidgetViewUpdater
import io.github.pknujsp.everyweather.feature.componentservice.widget.worker.WidgetRemoteViewModel

@HiltWorker
class WidgetCoroutineService
    @AssistedInject
    constructor(
        @Assisted val context: Context,
        @Assisted params: WorkerParameters,
        private val widgetRemoteViewModel: WidgetRemoteViewModel,
        private val jsonParser: JsonParser,
        appSettingsRepository: SettingsRepository,
        widgetRepository: WidgetRepository,
    ) : AppComponentCoroutineService<LoadWidgetDataArgument>(context, params, Companion) {
        private val widgetManager: WidgetManager = AppComponentManagerFactory.getManager(context, AppComponentManagerFactory.WIDGET_MANAGER)

        companion object : IWorker {
            override val name: String = "WidgetCoroutineService"
            override val requiredFeatures: Array<FeatureType> = arrayOf(FeatureType.Network)
        }

        private val widgetViewUpdater =
            AppWidgetViewUpdater(
                widgetManager,
                widgetRepository,
                featureStateManager,
                appSettingsRepository.settings.replayCache.last().units,
            )

        override suspend fun doWork(
            context: Context,
            argument: LoadWidgetDataArgument,
        ): Result {
            try {
                setLoadingViewToAllWidgets(context)
                start(context, argument)
            } catch (e: Exception) {
                e.printStackTrace()
                widgetViewUpdater(context, null)
            }
            return Result.success()
        }

        private suspend fun start(
            context: Context,
            argument: LoadWidgetDataArgument,
        ) {
            val widgetEntityList = widgetRemoteViewModel.loadWidgets(argument.widgetId, argument.action)

            if (widgetEntityList.widgetSettings.isEmpty()) {
                return
            }

            if (featureStateManager.retrieveFeaturesState(requiredFeatures, context) is FeatureStateManager.FeatureState.Unavailable) {
                for (widget in widgetEntityList.widgetSettings) {
                    widgetRemoteViewModel.updateResponseData(widget.id, WidgetStatus.RESPONSE_FAILURE)
                }
                widgetViewUpdater(context, widgetEntityList.widgetSettings.map { it.id })
                return
            }

            val excludeWidgets = mutableListOf<Int>()

            if (LocationType.CurrentLocation in widgetEntityList.locationTypeGroups &&
                featureStateManager.retrieveFeaturesState(
                    arrayOf(
                        FeatureType.Permission.Location,
                        FeatureType.LocationService,
                        FeatureType.Permission.BackgroundLocation,
                    ),
                    context,
                ) is FeatureStateManager.FeatureState.Unavailable
            ) {
                widgetEntityList.locationTypeGroups.getValue(LocationType.CurrentLocation).forEach {
                    widgetRemoteViewModel.updateResponseData(it.id, WidgetStatus.RESPONSE_FAILURE)
                    excludeWidgets.add(it.id)
                }
                widgetViewUpdater(context, excludeWidgets)

                if (widgetEntityList.widgetSettings.size == excludeWidgets.size) {
                    return
                }
            }

            val responses = widgetRemoteViewModel.load(excludeWidgets)
            val completedWidgets =
                responses.map { state ->
                    if (state.isSuccessful) {
                        widgetRemoteViewModel.updateResponseData(
                            state.widget.id,
                            WidgetStatus.RESPONSE_SUCCESS,
                            state.toWidgetResponseDBModel(jsonParser).toByteArray(jsonParser),
                        )
                    } else {
                        widgetRemoteViewModel.updateResponseData(state.widget.id, WidgetStatus.RESPONSE_FAILURE)
                    }
                    state.widget.id
                }

            widgetViewUpdater(context, completedWidgets)
        }

        private fun setLoadingViewToAllWidgets(context: Context) {
            val installedWidgets = widgetManager.installedAllWidgetIds
            if (installedWidgets.isNotEmpty()) {
                widgetManager.appWidgetManager.updateAppWidget(
                    installedWidgets.toIntArray(),
                    RemoteViews(context.packageName, R.layout.view_loading_widget),
                )
            }
        }
    }
