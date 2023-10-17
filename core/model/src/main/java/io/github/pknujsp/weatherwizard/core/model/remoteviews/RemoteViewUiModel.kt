package io.github.pknujsp.weatherwizard.core.model.remoteviews

import android.widget.RemoteViews
import androidx.annotation.DrawableRes
import androidx.core.graphics.drawable.IconCompat
import io.github.pknujsp.weatherwizard.core.model.UiModel

data class RemoteViewUiModel(
    val success: Boolean = true,
    val smallContentRemoteViews: RemoteViews,
    val bigContentRemoteViews: RemoteViews,
    val subText: String,
    val failedContentRemoteViews: RemoteViews? = null,
    val smallIcon: IconCompat? = null,
    @DrawableRes val smallIconId: Int = io.github.pknujsp.weatherwizard.core.common.R.mipmap.ic_launcher
) : UiModel {

}