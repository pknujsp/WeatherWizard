package io.github.pknujsp.everyweather.feature.componentservice

import android.widget.FrameLayout
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import io.github.pknujsp.everyweather.core.model.settings.CurrentUnits
import io.github.pknujsp.everyweather.core.ui.theme.AppShapes
import io.github.pknujsp.everyweather.core.widgetnotification.remoteview.DefaultRemoteViewCreator

@Composable
fun RemoteViewsScreen(
    sampleRemoteViews: DefaultRemoteViewCreator,
    units: CurrentUnits,
    modifier: Modifier = Modifier,
) {
    Box(contentAlignment = androidx.compose.ui.Alignment.Center) {
        Surface(
            shape = AppShapes.large,
            modifier = modifier.padding(16.dp),
            shadowElevation = 6.dp,
        ) {
            AndroidView(
                modifier =
                    modifier
                        .fillMaxWidth()
                        .heightIn(max = 330.dp),
                factory = { context ->
                    FrameLayout(context).apply {
                        addView(sampleRemoteViews.createSampleView(context, units).apply(context, this))
                    }
                },
                update = {},
            )
        }
    }
}
