package io.github.pknujsp.weatherwizard.core.widgetnotification.notification.util

import android.content.Context
import android.content.res.Resources
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
import io.github.pknujsp.weatherwizard.core.model.settings.CurrentUnits
import io.github.pknujsp.weatherwizard.core.model.weather.current.CurrentWeatherEntity

object NotificationIconGenerator {

    private const val ICON_WIDTH = 33f
    private const val ICON_HEIGHT = 28f
    private const val FONT_FAMILY = "sans-serif-condensed"

    private val iconWidth = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, ICON_WIDTH, Resources.getSystem().displayMetrics).toInt()
    private val iconHeight =
        TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, ICON_HEIGHT, Resources.getSystem().displayMetrics).toInt()

    private val textPaint = TextPaint().apply {
        color = Color.BLACK
        textSize = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 28f, Resources.getSystem().displayMetrics)
        typeface = android.graphics.Typeface.create(FONT_FAMILY, android.graphics.Typeface.NORMAL)
        textAlign = Paint.Align.CENTER
        isAntiAlias = true
        textScaleX = 0.94f
    }

    private fun createTemperatureIcon(temperature: String): IconCompat {
        val textRect = Rect()
        textPaint.getTextBounds(temperature, 0, temperature.length, textRect)
        val iconBitmap = Bitmap.createBitmap(iconWidth, iconHeight, Bitmap.Config.ARGB_8888)

        val canvas = Canvas(iconBitmap)
        val centerXOnCanvas = canvas.width / 2f
        val centerYOnCanvas = canvas.height / 2f

        val y = centerYOnCanvas + textRect.height() / 2f

        canvas.drawText(temperature, centerXOnCanvas, y, textPaint)
        return IconCompat.createWithBitmap(iconBitmap)
    }


    fun createIcon(
        context: Context, notificationIconType: NotificationIconType, weatherResponseEntity: WeatherResponseEntity, units: CurrentUnits
    ) = when (notificationIconType) {
        NotificationIconType.TEMPERATURE -> createTemperatureIcon(weatherResponseEntity.toEntity<CurrentWeatherEntity>().temperature.convertUnit(
            units.temperatureUnit).toStringWithOnlyDegree())

        NotificationIconType.ICON -> IconCompat.createWithResource(context,
            weatherResponseEntity.toEntity<CurrentWeatherEntity>().weatherCondition.value.getWeatherIconByTimeOfDay(weatherResponseEntity.dayNightCalculator.calculate(
                weatherResponseEntity.responseTime.toCalendar()) == DayNightCalculator.DayNight.DAY))
    }
}