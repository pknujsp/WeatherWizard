package io.github.pknujsp.weatherwizard.core.common.manager

import io.github.pknujsp.weatherwizard.core.common.FeatureType
import java.util.UUID
import java.util.concurrent.atomic.AtomicBoolean

interface IWorker {
    val name: String
    val requiredFeatures: Array<FeatureType>
    val isRunning: AtomicBoolean
}