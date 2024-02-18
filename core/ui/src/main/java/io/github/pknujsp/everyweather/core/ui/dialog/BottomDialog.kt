package io.github.pknujsp.everyweather.core.ui.dialog

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.absoluteOffset
import androidx.compose.foundation.layout.absolutePadding
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.ModalBottomSheetDefaults
import androidx.compose.material3.ModalBottomSheetProperties
import androidx.compose.material3.SheetState
import androidx.compose.material3.Surface
import androidx.compose.material3.contentColorFor
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import io.github.pknujsp.everyweather.core.ui.theme.AppColorScheme
import io.github.pknujsp.everyweather.core.ui.theme.AppShapes
import kotlin.math.roundToInt


@Composable
@ExperimentalMaterial3Api
fun CustomModalBottomSheet(
    onDismissRequest: () -> Unit,
    modifier: Modifier = Modifier,
    sheetState: SheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
    sheetMaxWidth: Dp = BottomSheetDefaults.SheetMaxWidth,
    shape: Shape = AppShapes.extraLarge,
    containerColor: Color = BottomSheetDefaults.ContainerColor,
    contentColor: Color = contentColorFor(containerColor),
    tonalElevation: Dp = BottomSheetDefaults.Elevation,
    scrimColor: Color = BottomSheetDefaults.ScrimColor,
    dragHandle: @Composable (() -> Unit)? = { DragHandle() },
    windowInsets: WindowInsets = WindowInsets(0, 0, 0, 0),
    properties: ModalBottomSheetProperties = ModalBottomSheetDefaults.properties(),
    content: @Composable ColumnScope.() -> Unit,
) {
    val density = LocalDensity.current.density
    val height = LocalView.current.height
    val maxHeightDp = (height * 0.6 / density).roundToInt().dp
    val bottomPadding = WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding()

    ModalBottomSheet(onDismissRequest = onDismissRequest,
        modifier = modifier
            .heightIn(min = 0.dp, max = maxHeightDp)
            .absolutePadding(left = 12.dp, right = 12.dp)
            .absoluteOffset(y = (-10 - bottomPadding.value).dp),
        sheetState = sheetState,
        sheetMaxWidth = sheetMaxWidth,
        shape = shape,
        containerColor = containerColor,
        contentColor = contentColor,
        tonalElevation = tonalElevation,
        scrimColor = scrimColor,
        dragHandle = dragHandle,
        windowInsets = windowInsets,
        properties = properties,
        content = content)
}

private val DragHandleVerticalPadding = 20.dp


@Composable
private fun DragHandle(
    modifier: Modifier = Modifier,
    width: Dp = 24.dp,
    height: Dp = 3.dp,
    shape: Shape = AppShapes.extraLarge,
    color: Color = AppColorScheme.onSurfaceVariant.copy(alpha = 0.4f),
) {
    Surface(modifier = modifier.padding(vertical = DragHandleVerticalPadding), color = color, shape = shape) {
        Box(Modifier.size(width = width, height = height))
    }
}