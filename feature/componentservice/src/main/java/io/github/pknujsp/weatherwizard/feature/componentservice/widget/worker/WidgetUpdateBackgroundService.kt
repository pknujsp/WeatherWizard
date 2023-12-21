package io.github.pknujsp.weatherwizard.feature.componentservice.widget.worker

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import io.github.pknujsp.weatherwizard.core.common.FeatureType
import io.github.pknujsp.weatherwizard.core.common.manager.FeatureStatusManager
import io.github.pknujsp.weatherwizard.core.common.manager.WidgetManager
import io.github.pknujsp.weatherwizard.core.data.widget.WidgetRepository
import io.github.pknujsp.weatherwizard.core.widgetnotification.model.AppComponentBackgroundService
import io.github.pknujsp.weatherwizard.core.widgetnotification.model.IWorker
import io.github.pknujsp.weatherwizard.core.widgetnotification.model.LoadWidgetDataArgument
import javax.inject.Inject

class WidgetUpdateBackgroundService @Inject constructor(
    @ApplicationContext context: Context, private val widgetRepository: WidgetRepository,
    private val featureStatusManager: FeatureStatusManager,
    private val widgetManager: WidgetManager
) : AppComponentBackgroundService<LoadWidgetDataArgument>(context) {

    override val id: Int = "WidgetUpdateBackgroundService".hashCode()

    companion object : IWorker {
        override val name: String = "WidgetUpdateBackgroundService"
        override val requiredFeatures: Array<FeatureType> = arrayOf(FeatureType.NETWORK)
        override val workerId: Int = name.hashCode()
    }

    override suspend fun doWork(argument: LoadWidgetDataArgument): Result<Unit> {
        return Result.success(Unit)
    }


}