package io.github.pknujsp.weatherwizard.feature.weather

import androidx.compose.animation.core.AnimationSpec
import androidx.compose.animation.core.AnimationState
import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.animation.core.DecayAnimationSpec
import androidx.compose.animation.core.FastOutLinearInEasing
import androidx.compose.animation.core.animateDecay
import androidx.compose.animation.core.animateTo
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.gestures.rememberDraggableState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.Surface
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.material3.TopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.layout.AlignmentLine
import androidx.compose.ui.layout.LastBaseline
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.layout.layoutId
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.Velocity
import androidx.compose.ui.unit.dp
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min
import kotlin.math.roundToInt


private val topTitleAlphaEasing = CubicBezierEasing(.8f, 0f, .8f, .15f)
private val topAppBarHorizontalPadding = 4.dp
private val topAppBarTitleInset = 16.dp - topAppBarHorizontalPadding

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun CustomTopAppBar(
    modifier: Modifier = Modifier,
    bigTitle: @Composable (() -> Unit)? = null,
    smallTitle: @Composable () -> Unit,
    navigationIcon: @Composable (() -> Unit)? = null,
    actions: @Composable RowScope.() -> Unit,
    windowInsets: WindowInsets = TopAppBarDefaults.windowInsets,
    colors: CustomTopAppBarColors,
    maxHeight: Dp = 220.dp,
    pinnedHeight: Dp = 86.dp,
    scrollBehavior: TopAppBarScrollBehavior,
    density: Density = LocalDensity.current
) {
    val pinnedHeightPx: Float = remember { pinnedHeight.value * density.density }
    val maxHeightPx: Float = remember { maxHeight.value * density.density }

    SideEffect {
        if (scrollBehavior.state.heightOffsetLimit != pinnedHeightPx - maxHeightPx) {
            scrollBehavior.state.heightOffsetLimit = pinnedHeightPx - maxHeightPx
        }
    }

    val colorTransitionFraction = scrollBehavior.state.collapsedFraction
    val appBarContainerColor by rememberUpdatedState(colors.containerColor(colorTransitionFraction))

    val actionsRow = @Composable {
        Row(horizontalArrangement = Arrangement.Center, verticalAlignment = Alignment.CenterVertically, content = actions)
    }
    val topTitleAlpha = topTitleAlphaEasing.transform(colorTransitionFraction)
    val bottomTitleAlpha = 1f - colorTransitionFraction
    val hideTopRowSemantics = colorTransitionFraction < 0.5f
    val hideBottomRowSemantics = !hideTopRowSemantics

    val appBarDragModifier = if (!scrollBehavior.isPinned) {
        Modifier.draggable(orientation = Orientation.Vertical, state = rememberDraggableState { delta ->
            scrollBehavior.state.heightOffset = scrollBehavior.state.heightOffset + delta
        }, onDragStopped = { velocity ->
            settleAppBar(scrollBehavior.state, velocity, scrollBehavior.flingAnimationSpec, scrollBehavior.snapAnimationSpec)
        })
    } else {
        Modifier
    }

    Surface(modifier = modifier.then(appBarDragModifier), color = appBarContainerColor) {
        Box {
            var barHeight by remember { mutableIntStateOf(0) }
            BarHeight(modifier = Modifier.windowInsetsPadding(windowInsets),
                navigationIcon = navigationIcon,
                actions = actionsRow) { height ->
                barHeight = height
            }
            if (barHeight > 0) {
                TopAppBarLayout(
                    modifier = Modifier.windowInsetsPadding(windowInsets),
                    heightPx = pinnedHeightPx,
                    navigationIconContentColor = colors.navigationIconContentColor,
                    actionIconContentColor = colors.actionIconContentColor,
                    title = smallTitle,
                    titleAlpha = topTitleAlpha,
                    titleVerticalArrangement = Arrangement.Center,
                    titleHorizontalArrangement = Arrangement.Start,
                    titleBottomPadding = 0,
                    hideTitleSemantics = hideTopRowSemantics,
                    navigationIcon = navigationIcon,
                    actions = actionsRow,
                )
                TopAppBarLayout(modifier = Modifier.windowInsetsPadding(windowInsets),
                    heightPx = (maxHeightPx) + scrollBehavior.state.heightOffset,
                    navigationIconContentColor = colors.navigationIconContentColor,
                    actionIconContentColor = colors.actionIconContentColor,
                    title = bigTitle,
                    titleAlpha = bottomTitleAlpha,
                    titleVerticalArrangement = Arrangement.Center,
                    titleHorizontalArrangement = Arrangement.Start,
                    titleTopPadding = barHeight,
                    titleBottomPadding = 0,
                    hideTitleSemantics = hideBottomRowSemantics,
                    navigationIcon = {},
                    actions = {})
            }
        }
    }
}

@Composable
private fun TopAppBarLayout(
    modifier: Modifier,
    heightPx: Float,
    navigationIconContentColor: Color,
    actionIconContentColor: Color,
    title: @Composable (() -> Unit)? = null,
    titleAlpha: Float,
    titleVerticalArrangement: Arrangement.Vertical,
    titleHorizontalArrangement: Arrangement.Horizontal,
    titleBottomPadding: Int,
    titleTopPadding: Int = 0,
    hideTitleSemantics: Boolean,
    navigationIcon: @Composable (() -> Unit)? = null,
    actions: @Composable () -> Unit,
) {
    Layout({
        Box(Modifier
            .layoutId("navigationIcon")
            .padding(start = topAppBarHorizontalPadding)) {
            navigationIcon?.run {
                CompositionLocalProvider(LocalContentColor provides navigationIconContentColor, content = this)
            }
        }
        Box(Modifier
            .layoutId("title")
            .padding(horizontal = topAppBarHorizontalPadding)
            .then(if (hideTitleSemantics) Modifier.clearAndSetSemantics { } else Modifier)
            .graphicsLayer(alpha = titleAlpha)) {
            title?.run {
                invoke()
            }
        }
        Box(Modifier
            .layoutId("actionIcons")
            .padding(end = topAppBarHorizontalPadding)) {
            CompositionLocalProvider(LocalContentColor provides actionIconContentColor, content = actions)
        }
    }, modifier = modifier) { measurables, constraints ->
        val navigationIconPlaceable = measurables.first { it.layoutId == "navigationIcon" }.measure(constraints.copy(minWidth = 0))
        val actionIconsPlaceable = measurables.first { it.layoutId == "actionIcons" }.measure(constraints.copy(minWidth = 0))

        val maxTitleWidth = if (constraints.maxWidth == Constraints.Infinity) {
            constraints.maxWidth
        } else {
            (constraints.maxWidth - navigationIconPlaceable.width - actionIconsPlaceable.width).coerceAtLeast(0)
        }
        val titlePlaceable = measurables.first { it.layoutId == "title" }.measure(constraints.copy(minWidth = 0, maxWidth = maxTitleWidth))

        val titleBaseline = if (titlePlaceable[LastBaseline] != AlignmentLine.Unspecified) {
            titlePlaceable[LastBaseline]
        } else {
            0
        }

        val layoutHeight = heightPx.roundToInt()

        layout(constraints.maxWidth, layoutHeight) {
            navigationIconPlaceable.placeRelative(x = 0, y = (layoutHeight - navigationIconPlaceable.height) / 2)

            titlePlaceable.placeRelative(x = when (titleHorizontalArrangement) {
                Arrangement.Center -> (constraints.maxWidth - titlePlaceable.width) / 2
                Arrangement.End -> constraints.maxWidth - titlePlaceable.width - actionIconsPlaceable.width

                else -> max(topAppBarTitleInset.roundToPx(), navigationIconPlaceable.width)
            }, y = when (titleVerticalArrangement) {
                Arrangement.Center -> (layoutHeight - titlePlaceable.height) / 2
                Arrangement.Bottom -> if (titleBottomPadding == 0) layoutHeight - titlePlaceable.height
                else layoutHeight - titlePlaceable.height - max(0, titleBottomPadding - titlePlaceable.height + titleBaseline)

                else -> 0
            }.let {
                if (titleTopPadding > 0) {
                    titleTopPadding
                } else {
                    it
                }
            })

            actionIconsPlaceable.placeRelative(x = constraints.maxWidth - actionIconsPlaceable.width,
                y = (layoutHeight - actionIconsPlaceable.height) / 2)
        }
    }
}

@Composable
private fun BarHeight(
    modifier: Modifier,
    navigationIcon: @Composable (() -> Unit)? = null,
    actions: @Composable (() -> Unit)? = null,
    onHeightCalculated: (Int) -> Unit,
) {
    Layout({
        Box(Modifier
            .layoutId("navigationIcon")
            .padding(start = topAppBarHorizontalPadding)) {
            navigationIcon?.run {
                CompositionLocalProvider(content = navigationIcon)
            }
        }
        Box(Modifier
            .layoutId("actionIcons")
            .padding(end = topAppBarHorizontalPadding)) {
            actions?.run {
                CompositionLocalProvider(content = actions)
            }
        }
    }, modifier = modifier) { measurables, constraints ->
        val navigationIconPlaceable = measurables.first { it.layoutId == "navigationIcon" }.measure(constraints.copy(minWidth = 0))
        val actionIconsPlaceable = measurables.first { it.layoutId == "actionIcons" }.measure(constraints.copy(minWidth = 0))
        onHeightCalculated(max(navigationIconPlaceable.height, actionIconsPlaceable.height))

        layout(constraints.maxWidth, 0) {

        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
private suspend fun settleAppBar(
    state: TopAppBarState, velocity: Float, flingAnimationSpec: DecayAnimationSpec<Float>?, snapAnimationSpec: AnimationSpec<Float>?
): Velocity {
    if (state.collapsedFraction < 0.01f || state.collapsedFraction == 1f) {
        return Velocity.Zero
    }
    var remainingVelocity = velocity
    if (flingAnimationSpec != null && abs(velocity) > 1f) {
        var lastValue = 0f
        AnimationState(
            initialValue = 0f,
            initialVelocity = velocity,
        ).animateDecay(flingAnimationSpec) {
            val delta = value - lastValue
            val initialHeightOffset = state.heightOffset
            state.heightOffset = initialHeightOffset + delta
            val consumed = abs(initialHeightOffset - state.heightOffset)
            lastValue = value
            remainingVelocity = this.velocity
            if (abs(delta - consumed) > 0.5f) this.cancelAnimation()
        }
    }
    if (snapAnimationSpec != null) {
        if (state.heightOffset < 0 && state.heightOffset > state.heightOffsetLimit) {
            AnimationState(initialValue = state.heightOffset).animateTo(if (state.collapsedFraction < 0.5f) {
                0f
            } else {
                state.heightOffsetLimit
            }, animationSpec = snapAnimationSpec) { state.heightOffset = value }
        }
    }

    return Velocity(0f, remainingVelocity)
}


@ExperimentalMaterial3Api
@Stable
data class CustomTopAppBarColors(
    val containerColor: Color = Color.White,
    val scrolledContainerColor: Color = Color.White,
    val navigationIconContentColor: Color = Color.Black,
    val titleContentColor: Color = Color.Black,
    val actionIconContentColor: Color = Color.Black,
) {
    @Composable
    fun containerColor(colorTransitionFraction: Float): Color {
        return lerp(containerColor, scrolledContainerColor, FastOutLinearInEasing.transform(colorTransitionFraction))
    }

}