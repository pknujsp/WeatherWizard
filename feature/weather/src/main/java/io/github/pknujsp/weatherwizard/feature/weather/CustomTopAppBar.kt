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
import androidx.compose.runtime.Stable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.layout.layoutId
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.Velocity
import androidx.compose.ui.unit.dp
import kotlin.math.abs
import kotlin.math.max


private val topTitleAlphaEasing = CubicBezierEasing(.8f, 0f, .8f, .15f)
private val topAppBarHorizontalPadding = 4.dp
private val topAppBarTitleInset = 16.dp - topAppBarHorizontalPadding

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun CustomTopAppBar(
    modifier: Modifier = Modifier,
    bigTitle: @Composable (() -> Unit),
    smallTitle: @Composable () -> Unit,
    navigationIcon: @Composable (() -> Unit)? = null,
    actions: @Composable RowScope.() -> Unit,
    windowInsets: WindowInsets = TopAppBarDefaults.windowInsets,
    colors: CustomTopAppBarColors,
    scrollBehavior: TopAppBarScrollBehavior,
) {
    val actionsRow = @Composable {
        Row(horizontalArrangement = Arrangement.Center, verticalAlignment = Alignment.CenterVertically, content = actions)
    }

    val appBarDragModifier = if (!scrollBehavior.isPinned) {
        Modifier.draggable(orientation = Orientation.Vertical, state = rememberDraggableState { delta ->
            scrollBehavior.state.heightOffset += delta
        }, onDragStopped = { velocity ->
            settleAppBar(scrollBehavior.state, velocity, scrollBehavior.flingAnimationSpec, scrollBehavior.snapAnimationSpec)
        })
    } else {
        Modifier
    }

    Surface(modifier = modifier.then(appBarDragModifier), color = Color.Transparent) {
        Box {
            val colorTransitionFraction = scrollBehavior.state.collapsedFraction
            val smallTitleAlpha = topTitleAlphaEasing.transform(colorTransitionFraction)
            val bigTitleAlpha = 1f - colorTransitionFraction

            TopAppBarLayout(
                modifier = Modifier.windowInsetsPadding(windowInsets),
                navigationIconContentColor = colors.navigationIconContentColor,
                actionIconContentColor = colors.actionIconContentColor,
                smallTitle = smallTitle,
                smallTitleAlpha = smallTitleAlpha,
                bigTitle = bigTitle,
                bigTitleAlpha = bigTitleAlpha,
                navigationIcon = navigationIcon,
                actions = actionsRow,
            )
        }
    }
}

private const val BIG_TITLE = "bigTitle"
private const val SMALL_TITLE = "smallTitle"
private const val NAVIGATION_ICON = "navigationIcon"
private const val ACTION_ICONS = "actionIcons"

@Composable
private fun TopAppBarLayout(
    modifier: Modifier,
    navigationIconContentColor: Color,
    actionIconContentColor: Color,
    bigTitle: @Composable (() -> Unit),
    bigTitleAlpha: Float,
    smallTitle: @Composable (() -> Unit),
    smallTitleAlpha: Float,
    navigationIcon: @Composable (() -> Unit)? = null,
    actions: @Composable () -> Unit,
) {
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
            .graphicsLayer(alpha = bigTitleAlpha)) {
            bigTitle()
        }
        Box(Modifier
            .layoutId(SMALL_TITLE)
            .padding(horizontal = topAppBarHorizontalPadding)
            .graphicsLayer(alpha = smallTitleAlpha)) {
            smallTitle()
        }
        Box(Modifier
            .layoutId(ACTION_ICONS)
            .padding(end = topAppBarHorizontalPadding)) {
            CompositionLocalProvider(LocalContentColor provides actionIconContentColor, content = actions)
        }
    }, modifier = modifier) { measurables, constraints ->
        val navigationIconPlaceable = measurables.first { it.layoutId == NAVIGATION_ICON }.measure(constraints.copy(minWidth = 0))
        val actionIconsPlaceable = measurables.first { it.layoutId == ACTION_ICONS }.measure(constraints.copy(minWidth = 0))
        val bigTitlePlaceable = measurables.first { it.layoutId == BIG_TITLE }.measure(constraints.copy(minWidth = 0))
        val smallTitlePlaceable = measurables.first { it.layoutId == SMALL_TITLE }.measure(constraints.copy(minWidth = 0))

        val layoutHeight = navigationIconPlaceable.height + (bigTitlePlaceable.height * bigTitleAlpha).toInt()

        layout(constraints.maxWidth, layoutHeight) {
            navigationIconPlaceable.place(x = 0, y = 0)

            bigTitlePlaceable.place(x = topAppBarTitleInset.roundToPx(), y = navigationIconPlaceable.height)
            smallTitlePlaceable.place(x = navigationIconPlaceable.width + topAppBarTitleInset.roundToPx(),
                y = (layoutHeight - smallTitlePlaceable.height) / 2)
            actionIconsPlaceable.place(x = constraints.maxWidth - actionIconsPlaceable.width, y = 0)
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