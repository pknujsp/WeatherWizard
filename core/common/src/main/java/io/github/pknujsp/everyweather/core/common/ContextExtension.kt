package io.github.pknujsp.everyweather.core.common

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper

fun Context.asActivity(): Activity? =
    when (this) {
        is Activity -> this
        is ContextWrapper -> baseContext.asActivity()
        else -> null
    }
