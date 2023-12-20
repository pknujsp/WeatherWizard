package io.github.pknujsp.weatherwizard.feature.componentservice.widget

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.WorkerParameters
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import io.github.pknujsp.weatherwizard.core.common.FeatureType
import io.github.pknujsp.weatherwizard.core.common.manager.FeatureState
import io.github.pknujsp.weatherwizard.core.common.manager.FeatureStatusManager
import io.github.pknujsp.weatherwizard.core.common.manager.WidgetManager
import io.github.pknujsp.weatherwizard.core.data.widget.WidgetSettingsEntity
import io.github.pknujsp.weatherwizard.core.domain.weather.WeatherResponseState
import io.github.pknujsp.weatherwizard.core.model.RemoteViewUiModel
import io.github.pknujsp.weatherwizard.core.model.coordinate.LocationType
import io.github.pknujsp.weatherwizard.core.model.widget.WidgetStatus
import io.github.pknujsp.weatherwizard.core.resource.R
import io.github.pknujsp.weatherwizard.core.widgetnotification.model.AppComponentCoroutineService
import io.github.pknujsp.weatherwizard.core.widgetnotification.model.IWorker
import io.github.pknujsp.weatherwizard.core.widgetnotification.model.LoadWidgetDataArgument
import io.github.pknujsp.weatherwizard.core.widgetnotification.remoteview.DefaultRemoteViewCreator
import io.github.pknujsp.weatherwizard.core.widgetnotification.remoteview.RemoteViewCreator
import io.github.pknujsp.weatherwizard.core.widgetnotification.remoteview.RemoteViewsCreatorManager
import io.github.pknujsp.weatherwizard.core.widgetnotification.remoteview.UiStateRemoteViewCreator
import io.github.pknujsp.weatherwizard.core.widgetnotification.widget.remoteview.WidgetRemoteViewsCreator
import io.github.pknujsp.weatherwizard.feature.componentservice.widget.worker.WidgetRemoteViewModel

@HiltWorker
class WidgetCoroutineService @AssistedInject constructor(
    @Assisted val context: Context,
    @Assisted params: WorkerParameters,
    private val widgetRemoteViewModel: WidgetRemoteViewModel,
    private val featureStatusManager: FeatureStatusManager,
    private val widgetManager: WidgetManager
) : AppComponentCoroutineService<LoadWidgetDataArgument>(context, params, Companion) {

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
        val widgetEntityList = widgetRemoteViewModel.loadWidgets()

        if (featureStatusManager.status(context, requiredFeatures) is FeatureState.Unavailable) {
            for (widgetId in argument.widgetIds) {
                widgetRemoteViewModel.updateResponseData(widgetId, WidgetStatus.RESPONSE_FAILURE, byteArrayOf())
            }
            return
        }

        var excludeLocationType: LocationType? = null
        if (argument.action == LoadWidgetDataArgument.UPDATE_ONLY_ON_CURRENT_LOCATION) {
            excludeLocationType = LocationType.CustomLocation
        }

        val excludeWidgets = mutableSetOf<WidgetSettingsEntity>()

        if (widgetEntityList.locationTypeGroups.getValue(LocationType.CurrentLocation).isNotEmpty()) {
            if (featureStatusManager.status(context,
                    arrayOf(FeatureType.LOCATION_PERMISSION, FeatureType.LOCATION_SERVICE)) is FeatureState.Unavailable) {
                widgetEntityList.locationTypeGroups.getValue(LocationType.CurrentLocation).forEach {
                    widgetRemoteViewModel.updateResponseData(it.id, WidgetStatus.RESPONSE_FAILURE, byteArrayOf())
                    excludeWidgets.add(it)
                }
            }
        }

        with(widgetRemoteViewModel.load(excludeWidgets, excludeLocationType)) {
            forEach { model ->
                 when (model.state) {
                    is WeatherResponseState.Success ->
                        widgetRemoteViewModel.updateResponseData(model.widget.id,WidgetStatus.RESPONSE_SUCCESS, model.state.entity.)

                    is WeatherResponseState.Failure -> widgetRemoteViewModel.updateResponseData(model.widget.id,WidgetStatus.RESPONSE_FAILURE, model.state.entity.)
                }
            }
        }
    }


}