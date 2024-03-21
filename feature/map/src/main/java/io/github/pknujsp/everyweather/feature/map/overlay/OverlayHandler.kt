package io.github.pknujsp.everyweather.feature.map.overlay

import android.os.Handler
import android.os.Looper
import android.os.Message
import android.view.View
import org.osmdroid.tileprovider.MapTileProviderBase

class OverlayHandler : Handler(Looper.getMainLooper()) {
    var mView: View? = null

    override fun handleMessage(msg: Message) {
        when (msg.what) {
            MapTileProviderBase.MAPTILE_SUCCESS_ID -> mView?.invalidate()
        }
    }

    fun destroy() {
        mView = null
    }
}
