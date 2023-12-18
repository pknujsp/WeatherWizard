package io.github.pknujsp.weatherwizard.feature.map

import android.content.Context
import androidx.preference.PreferenceManager
import org.osmdroid.config.Configuration
import java.util.concurrent.atomic.AtomicBoolean


class MapInitializer {

    companion object {
        private val initialized = AtomicBoolean(false)

        fun initialize(context: Context) {
            if (!initialized.get()) {
                initialized.getAndSet(true)
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