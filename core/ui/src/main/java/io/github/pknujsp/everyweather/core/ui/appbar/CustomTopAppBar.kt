package io.github.pknujsp.everyweather.core.ui.appbar

import androidx.compose.animation.core.AnimationSpec
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.SpringSpec
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material3.LocalContentColor
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.Stable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.layout.layoutId
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.unit.Velocity
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

private val topAppBarHorizontalPadding = 4.dp
private val topAppBarTitleInset = 16.dp

@Composable
fun CustomTopAppBar(
    modifier: Modifier = Modifier,
    windowInsets: WindowInsets,
    colors: CustomTopAppBarColors,
    scrollState: ScrollState,
    bigTitle: @Composable () -> Unit,
    smallTitle: @Composable () -> Unit,
    navigationIcon: @Composable (() -> Unit)? = null,
    actions: @Composable (RowScope.() -> Unit)? = null,
) {
    val coroutineScope = rememberCoroutineScope()
    var bigTitleHeight by remember { mutableIntStateOf(0) }
    val collapsedFraction by remember {
        derivedStateOf { if (scrollState.value < bigTitleHeight) scrollState.value / bigTitleHeight.toFloat() else 1f }
    }
    val nestedScrollConnection =
        remember {
            object : NestedScrollConnection {
                override suspend fun onPostFling(
                    consumed: Velocity,
                    available: Velocity,
                ): Velocity {
                    coroutineScope.onScroll(scrollState, bigTitleHeight, collapsedFraction)
                    return super.onPostFling(consumed, available)
                }
            }
        }

    if (scrollState.isScrollInProgress && scrollState.value != 0) {
        DisposableEffect(scrollState.isScrollInProgress) {
            onDispose {
                coroutineScope.onScroll(scrollState, bigTitleHeight, collapsedFraction)
            }
        }
    }

    val actionsRow: @Composable (() -> Unit)? =
        actions?.run {
            @Composable { Row(verticalAlignment = Alignment.CenterVertically, content = this) }
        }

    val bigTitleBox: @Composable () -> Unit = {
        Box(
            modifier =
                Modifier.onGloballyPositioned {
                    if (bigTitleHeight == 0) {
                        bigTitleHeight = it.size.height
                    }
                },
        ) {
            bigTitle()
        }
    }

    Box(modifier = modifier.nestedScroll(nestedScrollConnection)) {
        TopAppBarLayout(
            modifier =
                Modifier
                    .windowInsetsPadding(windowInsets),
            navigationIconContentColor = colors.navigationIconContentColor,
            actionIconContentColor = colors.actionIconContentColor,
            smallTitleAlpha = collapsedFraction,
            smallTitle = smallTitle,
            bigTitle = bigTitleBox,
            navigationIcon = navigationIcon,
            actions = actionsRow,
        )
    }
}

private const val BIG_TITLE = "bigTitle"
private const val SMALL_TITLE = "smallTitle"
private const val NAVIGATION_ICON = "navigationIcon"
private const val ACTION_ROW = "actionRow"

@Composable
private fun TopAppBarLayout(
    modifier: Modifier,
    navigationIconContentColor: Color,
    actionIconContentColor: Color,
    smallTitleAlpha: Float,
    bigTitle: @Composable () -> Unit,
    smallTitle: @Composable () -> Unit,
    navigationIcon: @Composable (() -> Unit)? = null,
    actions: @Composable (() -> Unit)? = null,
) {
    Layout({
        Box(
            Modifier
                .layoutId(NAVIGATION_ICON)
                .padding(start = topAppBarHorizontalPadding),
        ) {
            navigationIcon?.run {
                CompositionLocalProvider(LocalContentColor provides navigationIconContentColor, content = this)
            }
        }
        Box(
            Modifier
                .layoutId(BIG_TITLE)
                .padding(horizontal = topAppBarHorizontalPadding)
                .graphicsLayer(alpha = 1f - smallTitleAlpha),
        ) {
            bigTitle()
        }
        Box(
            Modifier
                .layoutId(SMALL_TITLE)
                .padding(horizontal = topAppBarHorizontalPadding)
                .graphicsLayer(alpha = smallTitleAlpha),
        ) {
            smallTitle()
        }
        Box(
            Modifier
                .layoutId(ACTION_ROW)
                .padding(end = topAppBarHorizontalPadding),
        ) {
            actions?.run {
                CompositionLocalProvider(LocalContentColor provides actionIconContentColor, content = this)
            }
        }
    }, modifier = modifier) { measurables, constraints ->
        val navigationIconPlaceable = measurables.first { it.layoutId == NAVIGATION_ICON }.measure(constraints.copy(minWidth = 0))
        val actionIconsPlaceable = measurables.first { it.layoutId == ACTION_ROW }.measure(constraints.copy(minWidth = 0))
        val bigTitlePlaceable = measurables.first { it.layoutId == BIG_TITLE }.measure(constraints.copy(minWidth = 0))
        val smallTitlePlaceable = measurables.first { it.layoutId == SMALL_TITLE }.measure(constraints.copy(minWidth = 0))

        val expandedRatio = 1f - (1f - smallTitleAlpha)
        val layoutHeight = navigationIconPlaceable.height + (bigTitlePlaceable.height * (1f - expandedRatio)).toInt()
        val titleInset = topAppBarTitleInset.roundToPx()

        layout(constraints.maxWidth, layoutHeight) {
            navigationIconPlaceable.place(x = 0, y = 0)
            actionIconsPlaceable.place(x = constraints.maxWidth - actionIconsPlaceable.width, y = 0)

            bigTitlePlaceable.place(
                x = (titleInset + navigationIconPlaceable.width * expandedRatio).toInt(),
                y = navigationIconPlaceable.height - (bigTitlePlaceable.height * expandedRatio).toInt(),
            )
            smallTitlePlaceable.place(x = navigationIconPlaceable.width + titleInset, y = (layoutHeight - smallTitlePlaceable.height) / 2)
        }
    }
}

private val animationSpec: AnimationSpec<Float> =
    SpringSpec(dampingRatio = Spring.DampingRatioLowBouncy, stiffness = Spring.StiffnessMediumLow)
private const val COLLAPSE_THRESHOLD = 0.5f

private fun CoroutineScope.onScroll(
    scrollState: ScrollState,
    shiftY: Int,
    collapsedFraction: Float,
) {
    if (scrollState.value < shiftY) {
        launch {
            scrollState.animateScrollTo(if (collapsedFraction < COLLAPSE_THRESHOLD) 0 else shiftY, animationSpec)
        }
    }
}

@Stable
class CustomTopAppBarColors(
    val navigationIconContentColor: Color = Color.White,
    val actionIconContentColor: Color = Color.White,
)
