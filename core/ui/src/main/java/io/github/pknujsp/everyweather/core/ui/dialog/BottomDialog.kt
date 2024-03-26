package io.github.pknujsp.everyweather.core.ui.dialog

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.absoluteOffset
import androidx.compose.foundation.layout.absolutePadding
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.ModalBottomSheetDefaults
import androidx.compose.material3.ModalBottomSheetProperties
import androidx.compose.material3.SheetState
import androidx.compose.material3.Surface
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import io.github.pknujsp.everyweather.core.ui.TitleTextWithoutNavigation
import io.github.pknujsp.everyweather.core.ui.dialog.BottomSheetLayoutParams.tonalElevation
import io.github.pknujsp.everyweather.core.ui.theme.AppColorScheme
import io.github.pknujsp.everyweather.core.ui.theme.AppShapes
import kotlin.math.roundToInt

enum class BottomSheetType {
    MODAL, PERSISTENT
}

@Composable
@ExperimentalMaterial3Api
fun CustomModalBottomSheet(
    onDismissRequest: () -> Unit,
    modifier: Modifier = Modifier.padding(horizontal = BottomSheetLayoutParams.dialogContentHorizontalPadding,
        BottomSheetLayoutParams.dialogContentVerticalPadding),
    maxHeightRatio: Float = BottomSheetLayoutParams.MAX_DIALOG_FREE_HEIGHT_RATIO,
    sheetState: SheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
    sheetMaxWidth: Dp = BottomSheetDefaults.SheetMaxWidth,
    dragHandle: @Composable() (() -> Unit)? = { DragHandle() },
    properties: ModalBottomSheetProperties = ModalBottomSheetDefaults.properties(),
    content: @Composable ColumnScope.() -> Unit,
) {
    val density = LocalDensity.current
    val height = LocalView.current.height
    val maxHeightDp = BottomSheetLayoutParams.height(height, density.density, maxHeightRatio)
    val bottomPadding = WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding()

    ModalBottomSheet(
        onDismissRequest = onDismissRequest,
        modifier = Modifier
            .heightIn(min = 0.dp, max = maxHeightDp)
            .padding(horizontal = BottomSheetLayoutParams.dialogHorizontalPadding)
            .offset(y = (-bottomPadding.value).dp),
        sheetState = sheetState,
        sheetMaxWidth = sheetMaxWidth,
        shape = BottomSheetLayoutParams.shape,
        containerColor = BottomSheetLayoutParams.containerColor,
        tonalElevation = tonalElevation,
        scrimColor = BottomSheetLayoutParams.scrimColor,
        dragHandle = dragHandle,
        windowInsets = BottomSheetLayoutParams.windowInsets,
        properties = properties,
        content = {
            Column(modifier = modifier) {
                content()
            }
        },
    )
}

private val DragHandleVerticalPadding = 12.dp

@Composable
private fun DragHandle(
    modifier: Modifier = Modifier,
    width: Dp = 26.dp,
    height: Dp = 3.dp,
    shape: Shape = AppShapes.extraLarge,
    color: Color = AppColorScheme.onSurfaceVariant.copy(alpha = 0.4f),
) {
    Surface(modifier = modifier.padding(vertical = DragHandleVerticalPadding), color = color, shape = shape) {
        Box(Modifier.size(width = width, height = height))
    }
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun BottomSheet(
    modifier: Modifier = Modifier.padding(horizontal = BottomSheetLayoutParams.dialogContentHorizontalPadding,
        BottomSheetLayoutParams.dialogContentVerticalPadding),
    bottomSheetType: BottomSheetType = BottomSheetType.MODAL,
    maxHeightRatio: Float = BottomSheetLayoutParams.MAX_DIALOG_FREE_HEIGHT_RATIO,
    onDismissRequest: () -> Unit,
    content: @Composable ColumnScope.() -> Unit,
) {
    when (bottomSheetType) {
        BottomSheetType.MODAL -> {
            CustomModalBottomSheet(onDismissRequest = onDismissRequest,
                modifier = modifier,
                content = content,
                maxHeightRatio = maxHeightRatio)
        }

        BottomSheetType.PERSISTENT -> {
            PersistentBottomSheet(onDismissRequest = onDismissRequest,
                modifier = modifier,
                content = content,
                maxHeightRatio = maxHeightRatio)
        }
    }
}

@Composable
fun ContentWithTitle(
    title: String,
    content: @Composable () -> Unit,
) {
    Column {
        TitleTextWithoutNavigation(title = title)
        content()
    }
}