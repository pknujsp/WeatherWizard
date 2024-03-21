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
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.TweenSpec
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheetProperties
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionContext
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
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
import io.github.pknujsp.everyweather.core.ui.dialog.BottomSheetLayoutParams.MAX_DIALOG_FREE_HEIGHT_RATIO
import io.github.pknujsp.everyweather.core.ui.dialog.BottomSheetLayoutParams.dialogContentHorizontalPadding
import io.github.pknujsp.everyweather.core.ui.dialog.BottomSheetLayoutParams.dialogHorizontalPadding
import io.github.pknujsp.everyweather.core.ui.theme.AppShapes
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.UUID
import kotlin.math.roundToInt

private const val ANIMATION_DURATION = 150L

object BottomSheetLayoutParams {
    private const val SCRIM_ALPHA = 0.32f * 255
    const val MAX_DIALOG_FREE_HEIGHT_RATIO = 0.8f

    val tonalElevation: Dp = 1.dp
    val scrimColor: Color = Color(0f, 0f, 0f, SCRIM_ALPHA)
    val containerColor: Color = Color.White
    val contentColor: Color = Color.White
    val dialogHorizontalPadding = 12.dp
    val dialogContentHorizontalPadding = 12.dp
    val dialogContentVerticalPadding = 12.dp
    val windowInsets = WindowInsets(0, 0, 0, 0)
    val shape = AppShapes.extraLarge
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PersistentBottomSheet(
    modifier: Modifier = Modifier.padding(horizontal = dialogContentHorizontalPadding,
        BottomSheetLayoutParams.dialogContentVerticalPadding), onDismissRequest: () -> Unit, content: @Composable () -> Unit
) {
    val density = LocalDensity.current
    val height = LocalView.current.height
    val maxHeightDp = (height * (MAX_DIALOG_FREE_HEIGHT_RATIO / density.density)).roundToInt().dp
    val bottomPadding = WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding()

    var animateIn by remember { mutableStateOf(false) }
    var visible by remember { mutableStateOf(true) }

    CustomModalBottomSheetPopup(
        onDismissRequest = {
            visible = false
        },
        windowInsets = BottomSheetLayoutParams.windowInsets,
    ) {
        BoxWithConstraints(Modifier.fillMaxSize()) {
            val fullHeight = constraints.maxHeight
            LaunchedEffect(visible) {
                animateIn = visible
                if (!animateIn) {
                    launch {
                        delay(ANIMATION_DURATION)
                        onDismissRequest()
                    }
                }
            }

            Scrim(color = BottomSheetLayoutParams.scrimColor, visible = animateIn, onDismissRequest = { visible = false })
            AnimatedVisibility(visible = animateIn,
                modifier = Modifier.align(Alignment.BottomCenter),
                enter = fadeIn(spring(stiffness = Spring.StiffnessMedium)) + slideInVertically(initialOffsetY = { it / 2 },
                    animationSpec = spring(stiffness = Spring.StiffnessMedium, dampingRatio = Spring.DampingRatioNoBouncy)),
                exit = slideOutVertically { it / 2 } + fadeOut()) {

                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(min = 0.dp, max = maxHeightDp)
                        .align(Alignment.BottomCenter)
                        .padding(horizontal = dialogHorizontalPadding)
                        .offset(y = (-bottomPadding.value).dp),
                    tonalElevation = BottomSheetLayoutParams.tonalElevation,
                    shape = AppShapes.extraLarge,
                    color = Color.White,
                ) {
                    Box(propagateMinConstraints = true, modifier = modifier) {
                        content()
                    }
                }
            }
        }
    }
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
                    .then(if (Build.VERSION.SDK_INT >= 33) Modifier.imePadding()
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

        setViewTreeLifecycleOwner(composeView.findViewTreeLifecycleOwner())
        setViewTreeViewModelStoreOwner(composeView.findViewTreeViewModelStoreOwner())
        setViewTreeSavedStateRegistryOwner(composeView.findViewTreeSavedStateRegistryOwner())
        setTag(androidx.compose.ui.R.id.compose_view_saveable_id_tag, "Popup:$saveId")
        clipChildren = false
    }

    private val windowManager = composeView.context.getSystemService(Context.WINDOW_SERVICE) as WindowManager

    private val displayWidth: Int
        get() = context.resources.displayMetrics.widthPixels

    private val params: WindowManager.LayoutParams = WindowManager.LayoutParams().apply {
        gravity = Gravity.BOTTOM or Gravity.START
        type = WindowManager.LayoutParams.TYPE_APPLICATION_PANEL
        width = displayWidth
        height = WindowManager.LayoutParams.MATCH_PARENT

        format = PixelFormat.TRANSLUCENT
        title = composeView.context.resources.getString(androidx.compose.ui.R.string.default_popup_window_title)
        token = composeView.applicationWindowToken

        flags = flags and (WindowManager.LayoutParams.FLAG_IGNORE_CHEEK_PRESSES or WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM).inv()
        flags = flags or WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS

        flags = if (properties.securePolicy.shouldApplySecureFlag(composeView.isFlagSecureEnabled())) {
            flags or WindowManager.LayoutParams.FLAG_SECURE
        } else {
            flags and (WindowManager.LayoutParams.FLAG_SECURE.inv())
        }

        flags = if (!properties.isFocusable) {
            flags or WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
        } else {
            flags and (WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE.inv())
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
    }

    override fun setLayoutDirection(layoutDirection: Int) {
    }

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

private fun View.isFlagSecureEnabled(): Boolean {
    val windowParams = rootView.layoutParams as? WindowManager.LayoutParams
    if (windowParams != null) {
        return (windowParams.flags and WindowManager.LayoutParams.FLAG_SECURE) != 0
    }
    return false
}

private fun SecureFlagPolicy.shouldApplySecureFlag(isSecureFlagSetOnParent: Boolean): Boolean {
    return when (this) {
        SecureFlagPolicy.SecureOff -> false
        SecureFlagPolicy.SecureOn -> true
        SecureFlagPolicy.Inherit -> isSecureFlagSetOnParent
    }
}