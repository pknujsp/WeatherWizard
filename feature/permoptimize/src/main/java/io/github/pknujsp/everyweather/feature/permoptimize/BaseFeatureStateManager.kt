package io.github.pknujsp.everyweather.feature.permoptimize

import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import io.github.pknujsp.everyweather.core.common.FeatureType

abstract class BaseFeatureStateManager {
    abstract val featureType: FeatureType

    var isChanged: Int by mutableIntStateOf(0)
        private set
    var isShowSettingsActivity: Boolean by mutableStateOf(false)
        private set

    fun hideSettingsActivity() {
        isShowSettingsActivity = false
        onChanged()
    }

    fun showSettingsActivity() {
        isShowSettingsActivity = true
        onChanged()
    }

    fun isEnabled(context: Context) = featureType.isEnabled(context)

    fun onChanged() {
        isChanged++
    }
}