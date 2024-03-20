package io.github.pknujsp.everyweather.core.widgetnotification.widget

import io.github.pknujsp.everyweather.core.model.Model
import io.github.pknujsp.everyweather.core.model.UiModel
import io.github.pknujsp.everyweather.core.model.mapper.UiModelMapper

abstract class WidgetUiModelMapper<T : Model, O : UiModel>(
    protected val hourlyForecastItemsCount: Int,
    protected val dailyForecastItemsCount: Int,
) : UiModelMapper<T, O>
