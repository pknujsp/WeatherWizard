package io.github.pknujsp.weatherwizard.core.ui.dialog

import android.content.Context
import android.graphics.PixelFormat
import android.view.Gravity
import android.view.KeyEvent
import android.view.View
import android.view.ViewTreeObserver
import android.view.WindowManager
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.material3.SheetValue.Expanded
import androidx.compose.material3.SheetValue.Hidden
import androidx.compose.material3.Surface
import androidx.compose.material3.contentColorFor
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionContext
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCompositionContext
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.AbstractComposeView
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.platform.ViewRootForInspector
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.popup
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.lifecycle.findViewTreeLifecycleOwner
import androidx.lifecycle.findViewTreeViewModelStoreOwner
import androidx.lifecycle.setViewTreeLifecycleOwner
import androidx.lifecycle.setViewTreeViewModelStoreOwner
import androidx.savedstate.findViewTreeSavedStateRegistryOwner
import androidx.savedstate.setViewTreeSavedStateRegistryOwner
import io.github.pknujsp.weatherwizard.core.common.asActivity
import io.github.pknujsp.weatherwizard.core.ui.theme.AppShapes
import kotlinx.coroutines.launch
import java.util.UUID
import kotlin.math.roundToInt


@Composable
@ExperimentalMaterial3Api
fun BottomSheet(
    modifier: Modifier = Modifier,
    onDismissRequest: () -> Unit,
    navigationBarHeight: Dp = WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding(),
    sheetState: SheetState = rememberModalBottomSheetState(),
    shape: Shape = AppShapes.large,
    containerColor: Color = BottomSheetDefaults.ContainerColor,
    contentColor: Color = contentColorFor(containerColor),
    tonalElevation: Dp = BottomSheetDefaults.Elevation,
    scrimColor: Color = BottomSheetDefaults.ScrimColor,
    windowInsets: WindowInsets = WindowInsets(0, 0, 0, 0),
    limitHeight: Boolean = true,
    content: @Composable ColumnScope.() -> Unit,
) {
    val scope = rememberCoroutineScope()
    val animateToDismiss: () -> Unit = {
        if (sheetState.currentValue == Hidden) {
            scope.launch { sheetState.hide() }.invokeOnCompletion {
                if (!sheetState.isVisible) {
                    onDismissRequest()
                }
            }
        }
    }

    ModalBottomSheetPopup(
        onDismissRequest = {
            if (sheetState.currentValue == Expanded && sheetState.hasPartiallyExpandedState) {
                scope.launch { sheetState.partialExpand() }
            } else {
                scope.launch { sheetState.hide() }.invokeOnCompletion { onDismissRequest() }
            }
        },
        windowInsets = windowInsets,
    ) {
        BoxWithConstraints(Modifier.fillMaxSize()) {
            val density = LocalDensity.current.density
            val ratio = if (limitHeight) 0.48 else 0.8
            val maxHeightDp = ((constraints.maxHeight / density) * ratio).roundToInt().dp

            Scrim(
                color = scrimColor,
                onDismissRequest = animateToDismiss,
            )
            Surface(
                modifier = modifier
                    .padding(
                        bottom = 10.dp + navigationBarHeight,
                        start = 12.dp,
                        end = 12.dp,
                    )
                    .heightIn(min = 0.dp, max = maxHeightDp)
                    .fillMaxWidth()
                    .align(Alignment.BottomCenter),
                shape = shape,
                color = containerColor,
                contentColor = contentColor,
                tonalElevation = tonalElevation,
            ) {
                Column(modifier
                    .fillMaxWidth()
                    .padding(8.dp)) {
                    content()
                }
            }
        }
    }

    LocalContext.current.asActivity()?.window?.let { window ->
        DisposableEffect(Unit) {
            val firstState: Boolean
            val windowInsetsController: WindowInsetsControllerCompat = WindowCompat.getInsetsController(window, window.decorView).apply {
                firstState = isAppearanceLightNavigationBars
                isAppearanceLightNavigationBars = false
            }
            onDispose {
                windowInsetsController.isAppearanceLightNavigationBars = firstState
            }
        }
    }

    if (sheetState.hasExpandedState) {
        LaunchedEffect(sheetState) {
            sheetState.show()
        }
    }
}

@Deprecated(message = "Use ModalBottomSheet overload with windowInset parameter.", level = DeprecationLevel.HIDDEN)
@Composable
@ExperimentalMaterial3Api
fun BottomSheet(
    onDismissRequest: () -> Unit,
    modifier: Modifier = Modifier,
    sheetState: SheetState = rememberModalBottomSheetState(),
    shape: Shape = BottomSheetDefaults.ExpandedShape,
    containerColor: Color = BottomSheetDefaults.ContainerColor,
    contentColor: Color = contentColorFor(containerColor),
    tonalElevation: Dp = BottomSheetDefaults.Elevation,
    scrimColor: Color = BottomSheetDefaults.ScrimColor,
    dragHandle: @Composable (() -> Unit)? = { BottomSheetDefaults.DragHandle() },
    content: @Composable ColumnScope.() -> Unit,
) = ModalBottomSheet(
    onDismissRequest = onDismissRequest,
    modifier = modifier,
    sheetState = sheetState,
    shape = shape,
    containerColor = containerColor,
    contentColor = contentColor,
    tonalElevation = tonalElevation,
    scrimColor = scrimColor,
    dragHandle = dragHandle,
    content = content,
)


@Composable
private fun Scrim(
    color: Color,
    onDismissRequest: () -> Unit,
) {
    val dismissSheet = Modifier
        .pointerInput(onDismissRequest) {
            detectTapGestures {
                onDismissRequest()
            }
        }
        .clearAndSetSemantics {}
    Canvas(Modifier
        .fillMaxSize()
        .then(dismissSheet)) {
        drawRect(color = color, alpha = 1f)
    }
}

/**
 * Popup specific for modal bottom sheet.
 */
@Composable
internal fun ModalBottomSheetPopup(
    onDismissRequest: () -> Unit,
    windowInsets: WindowInsets,
    content: @Composable () -> Unit,
) {
    val view = LocalView.current
    val id = rememberSaveable { UUID.randomUUID() }
    val parentComposition = rememberCompositionContext()
    val currentContent by rememberUpdatedState(content)
    val modalBottomSheetWindow = remember {
        ModalBottomSheetWindow(onDismissRequest = onDismissRequest, composeView = view, saveId = id).apply {
            setCustomContent(parent = parentComposition, content = {
                Box(Modifier
                    .semantics { this.popup() }
                    .windowInsetsPadding(windowInsets)
                    .imePadding()) {
                    currentContent()
                }
            })
        }
    }

    DisposableEffect(modalBottomSheetWindow) {
        modalBottomSheetWindow.show()
        onDispose {
            modalBottomSheetWindow.disposeComposition()
            modalBottomSheetWindow.dismiss()
        }
    }
}

/** Custom compose view for [BottomSheet] */
private class ModalBottomSheetWindow(
    private var onDismissRequest: () -> Unit,
    private val composeView: View,
    saveId: UUID,
) : AbstractComposeView(composeView.context), ViewTreeObserver.OnGlobalLayoutListener, ViewRootForInspector {
    init {
        id = android.R.id.content
        // Set up view owners
        setViewTreeLifecycleOwner(composeView.findViewTreeLifecycleOwner())
        setViewTreeViewModelStoreOwner(composeView.findViewTreeViewModelStoreOwner())
        setViewTreeSavedStateRegistryOwner(composeView.findViewTreeSavedStateRegistryOwner())
        setTag(androidx.compose.ui.R.id.compose_view_saveable_id_tag, "Popup:$saveId")
        // Enable children to draw their shadow by not clipping them
        clipChildren = false
    }

    private val windowManager = composeView.context.getSystemService(Context.WINDOW_SERVICE) as WindowManager

    private val displayWidth: Int
        get() {
            val density = context.resources.displayMetrics.density
            return (context.resources.configuration.screenWidthDp * density).roundToInt()
        }

    private val params: WindowManager.LayoutParams = WindowManager.LayoutParams().apply {
        // Position bottom sheet from the bottom of the screen
        gravity = Gravity.BOTTOM or Gravity.START
        // Application panel window
        type = WindowManager.LayoutParams.TYPE_APPLICATION_PANEL
        // Fill up the entire app view
        width = displayWidth
        height = WindowManager.LayoutParams.MATCH_PARENT

        // Format of screen pixels
        format = PixelFormat.TRANSLUCENT
        // Title used as fallback for a11y services
        title = "Popup-Window"
        // Get the Window token from the parent view
        token = composeView.applicationWindowToken

        // Flags specific to modal bottom sheet.
        flags = flags and (WindowManager.LayoutParams.FLAG_IGNORE_CHEEK_PRESSES or WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM).inv()

        flags = flags or WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
    }

    private var content: @Composable () -> Unit by mutableStateOf({})

    override var shouldCreateCompositionOnAttachedToWindow: Boolean = false
        private set

    @Composable
    override fun Content() {
        content()
    }

    fun setCustomContent(
        parent: CompositionContext? = null, content: @Composable () -> Unit
    ) {
        parent?.let { setParentCompositionContext(it) }
        this.content = content
        shouldCreateCompositionOnAttachedToWindow = true
    }

    fun show() {
        windowManager.addView(this, params)
    }

    fun dismiss() {
        setViewTreeLifecycleOwner(null)
        setViewTreeSavedStateRegistryOwner(null)
        composeView.viewTreeObserver.removeOnGlobalLayoutListener(this)
        windowManager.removeViewImmediate(this)
    }

    /**
     * Taken from PopupWindow. Calls [onDismissRequest] when back button is pressed.
     */
    override fun dispatchKeyEvent(event: KeyEvent): Boolean {
        if (event.keyCode == KeyEvent.KEYCODE_BACK) {
            if (keyDispatcherState == null) {
                return super.dispatchKeyEvent(event)
            }
            if (event.action == KeyEvent.ACTION_DOWN && event.repeatCount == 0) {
                val state = keyDispatcherState
                state?.startTracking(event, this)
                return true
            } else if (event.action == KeyEvent.ACTION_UP) {
                val state = keyDispatcherState
                if (state != null && state.isTracking(event) && !event.isCanceled) {
                    onDismissRequest()
                    return true
                }
            }
        }
        return super.dispatchKeyEvent(event)
    }

    override fun onGlobalLayout() {
        // No-op
    }
}