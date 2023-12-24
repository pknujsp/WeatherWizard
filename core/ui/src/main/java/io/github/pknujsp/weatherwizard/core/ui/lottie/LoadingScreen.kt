package io.github.pknujsp.weatherwizard.core.ui.lottie

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.popup
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsControllerCompat
import io.github.pknujsp.weatherwizard.core.ui.dialog.DialogScreen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun LoadingScreen(text: String? = null, onDismissRequest: (() -> Unit)? = null, content: @Composable (() -> Unit)? = null) {
    /** lottie
    val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.animation_lma54law))
    val progress by animateLottieCompositionAsState(composition,
    iterations = LottieConstants.IterateForever,
    reverseOnRepeat = true,
    speed = 1.1f)
     **/

    AlertDialog(onDismissRequest = {
        onDismissRequest?.invoke()
    },
        properties = DialogProperties(dismissOnBackPress = false,
            dismissOnClickOutside = false,
            usePlatformDefaultWidth = false,
            decorFitsSystemWindows = false)) {
        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            CircularProgressIndicator(
                color = Color.Black,
                trackColor = Color(0xFFD9D9D9),
            )
            text?.run {
                Text(text = this,
                    fontSize = 16.sp,
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(top = 16.dp))
            }
            content?.run {
                Spacer(modifier = Modifier.height(16.dp))
                this()
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
        TextButton(
            onClick = { onDismissRequest() },
            colors = ButtonDefaults.textButtonColors(contentColor = Color.White, disabledContentColor = Color.White),
        ) {
            Text(text = stringResource(id = io.github.pknujsp.weatherwizard.core.resource.R.string.cancel))
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

fun Context.asActivity(): Activity? = when (this) {
    is Activity -> this
    is ContextWrapper -> baseContext.asActivity()
    else -> null
}