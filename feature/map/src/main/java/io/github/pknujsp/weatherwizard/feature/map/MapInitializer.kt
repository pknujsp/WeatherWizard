package io.github.pknujsp.weatherwizard.feature.map

import android.content.Context
import android.os.Environment
import android.os.StrictMode
import org.osmdroid.config.Configuration
import java.io.File


class MapInitializer(context: Context) {

    private companion object {
        var initialized = false
    }

    init {
        if (!initialized) {
            initialized = true
            Configuration.getInstance().run {
                userAgentValue = context.packageName
                animationSpeedShort = 200
                animationSpeedDefault = 200
                cacheMapTileOvershoot = (12).toShort()
                cacheMapTileCount = (12).toShort()

                val policy = StrictMode.ThreadPolicy.Builder().permitAll().build()
                StrictMode.setThreadPolicy(policy)
            }
        }
    }
}