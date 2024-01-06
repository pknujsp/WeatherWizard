package io.github.pknujsp.weatherwizard.core.data.nominatim

import android.util.LruCache
import io.github.pknujsp.weatherwizard.core.common.util.GeographicalDistanceCalculator
import io.github.pknujsp.weatherwizard.core.common.util.toCoordinate
import io.github.pknujsp.weatherwizard.core.model.nominatim.GeoCodeEntity
import io.github.pknujsp.weatherwizard.core.model.nominatim.ReverseGeoCodeEntity
import io.github.pknujsp.weatherwizard.core.network.api.nominatim.NominatimDataSource

internal class NominatimRepositoryImpl(
    private val dataSource: NominatimDataSource
) : NominatimRepository {

    private val geoCodeCacheMap = LruCache<String, List<GeoCodeEntity>>(5)
    private val reverseCodeCacheMap = LruCache<Int, ReverseGeoCodeEntity>(5)

    override suspend fun geoCode(query: String): Result<List<GeoCodeEntity>> {
        return geoCodeCacheMap[query]?.run {
            Result.success(this)
        } ?: run {
            dataSource.getCode(query).map { response ->
                val mapped = response.features.map { feature ->
                    GeoCodeEntity(
                        displayName = feature.properties.displayName,
                        countryCode = feature.properties.address.countryCode,
                        country = feature.properties.address.country,
                        latitude = feature.geometry.coordinates[1].toCoordinate(),
                        longitude = feature.geometry.coordinates[0].toCoordinate(),
                        county = feature.properties.address.county,
                        city = feature.properties.address.city,
                        province = feature.properties.address.province,
                        road = feature.properties.address.road,
                        quarter = feature.properties.address.quarter,
                        state = feature.properties.address.state,
                        suburb = feature.properties.address.suburb,
                        placeId = feature.properties.placeId.toLong(),
                        category = feature.properties.category,
                        osmType = feature.properties.osmType,
                    )
                }
                geoCodeCacheMap.put(query, mapped)
                mapped
            }
        }
    }

    override suspend fun reverseGeoCode(latitude: Double, longitude: Double): Result<ReverseGeoCodeEntity> {
        val key = toKey(latitude, longitude)

        return reverseCodeCacheMap[key]?.run {
            Result.success(this)
        } ?: run {
            dataSource.reverseGeoCode(latitude, longitude).map {
                val proximate = it.features.minBy { feature ->
                    GeographicalDistanceCalculator.calculateDistance(latitude,
                        longitude,
                        feature.geometry.coordinates[1].toCoordinate(),
                        feature.geometry.coordinates[0].toCoordinate())
                }

                val result = proximate.run {
                    ReverseGeoCodeEntity(
                        displayName = properties.displayName,
                        countryCode = properties.address.countryCode,
                        country = properties.address.country,
                        latitude = geometry.coordinates[1].toCoordinate(),
                        longitude = geometry.coordinates[0].toCoordinate(),
                        county = properties.address.county,
                        city = properties.address.city,
                        province = properties.address.province,
                        road = properties.address.road,
                        quarter = properties.address.quarter,
                        state = properties.address.state,
                        suburb = properties.address.suburb,
                    )
                }
                reverseCodeCacheMap.put(key, result)
                result
            }
        }
    }

    private fun toKey(latitude: Double, longitude: Double) = latitude.hashCode() + 31 * longitude.hashCode()
}