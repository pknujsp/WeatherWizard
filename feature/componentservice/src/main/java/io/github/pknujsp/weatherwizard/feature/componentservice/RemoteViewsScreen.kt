package io.github.pknujsp.weatherwizard.feature.componentservice

import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.RemoteViews
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import io.github.pknujsp.weatherwizard.core.model.weather.common.CurrentUnits
import io.github.pknujsp.weatherwizard.core.ui.theme.AppShapes
import io.github.pknujsp.weatherwizard.core.widgetnotification.remoteview.DefaultRemoteViewCreator


@Composable
fun RemoteViewsScreen(sampleRemoteViews: DefaultRemoteViewCreator, units: CurrentUnits, modifier: Modifier = Modifier) {
    Box(modifier = modifier.fillMaxWidth(), contentAlignment = androidx.compose.ui.Alignment.Center) {
        Surface(
            shape = AppShapes.large,
            modifier = modifier.padding(16.dp, 12.dp),
            shadowElevation = 6.dp,
        ) {
            //val viewPadding = 16.dp.value.toInt()
            AndroidView(modifier = modifier.heightIn(max = 330.dp), factory = { context ->
                FrameLayout(context).apply {
                    addView(sampleRemoteViews.createSampleView(context, units).apply(context, this))
                }
            }, update = {})
        }
    }
}

private fun ViewGroup.updateViewLayoutParamsForRemoteViews(remoteViews: RemoteViews) {
}