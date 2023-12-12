package io.github.pknujsp.weatherwizard.core.model.remoteviews

import android.widget.RemoteViews
import androidx.annotation.DrawableRes
import androidx.core.graphics.drawable.IconCompat
import io.github.pknujsp.weatherwizard.core.model.UiModel

data class NotificationViewState(
    val success: Boolean = true,
    var smallContentRemoteViews: RemoteViews? = null,
    var bigContentRemoteViews: RemoteViews? = null,
    val subText: String = "",
    var failedContentRemoteViews: RemoteViews? = null,
    val smallIcon: IconCompat? = null,
    @DrawableRes val smallIconId: Int = io.github.pknujsp.weatherwizard.core.common.R.mipmap.ic_launcher
)