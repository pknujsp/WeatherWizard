package io.github.pknujsp.weatherwizard.feature.notification.util

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
import io.github.pknujsp.weatherwizard.core.model.weather.common.CurrentUnits
import io.github.pknujsp.weatherwizard.core.model.weather.current.CurrentWeatherEntity

object NotificationIconGenerator {

    private const val TEXT_SIZE = 22f
    private const val ICON_SIZE = 26f
    private const val FONT_FAMILY = "sans-serif-condensed"

    private val textSize = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, TEXT_SIZE, Resources.getSystem().displayMetrics)
    private val iconSize = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, ICON_SIZE, Resources.getSystem().displayMetrics).toInt()
    private val textPaint = TextPaint().apply {
        color = Color.BLACK
        typeface = android.graphics.Typeface.create(FONT_FAMILY, android.graphics.Typeface.NORMAL)
        textAlign = Paint.Align.CENTER
        isAntiAlias = true
        textSize = this@NotificationIconGenerator.textSize
    }

    private fun createTemperatureIcon(temperature: String): IconCompat {
        val textRect = Rect()
        textPaint.getTextBounds(temperature, 0, temperature.length, textRect)

        val iconBitmap = Bitmap.createBitmap(iconSize, iconSize, Bitmap.Config.ARGB_8888)

        val canvas = Canvas(iconBitmap)
        val x = canvas.width / 2f
        val y = (canvas.height / 2f) - ((textPaint.descent() + textPaint.ascent()) / 2f)

        canvas.drawText(temperature, x, y, textPaint)

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