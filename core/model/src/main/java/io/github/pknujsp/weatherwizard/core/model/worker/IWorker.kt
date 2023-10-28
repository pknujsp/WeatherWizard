package io.github.pknujsp.weatherwizard.core.model.worker

import io.github.pknujsp.weatherwizard.core.common.FeatureType
import java.util.UUID

interface IWorker {
    val name: String
    val requiredFeatures: Array<FeatureType>
}