package io.github.pknujsp.everyweather.core.model.coordinate

import androidx.annotation.DrawableRes
import io.github.pknujsp.everyweather.core.model.settings.BaseEnum
import io.github.pknujsp.everyweather.core.model.settings.IEnum

sealed interface LocationType : IEnum {
    data object CurrentLocation : LocationType {
        override val title: Int get() = io.github.pknujsp.everyweather.core.resource.R.string.current_location
        override val key: Int = 0
        override val icon: Int? = null
    }

    data object CustomLocation : LocationType {
        override val title: Int get() = io.github.pknujsp.everyweather.core.resource.R.string.custom_location
        override val key: Int = 1
        override val icon: Int? = null
    }

    companion object : BaseEnum<LocationType> {
        override val enums = arrayOf(CurrentLocation, CustomLocation)

        override val default: LocationType
            get() = CurrentLocation
        override val key: String
            get() = "LocationType"

        @DrawableRes val icon: Int = io.github.pknujsp.everyweather.core.resource.R.drawable.ic_location
    }
}
