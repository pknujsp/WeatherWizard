package io.github.pknujsp.everyweather.core.ui.dialog

import android.content.Context
import android.graphics.PixelFormat
import android.os.Build
import android.view.Gravity
import android.view.KeyEvent
import android.view.View
import android.view.ViewTreeObserver
import android.view.WindowManager
import android.window.OnBackInvokedCallback
import android.window.OnBackInvokedDispatcher
import androidx.annotation.DoNotInline
import androidx.annotation.RequiresApi
import androidx.compose.animation.core.TweenSpec
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.absoluteOffset
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
import androidx.compose.material3.ModalBottomSheetProperties
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionContext
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCompositionContext
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.AbstractComposeView
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.platform.ViewRootForInspector
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.popup
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.SecureFlagPolicy
import androidx.lifecycle.findViewTreeLifecycleOwner
import androidx.lifecycle.findViewTreeViewModelStoreOwner
import androidx.lifecycle.setViewTreeLifecycleOwner
import androidx.lifecycle.setViewTreeViewModelStoreOwner
import androidx.savedstate.findViewTreeSavedStateRegistryOwner
import androidx.savedstate.setViewTreeSavedStateRegistryOwner
import io.github.pknujsp.everyweather.core.ui.theme.AppShapes
import java.util.UUID
import kotlin.math.roundToInt

private const val DIALOG_PANEL_DESCRIPTION = "Dialog"
private const val MAX_DIALOG_FREE_HEIGHT_RATIO = 0.8f
private const val MIN_DIALOG_HEIGHT_RATIO = 0.6f
private val DialogHorizontalPadding = 12.dp
private val windowInsets = WindowInsets(0, 0, 0, 0)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomPersistentBottomSheet(
    tonalElevation: Dp = BottomSheetDefaults.Elevation,
    scrimColor: Color = BottomSheetDefaults.ScrimColor,
    freeHeight: Boolean = false,
    onDismissRequest: () -> Unit,
    content: @Composable () -> Unit
) {
    val density = LocalDensity.current.density
    val height = LocalView.current.height
    val maxHeightDp = (height * (MAX_DIALOG_FREE_HEIGHT_RATIO / density)).roundToInt().dp
    val bottomPadding = WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding()

    CustomModalBottomSheetPopup(
        onDismissRequest = {
            onDismissRequest()
        },
        windowInsets = windowInsets,
    ) {
        BoxWithConstraints(Modifier.fillMaxSize()) {
            val fullHeight = constraints.maxHeight
            Scrim(color = scrimColor, visible = true, onDismissRequest = onDismissRequest)
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 0.dp, max = maxHeightDp)
                    .align(Alignment.BottomCenter)
                    .padding(horizontal = DialogHorizontalPadding)
                    .absoluteOffset(y = (-10 - bottomPadding.value).dp),
                tonalElevation = tonalElevation,
                shape = AppShapes.extraLarge,
                color = Color.White,
            ) {
                Box(propagateMinConstraints = true, modifier = Modifier.padding(DialogHorizontalPadding, DialogHorizontalPadding)) {
                    content()
                }
            }
        }
    }

    /*
    val density = LocalDensity.current.density
    val height = LocalView.current.height
    val maxHeightDp = (height * ((if (freeHeight) 0.8 else 0.6) / density)).roundToInt().dp
    val bottomPadding = WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding()

    ModalBottomSheet(
        onDismissRequest = onDismissRequest,
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
        windowInsets = WindowInsets(0, 0, 0, 0),
        properties = properties,
        content = {
            Column(modifier = Modifier.padding(start = contentHorizontalPadding,
                end = contentHorizontalPadding,
                bottom = contentVerticalPadding), content = content)
        },
    )
    * */
}

@Composable
private fun Scrim(
    color: Color, visible: Boolean, onDismissRequest: () -> Unit
) {
    val alpha by animateFloatAsState(targetValue = if (visible) 1f else 0f, animationSpec = TweenSpec(), label = "")
    val dismissSheet = if (visible) {
        Modifier
            .pointerInput(onDismissRequest) {
                detectTapGestures {
                    onDismissRequest()
                }
            }
            .clearAndSetSemantics {}
    } else {
        Modifier
    }
    Canvas(Modifier
        .fillMaxSize()
        .then(dismissSheet)) {
        drawRect(color = color, alpha = alpha)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CustomModalBottomSheetPopup(
    properties: ModalBottomSheetProperties = ModalBottomSheetProperties(SecureFlagPolicy.Inherit,
        isFocusable = true,
        shouldDismissOnBackPress = true),
    onDismissRequest: () -> Unit,
    windowInsets: WindowInsets,
    content: @Composable () -> Unit,
) {
    val view = LocalView.current
    val id = rememberSaveable { UUID.randomUUID() }
    val parentComposition = rememberCompositionContext()
    val currentContent by rememberUpdatedState(content)
    val layoutDirection = LocalLayoutDirection.current
    val modalBottomSheetWindow = remember {
        CustomModalBottomSheetWindow(properties = properties, onDismissRequest = onDismissRequest, composeView = view, saveId = id).apply {
            setCustomContent(parent = parentComposition, content = {
                Box(Modifier
                    .semantics { this.popup() }
                    .windowInsetsPadding(windowInsets)
                    .then(
                        // TODO(b/290893168): Figure out a solution for APIs < 30.
                        if (Build.VERSION.SDK_INT >= 33) Modifier.imePadding()
                        else Modifier)) {
                    currentContent()
                }
            })
        }
    }

    DisposableEffect(modalBottomSheetWindow) {
        modalBottomSheetWindow.show()
        modalBottomSheetWindow.superSetLayoutDirection(layoutDirection)
        onDispose {
            modalBottomSheetWindow.disposeComposition()
            modalBottomSheetWindow.dismiss()
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
private class CustomModalBottomSheetWindow(
    private val properties: ModalBottomSheetProperties,
    private var onDismissRequest: () -> Unit,
    private val composeView: View,
    saveId: UUID
) : AbstractComposeView(composeView.context), ViewTreeObserver.OnGlobalLayoutListener, ViewRootForInspector {

    private var backCallback: Any? = null

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
        get() = context.resources.displayMetrics.widthPixels

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
        // TODO: Provide bottom sheet window resource
        title = composeView.context.resources.getString(androidx.compose.ui.R.string.default_popup_window_title)
        // Get the Window token from the parent view
        token = composeView.applicationWindowToken

        // Flags specific to modal bottom sheet.
        flags = flags and (WindowManager.LayoutParams.FLAG_IGNORE_CHEEK_PRESSES or WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM).inv()

        flags = flags or WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS

        // Security flag
        val secureFlagEnabled = properties.securePolicy.shouldApplySecureFlag(composeView.isFlagSecureEnabled())
        if (secureFlagEnabled) {
            flags = flags or WindowManager.LayoutParams.FLAG_SECURE
        } else {
            flags = flags and (WindowManager.LayoutParams.FLAG_SECURE.inv())
        }

        // Focusable
        if (!properties.isFocusable) {
            flags = flags or WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
        } else {
            flags = flags and (WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE.inv())
        }
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
        if (event.keyCode == KeyEvent.KEYCODE_BACK && properties.shouldDismissOnBackPress) {
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

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()

        maybeRegisterBackCallback()
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()

        maybeUnregisterBackCallback()
    }

    private fun maybeRegisterBackCallback() {
        if (!properties.shouldDismissOnBackPress || Build.VERSION.SDK_INT < 33) {
            return
        }
        if (backCallback == null) {
            backCallback = Api33Impl.createBackCallback(onDismissRequest)
        }
        Api33Impl.maybeRegisterBackCallback(this, backCallback)
    }

    private fun maybeUnregisterBackCallback() {
        if (Build.VERSION.SDK_INT >= 33) {
            Api33Impl.maybeUnregisterBackCallback(this, backCallback)
        }
        backCallback = null
    }

    override fun onGlobalLayout() {
        // No-op
    }

    override fun setLayoutDirection(layoutDirection: Int) {
        // Do nothing. ViewRootImpl will call this method attempting to set the layout direction
        // from the context's locale, but we have one already from the parent composition.
    }

    // Sets the "real" layout direction for our content that we obtain from the parent composition.
    fun superSetLayoutDirection(layoutDirection: LayoutDirection) {
        val direction = when (layoutDirection) {
            LayoutDirection.Ltr -> android.util.LayoutDirection.LTR
            LayoutDirection.Rtl -> android.util.LayoutDirection.RTL
        }
        super.setLayoutDirection(direction)
    }

    @RequiresApi(33)
    private object Api33Impl {
        @JvmStatic
        @DoNotInline
        fun createBackCallback(onDismissRequest: () -> Unit) = OnBackInvokedCallback(onDismissRequest)

        @JvmStatic
        @DoNotInline
        fun maybeRegisterBackCallback(view: View, backCallback: Any?) {
            if (backCallback is OnBackInvokedCallback) {
                view.findOnBackInvokedDispatcher()?.registerOnBackInvokedCallback(OnBackInvokedDispatcher.PRIORITY_OVERLAY, backCallback)
            }
        }

        @JvmStatic
        @DoNotInline
        fun maybeUnregisterBackCallback(view: View, backCallback: Any?) {
            if (backCallback is OnBackInvokedCallback) {
                view.findOnBackInvokedDispatcher()?.unregisterOnBackInvokedCallback(backCallback)
            }
        }
    }
}

// Taken from AndroidPopup.android.kt
private fun View.isFlagSecureEnabled(): Boolean {
    val windowParams = rootView.layoutParams as? WindowManager.LayoutParams
    if (windowParams != null) {
        return (windowParams.flags and WindowManager.LayoutParams.FLAG_SECURE) != 0
    }
    return false
}

// Taken from AndroidPopup.android.kt
private fun SecureFlagPolicy.shouldApplySecureFlag(isSecureFlagSetOnParent: Boolean): Boolean {
    return when (this) {
        SecureFlagPolicy.SecureOff -> false
        SecureFlagPolicy.SecureOn -> true
        SecureFlagPolicy.Inherit -> isSecureFlagSetOnParent
    }
}