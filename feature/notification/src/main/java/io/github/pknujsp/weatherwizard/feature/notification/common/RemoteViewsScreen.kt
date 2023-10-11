package io.github.pknujsp.weatherwizard.feature.notification.common

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import androidx.annotation.DrawableRes
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
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
fun RemoteViewsScreen(iRemoteViews: IRemoteViews) {
    Surface(shape = AppShapes.large, modifier = Modifier,
        shadowElevation = 4.dp) {
        AndroidView(modifier = Modifier.fillMaxWidth(), factory = { context ->
            iRemoteViews.create(context)
        }, update = {

        })
    }
}

interface IRemoteViews {
    fun create(context: Context): View
}

class OngoingNotificationRemoteViews(private val units: CurrentUnits) : IRemoteViews {
    @DrawableRes val weatherIcon: Int = io.github.pknujsp.weatherwizard.core.common.R.drawable.ic_sun
    private val temperature: TemperatureValueType = TemperatureValueType(16.0, TemperatureUnit.Celsius)
    private val feelsLikeTemperature: TemperatureValueType = TemperatureValueType(14.0, TemperatureUnit.Celsius)
    private val address: String = "Address"

    override fun create(context: Context): View {
        return NotificationOngoingBigBinding.inflate(LayoutInflater.from(context)).run {
            notificationOngoingSmall.weatherIcon.setImageResource(weatherIcon)
            notificationOngoingSmall.temperature.text = temperature.convertUnit(units.temperatureUnit).toString()
            notificationOngoingSmall.feelsLikeTemperature.text = feelsLikeTemperature.convertUnit(units.temperatureUnit).toString()
            notificationOngoingSmall.address.text = address

            notificationOngoingSmall.yesterdayTemperature.isVisible = false
            notificationOngoingSmall.progressbar.isVisible = false
            progressbar.isVisible = false
            hourlyForecast.isVisible = false
            root
        }
    }
}