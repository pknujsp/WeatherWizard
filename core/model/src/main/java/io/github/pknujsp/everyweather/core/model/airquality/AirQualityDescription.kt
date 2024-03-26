package io.github.pknujsp.everyweather.core.model.airquality

import androidx.annotation.StringRes
import androidx.compose.ui.graphics.Color
import io.github.pknujsp.everyweather.core.model.airquality.AirQualityDescription.GOOD
import io.github.pknujsp.everyweather.core.model.airquality.AirQualityDescription.HAZARDOUS
import io.github.pknujsp.everyweather.core.model.airquality.AirQualityDescription.MODERATE
import io.github.pknujsp.everyweather.core.model.airquality.AirQualityDescription.UNHEALTHY
import io.github.pknujsp.everyweather.core.model.airquality.AirQualityDescription.UNHEALTHY_FOR_SENSITIVE_GROUPS
import io.github.pknujsp.everyweather.core.model.airquality.AirQualityDescription.VERY_UNHEALTHY
import io.github.pknujsp.everyweather.core.resource.R
import kotlinx.serialization.Serializable

/**
 * @param descriptionStringId String resource id for the description of the air quality
 * @param color Color for the air quality
 * @param range Range of the air quality
 *
 * @property GOOD 좋음
 * @property MODERATE 보통
 * @property UNHEALTHY_FOR_SENSITIVE_GROUPS 약간 나쁨
 * @property UNHEALTHY 나쁨
 * @property VERY_UNHEALTHY 매우 나쁨
 * @property HAZARDOUS 최악
 */
@Serializable
enum class AirQualityDescription(
    @StringRes val descriptionStringId: Int,
    val color: Color,
    val range: IntRange,
    val description: String,
) {
    GOOD(R.string.airquality_0_good, Color(0xFF009865), 0..50, "Good"),
    MODERATE(R.string.airquality_1_moderate, Color(0xfffede33), 51..100, "Moderate"),
    UNHEALTHY_FOR_SENSITIVE_GROUPS(R.string.airquality_2_unhealthy_for_sensitive_groups, Color(0xFFFF9934), 101..150, "Slightly bad"),
    UNHEALTHY(R.string.airquality_3_unhealthy, Color(0xFFCC0033), 151..200, "Bad"),
    VERY_UNHEALTHY(R.string.airquality_4_very_unhealthy, Color(0xFF670099), 201..300, "Very bad"),
    HAZARDOUS(R.string.airquality_5_hazardous, Color(0xFF7E0123), 301..500, "Hazardous"),
    ;

    companion object {
        fun fromValue(value: Short): AirQualityDescription {
            return when (value.toInt()) {
                in GOOD.range -> GOOD
                in MODERATE.range -> MODERATE
                in UNHEALTHY_FOR_SENSITIVE_GROUPS.range -> UNHEALTHY_FOR_SENSITIVE_GROUPS
                in UNHEALTHY.range -> UNHEALTHY
                in VERY_UNHEALTHY.range -> VERY_UNHEALTHY
                else -> HAZARDOUS
            }
        }
    }
}