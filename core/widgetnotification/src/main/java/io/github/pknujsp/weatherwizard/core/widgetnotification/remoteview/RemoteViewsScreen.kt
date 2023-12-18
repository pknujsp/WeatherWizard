package io.github.pknujsp.weatherwizard.core.widgetnotification.remoteview

import android.widget.FrameLayout
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import io.github.pknujsp.weatherwizard.core.model.weather.common.CurrentUnits
import io.github.pknujsp.weatherwizard.core.ui.theme.AppShapes


@Composable
fun RemoteViewsScreen(sampleRemoteViews: DefaultRemoteViewCreator, units: CurrentUnits, modifier: Modifier = Modifier) {
    Surface(shape = AppShapes.large, modifier = modifier.padding(16.dp, 12.dp),
        shadowElevation = 4.dp) {
        AndroidView(modifier = Modifier.fillMaxWidth(), factory = { context ->
            FrameLayout(context).apply {
                addView(sampleRemoteViews.createSampleView(context, units).apply(context, this))
            }
        }, update = {

        })
    }
}