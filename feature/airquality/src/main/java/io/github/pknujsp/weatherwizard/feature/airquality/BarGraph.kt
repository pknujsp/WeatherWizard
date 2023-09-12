package io.github.pknujsp.weatherwizard.feature.airquality

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.Layout


@Composable
fun BarGraph() {

}

@Composable
private fun Body() {

}

@Composable
private fun Bar(barGraphTheme: BarGraphTheme, intValue: Int, minValue: Int, maxValue: Int) {
    Layout(
        content = {
            Box(
                modifier = Modifier
                    .width(barGraphTheme.barSize.width)
                    .height(barGraphTheme.barSize.height)
                    .padding(horizontal = barGraphTheme.barSize.horizontalPadding)
            )
            Text(
                text = intValue.toString(),
                modifier = Modifier.padding(horizontal = barGraphTheme.barSize.horizontalPadding)
            )
        }
    ) { measurables, constraints ->
        val placeables = measurables.map { measurable ->
            measurable.measure(constraints)
        }

        layout(
            width = constraints.maxWidth,
            height = constraints.maxHeight
        ) {
            placeables.forEach { placeable ->
                placeable.placeRelative(0, 0)
            }
        }
    }
}