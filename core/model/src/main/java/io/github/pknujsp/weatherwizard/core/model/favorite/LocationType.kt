package io.github.pknujsp.weatherwizard.core.model.favorite

import androidx.annotation.DrawableRes
import io.github.pknujsp.weatherwizard.core.common.BaseEnum
import io.github.pknujsp.weatherwizard.core.common.IEnum

sealed interface LocationType : IEnum {

    data object CurrentLocation : LocationType {
        override val title: Int get() = io.github.pknujsp.weatherwizard.core.common.R.string.current_location
        override val key: Int = 0
    }

    data class CustomLocation(
        val locationId: Long = 0, val latitude: Double = 0.0, val longitude: Double = 0.0, val address: String = ""
    ) : LocationType {

        override val title: Int get() = io.github.pknujsp.weatherwizard.core.common.R.string.custom_location
        override val key: Int = 1
    }

    companion object : BaseEnum<LocationType> {
        override val enums get() = arrayOf(CurrentLocation, CustomLocation())

        override val default: LocationType
            get() = CurrentLocation
        override val key: String
            get() = "LocationType"

        @DrawableRes val icon: Int = io.github.pknujsp.weatherwizard.core.common.R.drawable.ic_location

    }
}