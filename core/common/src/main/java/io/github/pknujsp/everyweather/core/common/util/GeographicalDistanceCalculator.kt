package io.github.pknujsp.everyweather.core.common.util

import kotlin.math.acos
import kotlin.math.cos
import kotlin.math.sin

object GeographicalDistanceCalculator {

    private const val MULTIPLIER_FROM_NAUTICAL_MILES_TO_MILES = 60 * 1.1515

    /*
       fun distance(lat1: Double, lon1: Double, lat2: Double, lon2: Double, unit: Unit): Double {
           val theta = lon1 - lon2
           var dist = sin(deg2rad(lat1)) * sin(deg2rad(lat2)) + cos(deg2rad(lat1)) * cos(deg2rad(lat2)) * cos(deg2rad(theta))
           dist = rad2deg(acos(dist))
           dist *= 60 * 1.1515
           dist *= if (unit == Unit.KM) 1.609344
           else 1609.344

           return dist
       }
   */

    fun calculateDistance(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Double {
        val theta = lon1 - lon2
        var dist = sin(lat1.deg2rad()) * sin(lat2.deg2rad()) + cos(lat1.deg2rad()) * cos(lat2.deg2rad()) * cos(theta.deg2rad())
        dist = acos(dist).rad2deg() * MULTIPLIER_FROM_NAUTICAL_MILES_TO_MILES

        return dist
    }

    private fun Double.deg2rad() = this * Math.PI / 180

    private fun Double.rad2deg() = this * 180 / Math.PI

    enum class Unit(val multiplierFromMile: Double) {
        METER(1609.344), KM(1.609344),
    }
}