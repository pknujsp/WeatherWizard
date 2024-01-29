package io.github.pknujsp.everyweather.core.ui.permission

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import io.github.pknujsp.everyweather.core.resource.R
import io.github.pknujsp.everyweather.core.ui.button.PrimaryButton
import io.github.pknujsp.everyweather.core.ui.button.SecondaryButton


@Composable
fun PermissionDialog(onDismissRequest: () -> Unit, onGrantPermission: () -> Unit) {
    Dialog(onDismissRequest = onDismissRequest,
        properties = DialogProperties(dismissOnBackPress = false, dismissOnClickOutside = false)) {
        Column(
            modifier = Modifier
                .background(color = Color.White, shape = RoundedCornerShape(12.dp))
                .padding(horizontal = 16.dp, vertical = 24.dp),
            horizontalAlignment = Alignment.Start,
        ) {
            Text(text = stringResource(id = io.github.pknujsp.everyweather.core.resource.R.string.title_why_you_need_permissions),
                style = MaterialTheme.typography
                    .titleLarge)
            Spacer(modifier = Modifier.height(16.dp))
            Text(text = stringResource(id = io.github.pknujsp.everyweather.core.resource.R.string.description_why_you_need_permissions),
                style = MaterialTheme.typography.bodyMedium)
            Spacer(modifier = Modifier.height(16.dp))
            Row(modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                SecondaryButton(text = stringResource(id = R.string.close), onClick = onDismissRequest)
                PrimaryButton(text = stringResource(id = R.string.grant_permissions),
                    onClick = onGrantPermission)
            }
        }
    }
}