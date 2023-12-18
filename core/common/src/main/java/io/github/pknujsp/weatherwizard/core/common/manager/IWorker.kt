package io.github.pknujsp.weatherwizard.core.common.manager

import android.content.Context
import android.os.Bundle
import androidx.lifecycle.LifecycleService
import io.github.pknujsp.weatherwizard.core.common.FeatureType
import java.util.UUID
import java.util.concurrent.atomic.AtomicBoolean

interface IWorker {
    val name: String
    val requiredFeatures: Array<FeatureType>
    val isRunning: AtomicBoolean
}

abstract class AppComponentService {
    abstract suspend fun start(context: Context, bundle: Bundle)
}