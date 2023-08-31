package io.github.pknujsp.weatherwizard.core.common.util

import kotlin.math.acos
import kotlin.math.cos
import kotlin.math.sin

object LocationDistance {
    fun distance(lat1: Double, lon1: Double, lat2: Double, lon2: Double, unit: Unit): Double {
        val theta = lon1 - lon2
        var dist = sin(deg2rad(lat1)) * sin(deg2rad(lat2)) + cos(deg2rad(lat1)) * cos(deg2rad(lat2)) * cos(deg2rad(theta))
        dist = rad2deg(acos(dist))
        dist *= 60 * 1.1515
        dist *= if (unit == Unit.KM) 1.609344
        else 1609.344

        return dist
    }

    private fun deg2rad(deg: Double): Double = deg * Math.PI / 180.0

    private fun rad2deg(rad: Double): Double = rad * 180 / Math.PI

    enum class Unit {
        METER, KM
    }
}