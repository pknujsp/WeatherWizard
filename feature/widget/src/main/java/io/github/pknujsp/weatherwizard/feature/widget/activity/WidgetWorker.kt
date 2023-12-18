package io.github.pknujsp.weatherwizard.feature.widget.activity

import android.content.Context
import android.os.Bundle
import io.github.pknujsp.weatherwizard.core.common.FeatureType
import io.github.pknujsp.weatherwizard.core.common.manager.AppComponentService
import io.github.pknujsp.weatherwizard.core.common.manager.FeatureState
import io.github.pknujsp.weatherwizard.core.common.manager.FeatureStateChecker
import io.github.pknujsp.weatherwizard.core.common.manager.IWorker
import io.github.pknujsp.weatherwizard.core.data.widget.WidgetSettingsEntity
import io.github.pknujsp.weatherwizard.core.domain.weather.WeatherResponseState
import io.github.pknujsp.weatherwizard.core.model.RemoteViewUiModel
import io.github.pknujsp.weatherwizard.core.model.coordinate.LocationType
import io.github.pknujsp.weatherwizard.core.resource.R
import io.github.pknujsp.weatherwizard.core.widgetnotification.remoteview.UiStateRemoteViewCreator
import io.github.pknujsp.weatherwizard.core.widgetnotification.widget.WidgetManager
import io.github.pknujsp.weatherwizard.core.widgetnotification.widget.remoteview.WidgetRemoteViewsCreator
import java.time.ZonedDateTime
import java.util.concurrent.atomic.AtomicBoolean
import javax.inject.Inject
import kotlin.properties.Delegates


class WidgetWorker @Inject constructor(private val widgetRemoteViewModel: WidgetRemoteViewModel) : AppComponentService() {

    private var widgetManager: WidgetManager by Delegates.notNull()

    companion object : IWorker {
        override val name: String get() = "WidgetWorker"
        override val requiredFeatures: Array<FeatureType>
            get() = arrayOf(FeatureType.NETWORK)

        const val ACTION_KEY = "action"
        const val APP_WIDGET_IDS_KEY = "appWidgetIds"

        override val isRunning: AtomicBoolean = AtomicBoolean(false)
    }

    override suspend fun start(context: Context, bundle: Bundle) {
        if (!bundle.containsKey(ACTION_KEY) or !bundle.containsKey(APP_WIDGET_IDS_KEY)) {
            return
        }

        widgetManager = WidgetManager.getInstance(context)

        val action = WidgetManager.Action.valueOf(bundle.getString(ACTION_KEY, ""))
        val appWidgetIds = bundle.getIntArray(APP_WIDGET_IDS_KEY)!!
        val widgetEntityList = widgetRemoteViewModel.loadWidgets()

        // 네트워크 연결 상태 확인, 연결이 안되어 있다면 위젯에 네트워크 연결 상태를 표시
        if (!checkFeatureStateAndUpdateWidgets(requiredFeatures, appWidgetIds, context)) {
            return
        }

        var excludeLocationType: LocationType? = null
        if (action == WidgetManager.Action.UPDATE_ONLY_BASED_CURRENT_LOCATION) {
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
                    }.toIntArray(),
                    context)) {
                widgetEntityList.locationTypeGroups.getValue(LocationType.CurrentLocation).forEach {
                    excludeWidgets.add(it)
                }
            }
        }

        with(widgetRemoteViewModel.load(excludeWidgets, excludeLocationType)) {
            val failedWidgetIds = filter { it.state is WeatherResponseState.Failure }.map { it.widget.id }.toIntArray()
            val retryPendingIntent = if (failedWidgetIds.isNotEmpty()) widgetManager.getUpdatePendingIntent(context,
                WidgetManager.Action.UPDATE_ONLY_WITH_WIDGETS,
                failedWidgetIds,SummaryWeatherWidgetProvider::class) else null

            forEach { model ->
                val remoteView = when (model.state) {
                    is WeatherResponseState.Success -> {
                        val creator: WidgetRemoteViewsCreator<RemoteViewUiModel> = widgetManager.remoteViewCreator(model.widget.widgetType)
                        creator.createContentView(model.map(widgetRemoteViewModel.units),
                            io.github.pknujsp.weatherwizard.core.widgetnotification.remoteview.DefaultRemoteViewCreator.Header("",
                                ZonedDateTime.now()),
                            context)
                    }

                    else -> {
                        UiStateRemoteViewCreator.createView(context,
                            R.string.title_failed_to_load_data,
                            R.string.failed_to_load_data,
                            R.string.refresh,
                            io.github.pknujsp.weatherwizard.core.widgetnotification.remoteview.RemoteViewCreator.ContainerType.WIDGET,
                            retryPendingIntent!!)
                    }
                }

                widgetManager.updateWidget(model.widget.id, remoteView, context, WidgetActivity::class)
            }
        }
    }


    private fun checkFeatureStateAndUpdateWidgets(featureTypes: Array<FeatureType>, widgetIds: IntArray, context: Context): Boolean {
        return when (val state = FeatureStateChecker.checkFeatureState(context, featureTypes)) {
            is FeatureState.Unavailable -> {
                val remoteViews = UiStateRemoteViewCreator.createView(context,
                    state.featureType,
                    io.github.pknujsp.weatherwizard.core.widgetnotification.remoteview.RemoteViewCreator.ContainerType.WIDGET,
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