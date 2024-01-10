package io.github.pknujsp.weatherwizard.core.model.weather.yesterday

import android.content.Context
import androidx.compose.runtime.Stable
import io.github.pknujsp.weatherwizard.core.model.UiModel
import io.github.pknujsp.weatherwizard.core.model.weather.common.TemperatureValueType
import io.github.pknujsp.weatherwizard.core.resource.R
import kotlin.math.absoluteValue

@Stable
data class YesterdayWeather(
    val temperature: TemperatureValueType,
) : UiModel {

    fun text(todayTemperature: TemperatureValueType, context: Context): List<String> {
        val diffTemperature = TemperatureValueType(todayTemperature.value - temperature.value, todayTemperature.unit)

        val text =
            context.getString(if (diffTemperature.value.toInt() == 0) io.github.pknujsp.weatherwizard.core.resource.R.string.as_yesterday else io.github.pknujsp.weatherwizard.core.resource.R.string.than_yesterday)

        val endText =
            context.getString(if (diffTemperature.value.toInt() > 0) io.github.pknujsp.weatherwizard.core.resource.R.string.higher else if (diffTemperature.value.toInt() < 0) io.github.pknujsp.weatherwizard.core.resource.R.string.lower else io.github.pknujsp.weatherwizard.core.resource.R.string.is_)

        val temp = if (diffTemperature.value.toInt() == 0) {
            context.getString(R.string.same_temperature)
        } else {
            "${diffTemperature.value.absoluteValue.toInt()}${diffTemperature.unit.symbol}"
        }

        return listOf(text, temp, endText)
    }

}