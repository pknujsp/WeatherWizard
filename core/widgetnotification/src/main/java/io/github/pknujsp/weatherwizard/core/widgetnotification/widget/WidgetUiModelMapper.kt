package io.github.pknujsp.weatherwizard.core.widgetnotification.widget

import io.github.pknujsp.weatherwizard.core.model.Model
import io.github.pknujsp.weatherwizard.core.model.UiModel
import io.github.pknujsp.weatherwizard.core.model.mapper.UiModelMapper

abstract class WidgetUiModelMapper<T : Model, O : UiModel>(
    protected val hourlyForecastItemsCount: Int, protected val dailyForecastItemsCount: Int
) : UiModelMapper<T, O> {

}