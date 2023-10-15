package io.github.pknujsp.weatherwizard.feature.notification.common

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import androidx.annotation.DrawableRes
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.view.isVisible
import io.github.pknujsp.weatherwizard.core.model.weather.common.CurrentUnits
import io.github.pknujsp.weatherwizard.core.model.weather.common.TemperatureUnit
import io.github.pknujsp.weatherwizard.core.model.weather.common.TemperatureValueType
import io.github.pknujsp.weatherwizard.core.ui.theme.AppShapes
import io.github.pknujsp.weatherwizard.feature.notification.databinding.NotificationOngoingBigBinding


@Composable
fun RemoteViewsScreen(sampleRemoteViews: SampleRemoteViews) {
    Surface(shape = AppShapes.large, modifier = Modifier,
        shadowElevation = 4.dp) {
        AndroidView(modifier = Modifier.fillMaxWidth(), factory = { context ->
            sampleRemoteViews.create(context)
        }, update = {

        })
    }
}

interface SampleRemoteViews {
    fun create(context: Context): View
}

class OngoingNotificationSampleRemoteViews(private val units: CurrentUnits) : SampleRemoteViews {
    @DrawableRes val weatherIcon: Int = io.github.pknujsp.weatherwizard.core.common.R.drawable.ic_sun
    private val temperature: TemperatureValueType = TemperatureValueType(16.0, TemperatureUnit.Celsius)
    private val feelsLikeTemperature: TemperatureValueType = TemperatureValueType(14.0, TemperatureUnit.Celsius)

    override fun create(context: Context): View {
        return NotificationOngoingBigBinding.inflate(LayoutInflater.from(context)).run {
            notificationOngoingSmall.weatherIcon.setImageResource(weatherIcon)
            notificationOngoingSmall.temperature.text = temperature.convertUnit(units.temperatureUnit).toString()
            notificationOngoingSmall.feelsLikeTemperature.text = feelsLikeTemperature.convertUnit(units.temperatureUnit).toString()

            notificationOngoingSmall.yesterdayTemperature.isVisible = false
            hourlyForecast.isVisible = false
            root
        }
    }
}