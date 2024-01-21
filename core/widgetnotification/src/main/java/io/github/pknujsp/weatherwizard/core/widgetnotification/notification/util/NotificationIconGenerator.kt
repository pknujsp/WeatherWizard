package io.github.pknujsp.weatherwizard.core.widgetnotification.notification.util

import android.content.Context
import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.text.TextPaint
import android.util.DisplayMetrics
import android.util.Size
import android.util.TypedValue
import androidx.core.graphics.drawable.IconCompat
import io.github.pknujsp.weatherwizard.core.common.util.DayNightCalculator
import io.github.pknujsp.weatherwizard.core.common.util.toCalendar
import io.github.pknujsp.weatherwizard.core.domain.weather.WeatherResponseEntity
import io.github.pknujsp.weatherwizard.core.model.notification.enums.NotificationIconType
import io.github.pknujsp.weatherwizard.core.model.settings.CurrentUnits
import io.github.pknujsp.weatherwizard.core.model.weather.current.CurrentWeatherEntity

object NotificationIconGenerator {

    private val iconSize: Size = calculateIconSizeForDeviceResolution().run {
        Size(this, this)
    }

    private val textPaint = TextPaint().apply {
        color = Color.BLACK
        textSize = iconSize.height - 2f
        typeface = android.graphics.Typeface.create("sans-serif-condensed", android.graphics.Typeface.NORMAL)
        textAlign = Paint.Align.CENTER
        isAntiAlias = true
    }

    private fun createTemperatureIcon(temperature: String): IconCompat {
        val textRect = Rect()
        textPaint.getTextBounds(temperature, 0, temperature.length, textRect)
        val iconBitmap = Bitmap.createBitmap(iconSize.width, iconSize.height, Bitmap.Config.ARGB_8888)

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

    private fun calculateIconSizeForDeviceResolution(): Int {
        val metrics = Resources.getSystem().displayMetrics
        return when (metrics.densityDpi) {
            DisplayMetrics.DENSITY_LOW -> 24
            DisplayMetrics.DENSITY_MEDIUM -> 24
            DisplayMetrics.DENSITY_HIGH -> 36
            DisplayMetrics.DENSITY_XHIGH -> 48
            DisplayMetrics.DENSITY_XXHIGH -> 72
            DisplayMetrics.DENSITY_XXXHIGH -> 96
            else -> 96
        }
    }
}

/*
* mdpi
24x24

hdpi
36x36

xhdpi
48x48

xxhdpi
72x72

xxxhdpi
96x96

*
* */