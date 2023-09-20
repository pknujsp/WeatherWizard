package io.github.pknujsp.weatherwizard.feature.favorite.search

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Clear
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import io.github.pknujsp.weatherwizard.core.ui.TitleTextWithNavigation
import io.github.pknujsp.weatherwizard.feature.map.R

@Composable
fun SearchAreaScreen(navController: NavController) {
    Column(modifier = Modifier
        .fillMaxSize()
        .statusBarsPadding()) {
        var text by rememberSaveable { mutableStateOf("") }
        TitleTextWithNavigation(title = stringResource(id = io.github.pknujsp.weatherwizard.feature.favorite.R.string.add_new_area)) {
            navController.popBackStack()
        }
        SearchBar(Modifier.padding(horizontal = 16.dp)) {
            text = it
        }
        LazyColumn {

        }
    }
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun SearchBar(modifier: Modifier, onTextChange: (String) -> Unit) {
    Row(modifier = modifier
        .fillMaxWidth()
        .background(Color(0xFFD8D8D8), shape = RoundedCornerShape(30.dp))
        .padding(horizontal = 14.dp, vertical = 4.dp), verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)) {

        val keyboardController = LocalSoftwareKeyboardController.current
        var text by rememberSaveable { mutableStateOf("") }

        Icon(imageVector = Icons.Rounded.Search,
            contentDescription = stringResource(R.string.search), tint = Color.DarkGray)
        TextField(value = text, label = { Text(text = stringResource(id = R.string.search_area)) }, onValueChange = {
            text = it
            onTextChange(it)
        },
            singleLine = true,
            modifier = Modifier
                .fillMaxWidth(),
            colors = TextFieldDefaults.colors(
                unfocusedContainerColor = Color.Transparent,
                focusedContainerColor = Color.Transparent,
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
                focusedLabelColor = Color.Blue,
            ),
            shape = RectangleShape,
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
            keyboardActions = KeyboardActions(
                onDone = {
                    keyboardController?.hide()
                }
            ),
            trailingIcon = {
                IconButton(onClick = { text = "" }) {
                    Icon(imageVector = Icons.Rounded.Clear, contentDescription =
                    stringResource(id = R.string.clear_query))
                }
            }
        )
    }
}