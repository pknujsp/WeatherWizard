package io.github.pknujsp.everyweather.feature.map

import android.app.Application
import android.content.Context
import androidx.annotation.WorkerThread
import androidx.preference.PreferenceManager
import io.github.pknujsp.everyweather.core.resource.R
import io.github.pknujsp.everyweather.feature.map.model.MapSettingsDefault
import org.osmdroid.config.Configuration
import org.osmdroid.mapsforge.MapsForgeTileProvider
import org.osmdroid.mapsforge.MapsForgeTileSource
import org.osmdroid.tileprovider.util.SimpleRegisterReceiver
import org.osmdroid.views.MapView
import java.util.concurrent.atomic.AtomicBoolean


object OsmdroidInitializer {
    private const val MAPSFORGE_MAP_FILES_DIR = "mapsforge"
    private const val MAPSFORGE_MAP_FILE_FORMAT = ".map"
    private const val FILE_SYSTEM_THREADS: Short = 3
    private const val DOWNLOAD_THREADS: Short = 3

    private val isInitialized = AtomicBoolean(false)
    private var mapsForgeTileSource: MapsForgeTileSource? = null

    @WorkerThread
    fun initialize(application: Application) {
        if (!isInitialized.get()) {
            isInitialized.getAndSet(true)

            Configuration.getInstance().run {
                load(application, PreferenceManager.getDefaultSharedPreferences(application))
                tileDownloadThreads = minOf(DOWNLOAD_THREADS, Runtime.getRuntime().availableProcessors().toShort())
                tileFileSystemThreads = minOf(FILE_SYSTEM_THREADS, Runtime.getRuntime().availableProcessors().toShort())
                tileDownloadMaxQueueSize = 20
                tileFileSystemMaxQueueSize = 20
                animationSpeedShort = 200
                animationSpeedDefault = 200
            }
            MapsForgeTileSource.createInstance(application)
            getMapsForgeTileSource(application)
        }
    }

    private fun getMapsForgeTileSource(context: Context): MapsForgeTileSource = synchronized(this) {
        mapsForgeTileSource ?: run {
            val assetManager = context.assets
            val mapsForgeFiles = assetManager.list(MAPSFORGE_MAP_FILES_DIR)?.filter {
                it.endsWith(MAPSFORGE_MAP_FILE_FORMAT)
            }?.map {
                assetManager.open("$MAPSFORGE_MAP_FILES_DIR/$it").use { inputStream ->
                    val file = context.getFileStreamPath(it)
                    inputStream.copyTo(file.outputStream())
                    file
                }
            }?.toTypedArray()

            MapsForgeTileSource.createFromFiles(mapsForgeFiles).apply {
                setUserScaleFactor(0.7f)
                mapsForgeTileSource = this
            }
        }
    }

    fun getMapsForgeTileProvider(context: Context): MapsForgeTileProvider =
        MapsForgeTileProvider(SimpleRegisterReceiver(context), getMapsForgeTileSource(context), null)

    fun initializeMapView(mapView: MapView) {
        mapView.run {
            clipToOutline = true
            setBackgroundResource(R.drawable.map_background)
            tileProvider.setUseDataConnection(false)
            maxZoomLevel = MapSettingsDefault.MAX_ZOOM_LEVEL
            minZoomLevel = MapSettingsDefault.MIN_ZOOM_LEVEL

            setMultiTouchControls(true)
            zoomController.setVisibility(org.osmdroid.views.CustomZoomButtonsController.Visibility.NEVER)

            isHorizontalMapRepetitionEnabled = true
            isVerticalMapRepetitionEnabled = true
        }
    }


}