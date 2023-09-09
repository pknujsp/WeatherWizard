package io.github.pknujsp.weatherwizard.core.common

import android.annotation.SuppressLint
import android.app.Activity
import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.Rect
import android.os.Handler
import android.os.Looper
import android.view.PixelCopy
import android.view.View
import android.view.Window
import androidx.core.graphics.get
import androidx.core.view.doOnPreDraw
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.cancel
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.plus
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import kotlin.coroutines.resume

@SuppressLint("InternalInsetResource", "DiscouragedApi")
class SystemBarColorMonitor(
    activity: Activity,
    private val systemBarController: SystemBarController,
    lifecycle: Lifecycle,
    resource: Resources = Resources.getSystem()
) {
    private val waitLock = Mutex()
    private var waiting: Job? = null
    private val coroutineScope = MainScope() + CoroutineName("SystemBarColorAnalyzer")
    private val onChangedFragmentFlow =
        MutableSharedFlow<Unit>(onBufferOverflow = BufferOverflow.SUSPEND, replay = 1, extraBufferCapacity = 6)

    private var _decorView: View? = activity.window.decorView
    private val decorView: View get() = _decorView!!
    private var _window: Window? = activity.window
    private val window: Window get() = _window!!

    private val statusBarHeight = resource.getDimensionPixelSize(resource.getIdentifier("status_bar_height", "dimen", "android"))
    private val navigationBarHeight = resource.getDimensionPixelSize(resource.getIdentifier("navigation_bar_height", "dimen", "android"))

    private val criteriaColor = 140
    private val avgRange = 20
    private val delayTime = 80L

    private val _statusBarColor = MutableSharedFlow<SystemBarStyler.SystemBarColor>(
        onBufferOverflow = BufferOverflow.DROP_OLDEST, replay = 1,
        extraBufferCapacity = 2,
    )

    private val _navigationBarColor = MutableSharedFlow<SystemBarStyler.SystemBarColor>(
        onBufferOverflow = BufferOverflow.DROP_OLDEST, replay = 1,
        extraBufferCapacity = 2,
    )

    init {
        lifecycle.addObserver(
            object : DefaultLifecycleObserver {
                override fun onStart(owner: LifecycleOwner) {
                    super.onStart(owner)
                    convert()
                }

                override fun onDestroy(owner: LifecycleOwner) {
                    super.onDestroy(owner)
                    coroutineScope.cancel()
                    _window = null
                    _decorView = null
                }
            },
        )

        coroutineScope.launch(Dispatchers.Default) {
            onChangedFragmentFlow.collect {
                val convertJob = launch(start = CoroutineStart.LAZY) {
                    val colors = startConvert()
                    withContext(Dispatchers.Main) {
                        systemBarController.setStyle(colors.first, colors.second)
                    }
                }

                decorView.doOnPreDraw {
                    convertJob.start()
                }
                convertJob.join()
            }
        }
    }

    private suspend fun startConvert(): Pair<SystemBarStyler.SystemBarColor, SystemBarStyler.SystemBarColor> {
        val statusBarBitmap = Bitmap.createBitmap(decorView.width, statusBarHeight, Bitmap.Config.ARGB_8888)
        val navigationBarBitmap = Bitmap.createBitmap(decorView.width, navigationBarHeight, Bitmap.Config.ARGB_8888)

        pixelCopy(Rect(0, 0, decorView.width, statusBarHeight), statusBarBitmap)
        pixelCopy(Rect(0, decorView.height - navigationBarHeight, decorView.width, decorView.height), navigationBarBitmap)

        val statusBarColor = statusBarBitmap.avg().toColor()
        val navigationBarColor = navigationBarBitmap.avg().toColor()

        statusBarBitmap.recycle()
        navigationBarBitmap.recycle()

        _statusBarColor.emit(statusBarColor)
        _navigationBarColor.emit(navigationBarColor)

        return statusBarColor to navigationBarColor
    }

    private fun Bitmap.avg(): Int {
        val startX = width / 2 - avgRange / 2
        val centerY = height / 2
        return (startX..<startX + avgRange).sumOf { this[it, centerY].toGrayScale() } / avgRange
    }


    private fun Int.toGrayScale(): Int = kotlin.run {
        val r = Color.red(this)
        val g = Color.green(this)
        val b = Color.blue(this)
        val a = Color.alpha(this)

        if (a == 0) -1
        else (0.2989 * r + 0.5870 * g + 0.1140 * b).toInt()
    }

    private fun Int.toColor() = toGrayScale().let { gray ->
        if (gray == 0 || gray == -1) SystemBarStyler.SystemBarColor.WHITE
        else if (gray <= criteriaColor) SystemBarStyler.SystemBarColor.WHITE
        else SystemBarStyler.SystemBarColor.BLACK
    }

    private suspend fun pixelCopy(rect: Rect, bitmap: Bitmap) = suspendCancellableCoroutine { cancellableContinuation ->
        PixelCopy.request(
            window, rect, bitmap,
            {
                cancellableContinuation.resume(it == PixelCopy.SUCCESS)
            },
            Handler(Looper.getMainLooper()),
        )
    }

    fun convert() {
        coroutineScope.launch {
            waitLock.withLock {
                if (waiting?.isActive == true) waiting?.cancel()
                waiting = launch(Dispatchers.Default) {
                    delay(delayTime)
                    onChangedFragmentFlow.emit(Unit)
                }
            }

        }
    }

}