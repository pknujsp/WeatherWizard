package io.github.pknujsp.everyweather.feature.permoptimize.feature

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import io.github.pknujsp.everyweather.core.common.FeatureType


private class MutableAppFeatureState(
    override val featureType: FeatureType
) : AppFeatureState {
    override var isAvailable: Boolean by mutableStateOf(false)
    override var isShowSettingsActivity: Boolean by mutableStateOf(false)

    override fun hideSettingsActivity() {
        isShowSettingsActivity = false
    }

    override fun showSettingsActivity() {
        isShowSettingsActivity = true
    }
}

@Composable
fun rememberAppFeatureState(featureType: FeatureType, context: Context = LocalContext.current): AppFeatureState {
    val manager = remember(featureType) {
        MutableAppFeatureState(featureType)
    }

    LaunchedEffect(featureType, manager.isShowSettingsActivity) {
        if (!manager.isShowSettingsActivity) {
            manager.isAvailable = featureType.isAvailable(context)
        }
    }
    return manager
}


@Stable
interface AppFeatureState {
    val featureType: FeatureType
    val isAvailable: Boolean
    val isShowSettingsActivity: Boolean
    fun showSettingsActivity()
    fun hideSettingsActivity()
}