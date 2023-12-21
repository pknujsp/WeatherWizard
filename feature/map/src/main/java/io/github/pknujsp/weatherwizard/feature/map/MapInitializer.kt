package io.github.pknujsp.weatherwizard.feature.map

import android.content.Context
import androidx.preference.PreferenceManager
import org.osmdroid.config.Configuration
import java.util.concurrent.atomic.AtomicBoolean


object MapInitializer {

    private val isInitialized = AtomicBoolean(false)

    fun initialize(context: Context) {
        if (!isInitialized.get()) {
            isInitialized.getAndSet(true)

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