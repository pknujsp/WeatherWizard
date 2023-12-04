package io.github.pknujsp.weatherwizard.core.model.favorite

import androidx.annotation.DrawableRes
import io.github.pknujsp.weatherwizard.core.common.enum.BaseEnum
import io.github.pknujsp.weatherwizard.core.common.enum.IEnum

sealed interface LocationType : IEnum {
    val latitude: Double
    val longitude: Double
    val address: String

    data class CurrentLocation(
        override val latitude: Double = 0.0, override val longitude: Double = 0.0, override val address: String = ""
    ) : LocationType {
        override val title: Int get() = io.github.pknujsp.weatherwizard.core.common.R.string.current_location
        override val key: Int = 0

        override fun equals(other: Any?): Boolean = other is CurrentLocation
        override fun hashCode(): Int = 0
    }

    data class CustomLocation(
        val locationId: Long = 0,
        override val latitude: Double = 0.0,
        override val longitude: Double = 0.0,
        override val address: String = ""
    ) : LocationType {
        override val title: Int get() = io.github.pknujsp.weatherwizard.core.common.R.string.custom_location
        override val key: Int = 1

        override fun equals(other: Any?): Boolean = other is CustomLocation
        override fun hashCode(): Int = 0
    }

    companion object : BaseEnum<LocationType> {
        override val enums = arrayOf(CurrentLocation(), CustomLocation())

        override val default: LocationType
            get() = CurrentLocation()
        override val key: String
            get() = "LocationType"

        @DrawableRes val icon: Int = io.github.pknujsp.weatherwizard.core.common.R.drawable.ic_location
    }
}