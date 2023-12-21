package io.github.pknujsp.weatherwizard.feature.componentservice.widget.worker

import android.content.Context
import android.util.Log
import dagger.hilt.android.qualifiers.ApplicationContext
import io.github.pknujsp.weatherwizard.core.common.FeatureType
import io.github.pknujsp.weatherwizard.core.common.manager.FeatureStatusManager
import io.github.pknujsp.weatherwizard.core.common.manager.WidgetManager
import io.github.pknujsp.weatherwizard.core.common.module.KtJson
import io.github.pknujsp.weatherwizard.core.data.settings.SettingsRepository
import io.github.pknujsp.weatherwizard.core.data.widget.WidgetRepository
import io.github.pknujsp.weatherwizard.core.data.widget.WidgetSettingsEntityList
import io.github.pknujsp.weatherwizard.core.model.JsonParser
import io.github.pknujsp.weatherwizard.core.model.RemoteViewUiModel
import io.github.pknujsp.weatherwizard.core.widgetnotification.model.AppComponentBackgroundService
import io.github.pknujsp.weatherwizard.core.widgetnotification.model.IWorker
import io.github.pknujsp.weatherwizard.core.widgetnotification.model.LoadWidgetDataArgument
import io.github.pknujsp.weatherwizard.core.widgetnotification.model.RemoteViewUiModelMapperManager
import io.github.pknujsp.weatherwizard.core.widgetnotification.model.WidgetUpdatedArgument
import io.github.pknujsp.weatherwizard.core.widgetnotification.remoteview.DefaultRemoteViewCreator
import io.github.pknujsp.weatherwizard.core.widgetnotification.remoteview.RemoteViewsCreatorManager
import io.github.pknujsp.weatherwizard.core.widgetnotification.widget.remoteview.WidgetRemoteViewsCreator
import io.github.pknujsp.weatherwizard.feature.componentservice.widget.WidgetActivity
import kotlinx.coroutines.flow.first
import kotlinx.serialization.json.Json
import javax.inject.Inject

class WidgetUpdateBackgroundService @Inject constructor(
    @ApplicationContext context: Context,
    private val widgetRepository: WidgetRepository,
    private val featureStatusManager: FeatureStatusManager,
    private val widgetManager: WidgetManager,
    appSettingsRepository: SettingsRepository,
    @KtJson json: Json
) : AppComponentBackgroundService<WidgetUpdatedArgument>(context) {

    private val units = appSettingsRepository.currentUnits.value
    private val jsonParser by lazy { JsonParser(json) }

    override val id: Int = "WidgetUpdateBackgroundService".hashCode()

    companion object : IWorker {
        override val name: String = "WidgetUpdateBackgroundService"
        override val requiredFeatures: Array<FeatureType> = arrayOf(FeatureType.NETWORK)
        override val workerId: Int = name.hashCode()
    }

    override suspend fun doWork(argument: WidgetUpdatedArgument): Result<Unit> {
        val widgets = widgetRepository.get(argument.widgetIds, argument.widgetIds.isEmpty())
        Log.d("WidgetUpdateBackgroundService", "widgets: $widgets")

        for (widget in widgets) {
            val uiModelManager = RemoteViewUiModelMapperManager.getByWidgetType(widget.widgetType)
            val creator: WidgetRemoteViewsCreator<RemoteViewUiModel> = RemoteViewsCreatorManager.getByWidgetType(widget.widgetType)

            val remoteView = creator.createContentView(uiModelManager.mapToUiModel(widget, units),
                DefaultRemoteViewCreator.Header(widget.address, widget.updatedAt),
                context)

            widgetManager.updateWidget(widget.id, remoteView, context, WidgetActivity::class)
        }
        return Result.success(Unit)
    }


}