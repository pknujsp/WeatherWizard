package io.github.pknujsp.weatherwizard.feature.map

import android.content.Context
import androidx.preference.PreferenceManager
import org.osmdroid.config.Configuration


class MapInitializer {

    companion object {
        private var initialized = false

        fun initialize(context: Context) {
            if (!initialized) {
                initialized = true
                Configuration.getInstance().load(context, PreferenceManager.getDefaultSharedPreferences(context))
                Configuration.getInstance().apply {
                    animationSpeedShort = 200
                    animationSpeedDefault = 200
                    cacheMapTileOvershoot = (12).toShort()
                    cacheMapTileCount = (12).toShort()
                    isMapViewHardwareAccelerated = true
                }
            }
        }
    }

}