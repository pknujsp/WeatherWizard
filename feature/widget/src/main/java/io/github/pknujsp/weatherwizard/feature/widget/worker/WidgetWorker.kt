package io.github.pknujsp.weatherwizard.feature.widget.worker

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.ForegroundInfo
import androidx.work.WorkerParameters
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import io.github.pknujsp.weatherwizard.core.common.FeatureType
import io.github.pknujsp.weatherwizard.core.common.manager.FeatureState
import io.github.pknujsp.weatherwizard.core.common.manager.checkFeatureState
import io.github.pknujsp.weatherwizard.core.domain.weather.WeatherResponseState
import io.github.pknujsp.weatherwizard.core.model.UiModel
import io.github.pknujsp.weatherwizard.core.model.favorite.LocationType
import io.github.pknujsp.weatherwizard.core.model.notification.enums.NotificationType
import io.github.pknujsp.weatherwizard.core.model.worker.IWorker
import io.github.pknujsp.weatherwizard.core.ui.feature.FeatureStateRemoteViewCreator
import io.github.pknujsp.weatherwizard.core.ui.notification.AppNotificationManager
import io.github.pknujsp.weatherwizard.core.ui.remoteview.RemoteViewCreator
import io.github.pknujsp.weatherwizard.core.ui.remoteview.RetryRemoteViewCreator
import io.github.pknujsp.weatherwizard.feature.widget.WidgetManager
import io.github.pknujsp.weatherwizard.feature.widget.remoteview.WidgetRemoteViewsCreator
import kotlinx.coroutines.withTimeout
import kotlin.time.Duration


@HiltWorker
class WidgetWorker @AssistedInject constructor(
    @Assisted val context: Context, @Assisted params: WorkerParameters, private val widgetRemoteViewModel: WidgetRemoteViewModel
) : CoroutineWorker(context, params) {

    private val widgetManager: WidgetManager by lazy {
        WidgetManager.getInstance(context)
    }
    private val featureStateRemoteViewCreator: FeatureStateRemoteViewCreator by lazy {
        FeatureStateRemoteViewCreator()
    }
    private val retryRemoteViewCreator: RetryRemoteViewCreator by lazy {
        RetryRemoteViewCreator()
    }

    companion object : IWorker {
        override val name: String get() = "WidgetWorker"
        override val requiredFeatures: Array<FeatureType>
            get() = arrayOf(FeatureType.NETWORK)

        const val ACTION_KEY = "action"
        const val APP_WIDGET_IDS_KEY = "appWidgetIds"
    }

    override suspend fun doWork(): Result {
        val inputDataMap = inputData.keyValueMap
        if (ACTION_KEY !in inputDataMap || APP_WIDGET_IDS_KEY !in inputDataMap) {
            return Result.success()
        }

        val action = WidgetManager.Action.valueOf(inputDataMap[ACTION_KEY] as String)
        val appWidgetIds = inputDataMap[APP_WIDGET_IDS_KEY] as IntArray
        val widgetEntityList = widgetRemoteViewModel.loadWidgets()

        // 네트워크 연결 상태 확인, 연결이 안되어 있다면 위젯에 네트워크 연결 상태를 표시
        if (!checkFeatureStateAndUpdateWidgets(requiredFeatures, appWidgetIds)) {
            return Result.success()
        }

        var excludeLocationType: LocationType? = null
        if (action == WidgetManager.Action.UPDATE_ONLY_BASED_CURRENT_LOCATION) {
            excludeLocationType = LocationType.CustomLocation()
        }

        val excludeAppWidgetIds = mutableListOf<Int>()

        // 위젯이 활성화되어 있지 않다면 DB에서 삭제
        widgetEntityList.widgetSettings.forEach {
            if (!widgetManager.isBind(it.id)) {
                excludeAppWidgetIds.add(it.id)
            }
        }

        if (widgetEntityList.locationTypeGroups.getValue(LocationType.CurrentLocation()).isNotEmpty()) {
            if (!checkFeatureStateAndUpdateWidgets(arrayOf(FeatureType.LOCATION_PERMISSION, FeatureType.LOCATION_SERVICE),
                    widgetEntityList.locationTypeGroups.getValue(LocationType.CurrentLocation()).map {
                        it.id
                    }.toIntArray())) {
                return Result.success()
            }
        }

        val widgetStates = widgetRemoteViewModel.load(excludeAppWidgetIds, excludeLocationType)
        val failedWidgetIds = widgetStates.filter { it.state is WeatherResponseState.Failure }.map { it.widget.id }.toIntArray()
        val retryPendingIntent = if (failedWidgetIds.isNotEmpty()) widgetManager.getUpdatePendingIntent(context,
            WidgetManager.Action.UPDATE_ONLY_WITH_WIDGETS,
            failedWidgetIds) else null

        widgetStates.forEach { model ->
            val remoteView = when (model.state) {
                is WeatherResponseState.Success -> {
                    val creator: WidgetRemoteViewsCreator<UiModel> = widgetManager.remoteViewCreator(model.widget.widgetType)
                    creator.createContentView(model.map(widgetRemoteViewModel.units), context)
                }

                else -> {
                    retryRemoteViewCreator.createView(context,
                        context.getString(io.github.pknujsp.weatherwizard.core.common.R.string.refresh),
                        retryPendingIntent!!,
                        RemoteViewCreator.WIDGET)
                }
            }

            widgetManager.updateWidget(model.widget.id, remoteView, context)
        }

        return Result.success()
    }

    override suspend fun getForegroundInfo(): ForegroundInfo {
        return AppNotificationManager(context).createForegroundNotification(context, NotificationType.WORKING)
    }

    private fun checkFeatureStateAndUpdateWidgets(featureTypes: Array<FeatureType>, widgetIds: IntArray): Boolean {
        return when (val state = context.checkFeatureState(featureTypes)) {
            is FeatureState.Unavailable -> {
                val remoteViews = featureStateRemoteViewCreator.createView(context, state.featureType, RemoteViewCreator.WIDGET)
                widgetIds.forEach {
                    widgetManager.updateWidget(it, remoteViews, context)
                }
                false
            }

            else -> true
        }
    }

}