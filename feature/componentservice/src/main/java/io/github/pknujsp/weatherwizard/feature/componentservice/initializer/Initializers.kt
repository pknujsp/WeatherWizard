package io.github.pknujsp.weatherwizard.feature.componentservice.initializer

import android.content.Context


abstract class AppComponentServiceIntializer(
    protected val context: Context,
) {
    abstract suspend fun initialize()
}