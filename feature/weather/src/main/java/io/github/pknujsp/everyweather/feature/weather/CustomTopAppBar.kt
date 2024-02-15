package io.github.pknujsp.everyweather.feature.weather

import androidx.compose.animation.core.AnimationSpec
import androidx.compose.animation.core.DecayAnimationSpec
import androidx.compose.animation.core.EaseInOut
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.rememberSplineBasedDecay
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.gestures.rememberDraggableState
import androidx.compose.foundation.gestures.scrollBy
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.Stable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.layout.layoutId
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch


private val topTitleAlphaEasing = EaseInOut
private val topAppBarHorizontalPadding = 4.dp
private val topAppBarTitleInset = 16.dp

@Composable
internal fun CustomTopAppBar(
    modifier: Modifier = Modifier,
    windowInsets: WindowInsets,
    colors: CustomTopAppBarColors,
    scrollState: ScrollState,
    nestedScrollConnection: NestedScrollConnection,
    snapAnimationSpec: AnimationSpec<Float> = spring(stiffness = Spring.StiffnessMediumLow),
    flingAnimationSpec: DecayAnimationSpec<Float> = rememberSplineBasedDecay(),
    bigTitle: @Composable () -> Unit,
    smallTitle: @Composable () -> Unit,
    navigationIcon: @Composable (() -> Unit)? = null,
    actions: @Composable (RowScope.() -> Unit)? = null,
) {
    val actionsRow: @Composable (() -> Unit)? = actions?.run {
        @Composable {
            Row(verticalAlignment = Alignment.CenterVertically, content = this)
        }
    }
    val coroutineScope = rememberCoroutineScope()
    var collapsedFraction by remember { mutableFloatStateOf(0f) }

    val appBarDragModifier = Modifier.draggable(orientation = Orientation.Vertical, state = rememberDraggableState { delta ->
        coroutineScope.launch {
            if (scrollState.isScrollInProgress.not()) {
                scrollState.scrollBy(-delta)
            }
        }
    }, onDragStopped = { velocity ->
        //settleAppBar(collapsedFraction, scrollState, velocity, flingAnimationSpec, snapAnimationSpec)
    })


    Surface(modifier = modifier.then(appBarDragModifier), color = Color.Transparent) {
        TopAppBarLayout(modifier = Modifier
            .windowInsetsPadding(windowInsets)
            .clipToBounds(),
            navigationIconContentColor = colors.navigationIconContentColor,
            actionIconContentColor = colors.actionIconContentColor,
            scrollState = scrollState,
            smallTitle = smallTitle,
            bigTitle = bigTitle,
            navigationIcon = navigationIcon,
            actions = actionsRow,
            collapsedFraction = {
                collapsedFraction = it
            })
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
    scrollState: ScrollState,
    collapsedFraction: (Float) -> Unit,
    bigTitle: @Composable (() -> Unit),
    smallTitle: @Composable (() -> Unit),
    navigationIcon: @Composable (() -> Unit)? = null,
    actions: @Composable (() -> Unit)? = null,
) {
    var bigTitleHeightPx by remember { mutableIntStateOf(0) }
    val smallTitleAlpha by remember {
        derivedStateOf {
            val collapsedRatio = if (scrollState.value < bigTitleHeightPx) scrollState.value / bigTitleHeightPx.toFloat() else 1f
            collapsedFraction(collapsedRatio)
            topTitleAlphaEasing.transform(collapsedRatio)
        }
    }

    Layout({
        Box(Modifier
            .layoutId(NAVIGATION_ICON)
            .padding(start = topAppBarHorizontalPadding)) {
            navigationIcon?.run {
                CompositionLocalProvider(LocalContentColor provides navigationIconContentColor, content = this)
            }
        }
        Box(Modifier
            .layoutId(BIG_TITLE)
            .padding(horizontal = topAppBarHorizontalPadding)
            .graphicsLayer(alpha = 1f - smallTitleAlpha)) {
            bigTitle()
        }
        Box(Modifier
            .layoutId(SMALL_TITLE)
            .padding(horizontal = topAppBarHorizontalPadding)
            .graphicsLayer(alpha = smallTitleAlpha)) {
            smallTitle()
        }
        Box(Modifier
            .layoutId(ACTION_ROW)
            .padding(end = topAppBarHorizontalPadding)) {
            actions?.run {
                CompositionLocalProvider(LocalContentColor provides actionIconContentColor, content = this)
            }
        }
    }, modifier = modifier) { measurables, constraints ->
        val navigationIconPlaceable = measurables.first { it.layoutId == NAVIGATION_ICON }.measure(constraints.copy(minWidth = 0))
        val actionIconsPlaceable = measurables.first { it.layoutId == ACTION_ROW }.measure(constraints.copy(minWidth = 0))
        val bigTitlePlaceable = measurables.first { it.layoutId == BIG_TITLE }.measure(constraints.copy(minWidth = 0))
        val smallTitlePlaceable = measurables.first { it.layoutId == SMALL_TITLE }.measure(constraints.copy(minWidth = 0))

        if (bigTitleHeightPx == 0) {
            bigTitleHeightPx = bigTitlePlaceable.height
        }

        val collapsedRatio = 1f - smallTitleAlpha
        val expandedRatio = 1f - collapsedRatio
        val layoutHeight = navigationIconPlaceable.height + (bigTitlePlaceable.height * collapsedRatio).toInt()
        val titleInset = topAppBarTitleInset.roundToPx()

        layout(constraints.maxWidth, layoutHeight) {
            navigationIconPlaceable.place(x = 0, y = 0)
            actionIconsPlaceable.place(x = constraints.maxWidth - actionIconsPlaceable.width, y = 0)

            bigTitlePlaceable.place(x = (titleInset + navigationIconPlaceable.width * expandedRatio).toInt(),
                y = navigationIconPlaceable.height - (bigTitlePlaceable.height * expandedRatio).toInt())
            smallTitlePlaceable.place(x = navigationIconPlaceable.width + titleInset, y = (layoutHeight - smallTitlePlaceable.height) / 2)
        }
    }
}

@Stable
data class CustomTopAppBarColors(
    val containerColor: Color = Color.White,
    val scrolledContainerColor: Color = Color.White,
    val navigationIconContentColor: Color = Color.Black,
    val titleContentColor: Color = Color.Black,
    val actionIconContentColor: Color = Color.Black,
)