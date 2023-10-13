package io.github.pknujsp.weatherwizard.core.model.remoteviews

import android.widget.RemoteViews
import androidx.annotation.DrawableRes
import io.github.pknujsp.weatherwizard.core.model.EntityModel

data class RemoteViewsEntity(
    val success:Boolean = true,
    val smallContentRemoteViews: RemoteViews,
    val bigContentRemoteViews: RemoteViews,
    val subText: String,
    @DrawableRes val smallIcon: Int,
    val failedContentRemoteViews: RemoteViews? = null,
) : EntityModel