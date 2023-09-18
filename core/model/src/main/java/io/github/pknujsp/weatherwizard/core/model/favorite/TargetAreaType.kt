package io.github.pknujsp.weatherwizard.core.model.favorite

sealed class TargetAreaType(
    val id: Long,
) {
    data object CurrentLocation : TargetAreaType(-1L)
    class CustomLocation(id: Long) : TargetAreaType(id)
}