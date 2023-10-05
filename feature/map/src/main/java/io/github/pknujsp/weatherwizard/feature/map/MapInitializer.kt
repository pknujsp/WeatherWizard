package io.github.pknujsp.weatherwizard.feature.map

import android.content.Context
import org.osmdroid.config.Configuration

class MapInitializer(context: Context) {
    init {
        Configuration.getInstance().run {
            userAgentValue = context.packageName
            animationSpeedShort = 200
            animationSpeedDefault = 200
            cacheMapTileOvershoot = (12).toShort()
            cacheMapTileCount = (12).toShort()
        }
    }
}