package io.github.pknujsp.weatherwizard.core.model.weather.yesterday

import android.content.Context
import io.github.pknujsp.weatherwizard.core.model.UiModel
import io.github.pknujsp.weatherwizard.core.model.weather.common.TemperatureValueType
import kotlin.math.absoluteValue

data class YesterdayWeather(
    val temperature: TemperatureValueType,
) : UiModel {

    fun text(todayTemperature: TemperatureValueType, context: Context): String {
        val diffTemperature = TemperatureValueType(todayTemperature.value - temperature.value, todayTemperature.unit)
        val text =
            context.getString(if (diffTemperature.value.toInt() == 0) io.github.pknujsp.weatherwizard.core.common.R.string.as_yesterday else io.github.pknujsp.weatherwizard.core.common.R.string.than_yesterday)
        val endText =
            context.getString(if (diffTemperature.value.toInt() > 0) io.github.pknujsp.weatherwizard.core.common.R.string.higher else if (diffTemperature.value.toInt() < 0) io.github.pknujsp.weatherwizard.core.common.R.string.lower else io.github.pknujsp.weatherwizard.core.common.R.string.is_)
        val temp =
            if (diffTemperature.value.toInt() == 0) context.getString(io.github.pknujsp.weatherwizard.core.common.R.string.same_temperature) else "${
                diffTemperature.value.absoluteValue.toInt()
            }${diffTemperature.unit.symbol}"

        return "$text $temp $endText"
    }

}