package io.github.pknujsp.weatherwizard.feature.notification.util

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.text.TextPaint
import android.util.TypedValue
import androidx.core.graphics.drawable.IconCompat
import io.github.pknujsp.weatherwizard.core.common.util.DayNightCalculator
import io.github.pknujsp.weatherwizard.core.common.util.toCalendar
import io.github.pknujsp.weatherwizard.core.domain.weather.WeatherResponseEntity
import io.github.pknujsp.weatherwizard.core.model.notification.enums.NotificationIconType
import io.github.pknujsp.weatherwizard.core.model.weather.common.CurrentUnits
import io.github.pknujsp.weatherwizard.core.model.weather.current.CurrentWeatherEntity

object NotificationIconGenerator {
    private fun createTemperatureIcon(context: Context, temperature: String): IconCompat {
        val textSize = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 19f, context.resources.displayMetrics)
        val textRect = Rect()

        val textPaint = TextPaint().apply {
            color = Color.WHITE
            typeface = android.graphics.Typeface.create("sans-serif-condensed", android.graphics.Typeface.BOLD)
            textAlign = Paint.Align.CENTER
            textScaleX = 0.9f
            isAntiAlias = true
            setTextSize(textSize)
            getTextBounds(temperature, 0, temperature.length, textRect)
            style = Paint.Style.FILL
        }

        val iconSize = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 24f, context.resources.displayMetrics).toInt()
        val iconBitmap = Bitmap.createBitmap(iconSize, iconSize, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(iconBitmap)

        val x = canvas.width / 2f
        val y = canvas.height / 2f + textRect.height() / 2f

        canvas.drawText(temperature, x, y, textPaint)
        textPaint.apply {
            color = Color.BLACK
            style = Paint.Style.STROKE
            strokeWidth = 1f
        }
        canvas.drawText(temperature, x, y, textPaint)

        return IconCompat.createWithBitmap(iconBitmap)
    }

    fun createIcon(
        context: Context, notificationIconType: NotificationIconType, weatherResponseEntity: WeatherResponseEntity, units: CurrentUnits
    ) = when (notificationIconType) {
        NotificationIconType.TEMPERATURE -> createTemperatureIcon(context,
            weatherResponseEntity.toEntity<CurrentWeatherEntity>().temperature.convertUnit(units.temperatureUnit).toString())

        NotificationIconType.ICON -> IconCompat.createWithResource(context,
            weatherResponseEntity.toEntity<CurrentWeatherEntity>().weatherCondition.value.getWeatherIconByTimeOfDay(weatherResponseEntity.dayNightCalculator.calculate(
                weatherResponseEntity.responseTime.toCalendar()) == DayNightCalculator.DayNight.DAY))
    }
}