package io.github.pknujsp.everyweather.feature.permoptimize.feature

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import io.github.pknujsp.everyweather.core.common.FeatureIntent
import io.github.pknujsp.everyweather.core.common.FeatureType


private class MutableAppFeatureState(
    override val featureType: FeatureType,
) : AppFeatureState {
    override var isShowSettingsActivity: Boolean by mutableStateOf(false)
    override var isChanged: Int by mutableIntStateOf(0)

    override fun hideSettingsActivity() {
        isShowSettingsActivity = false
        isChanged++
    }

    override fun showSettingsActivity() {
        isShowSettingsActivity = true
    }

    override fun isAvailable(context: Context): Boolean = featureType.isAvailable(context)
}

@Composable
fun rememberAppFeatureState(featureType: FeatureType): AppFeatureState {
    val manager = remember(featureType) {
        MutableAppFeatureState(featureType)
    }
    return manager
}


@Stable
interface AppFeatureState {
    val featureType: FeatureType
    val isChanged: Int
    val isShowSettingsActivity: Boolean
    fun isAvailable(context: Context): Boolean
    fun showSettingsActivity()
    fun hideSettingsActivity()
}