package io.github.pknujsp.weatherwizard.core.model.favorite

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
sealed class TargetAreaType(
    val locationId: Long,
    val typeId: Int
) : Parcelable {

    data object CurrentLocation : TargetAreaType(-1L, 0)
    class CustomLocation(val id: Long) : TargetAreaType(id, 1)
}