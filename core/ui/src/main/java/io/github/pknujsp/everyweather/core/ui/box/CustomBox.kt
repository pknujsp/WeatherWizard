package io.github.pknujsp.everyweather.core.ui.box

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.SubcomposeLayout
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.offset

private val snackbarHorizontalInset = 16.dp
private val snackbarBottomInset = 20.dp
private val snackbarPaddingValues =
    PaddingValues(start = snackbarHorizontalInset, end = snackbarHorizontalInset, bottom = snackbarBottomInset)

@Composable
fun CustomBox(
    modifier: Modifier = Modifier,
    contentAlignment: Alignment = Alignment.TopStart,
    propagateMinConstraints: Boolean = false,
    snackbarHost: @Composable () -> Unit = {},
    content: @Composable BoxScope.() -> Unit,
) {
    Box(modifier = modifier, contentAlignment = contentAlignment, propagateMinConstraints = propagateMinConstraints) {
        content()
        SubcomposeLayout { constraints ->
            val layoutWidth = constraints.maxWidth
            val layoutHeight = constraints.maxHeight
            val looseConstraints = constraints.copy(minWidth = 0, minHeight = 0)

            layout(layoutWidth, layoutHeight) {
                val snackbarPlaceables =
                    subcompose(CustomBoxLayoutContent.Snackbar, snackbarHost).map {
                        val leftInset = snackbarPaddingValues.calculateLeftPadding(layoutDirection).toPx().toInt()
                        val rightInset = snackbarPaddingValues.calculateLeftPadding(layoutDirection).toPx().toInt()
                        val bottomInset = snackbarPaddingValues.calculateBottomPadding().toPx().toInt()
                        it.measure(looseConstraints.offset(-leftInset - rightInset, -bottomInset))
                    }

                val snackbarHeight = snackbarPlaceables.maxByOrNull { it.height }?.height ?: 0
                val snackbarWidth = snackbarPlaceables.maxByOrNull { it.width }?.width ?: 0

                snackbarPlaceables.forEach {
                    it.place((layoutWidth - snackbarWidth) / 2, layoutHeight - snackbarHeight)
                }
            }
        }
    }
}

private enum class CustomBoxLayoutContent { TopBar, MainContent, Snackbar, Fab, BottomBar }
