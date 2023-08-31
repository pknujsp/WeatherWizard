package io.github.pknujsp.weatherwizard.core.ui.lottie

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Button
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
import io.github.pknujsp.weatherwizard.core.ui.R

@Composable
private fun LoadingScreen(text: String? = null, onDismissRequest: (() -> Unit)? = null, content: @Composable (() -> Unit)? = null) {
    val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.animation_loading))
    val progress by animateLottieCompositionAsState(composition,
        iterations = LottieConstants.IterateForever,
        reverseOnRepeat = true,
        speed = 3f)

    Dialog(onDismissRequest = {
        onDismissRequest?.invoke()
    },
        properties = DialogProperties(dismissOnBackPress = false,
            dismissOnClickOutside = false,
            usePlatformDefaultWidth = false,
            decorFitsSystemWindows = false)) {
        Surface(modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight()) {
            Column(verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.background(color = Color.Black.copy(alpha = 0.4f))) {
                LottieAnimation(
                    composition,
                    progress = { progress },
                )
                text?.run {
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(text = this, fontSize = 16.sp, color = Color.White)
                }
                content?.run {
                    Spacer(modifier = Modifier.height(16.dp))
                    this()
                }
            }
        }
    }
}

@Composable
fun CancellableLoadingScreen(
    text: String? = null,
    onDismissRequest: () -> Unit,
) {
    LoadingScreen(text, onDismissRequest) {
        Button(onClick = { onDismissRequest() }) {
            Text(text = "취소")
        }
    }
}

@Composable
fun NonCancellableLoadingScreen(
    text: String? = null,
    onDismissRequest: () -> Unit,
) {
    LoadingScreen(text, onDismissRequest)
}