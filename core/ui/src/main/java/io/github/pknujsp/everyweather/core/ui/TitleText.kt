package io.github.pknujsp.everyweather.core.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
private fun TitleText(title: String, modifier: Modifier = Modifier, textStyle: TextStyle) {
    Text(text = title,
        style = LocalTextStyle.current.merge(textStyle),
        fontWeight = FontWeight.Bold,
        color = Color.Black,
        modifier = modifier.wrapContentSize())
}

@Composable
fun TitleTextWithNavigation(
    title: String, modifier: Modifier = Modifier, onClickNavigation: () -> Unit
) {
    Row(modifier = modifier.padding(bottom = 12.dp, start = 16.dp),
        verticalAlignment = androidx.compose.ui.Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)) {
        IconButton(onClick = onClickNavigation) {
            Icon(Icons.AutoMirrored.Rounded.ArrowBack, contentDescription = stringResource(io.github.pknujsp.everyweather.core.resource.R.string.back))
        }
        TitleText(title = title, textStyle = TextStyle(fontSize = 21.sp))
    }
}

@Composable
fun TitleTextWithoutNavigation(
    title: String, modifier: Modifier = Modifier,
) {
    TitleText(title = title,
        modifier = modifier
            .padding(bottom = 12.dp, start = 16.dp)
            .then(modifier),
        textStyle = TextStyle(fontSize = 21.sp))
}

@Composable
fun MediumTitleTextWithoutNavigation(
    title: String, modifier: Modifier = Modifier,
) {
    TitleText(title = title,
        modifier = modifier
            .padding(bottom = 10.dp, start = 16.dp)
            .then(modifier),
        textStyle = TextStyle(fontSize = 19.sp))
}