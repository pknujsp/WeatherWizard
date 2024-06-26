package io.github.pknujsp.everyweather.core.widgetnotification.remoteview

import android.widget.RemoteViews
import androidx.annotation.IdRes

fun RemoteViews.addViewSafely(
    @IdRes viewId: Int,
    nestedView: RemoteViews,
) {
    removeAllViews(viewId)
    addView(viewId, nestedView)
}
