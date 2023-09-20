package io.github.pknujsp.weatherwizard.core.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
private fun TitleText(title: String, modifier: Modifier = Modifier) {
    Text(text = title, fontSize = 24.sp, fontWeight = FontWeight.Bold, color = Color.Black,
        modifier = modifier
            .wrapContentSize())
}

@Composable
fun TitleTextWithNavigation(
    title: String, modifier: Modifier = Modifier,
    onClickNavigation: () -> Unit
) {
    Row(
        modifier = modifier
            .padding(top = 24.dp, bottom = 16.dp, start = 16.dp),
        verticalAlignment = androidx.compose.ui.Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        IconButton(onClick = onClickNavigation) {
            Icon(Icons.Rounded.ArrowBack, contentDescription = stringResource(io.github.pknujsp.weatherwizard.core.common.R.string.back))
        }
        TitleText(title = title)
    }
}

@Composable
fun TitleTextWithoutNavigation(
    title: String, modifier: Modifier = Modifier,
) {
    TitleText(title = title, modifier = modifier.padding(top = 24.dp, bottom = 16.dp, start = 16.dp))
}