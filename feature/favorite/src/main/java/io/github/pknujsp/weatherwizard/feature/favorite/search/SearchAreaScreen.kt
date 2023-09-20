package io.github.pknujsp.weatherwizard.feature.favorite.search

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import io.github.pknujsp.weatherwizard.core.ui.TitleTextWithNavigation
import io.github.pknujsp.weatherwizard.feature.map.R

@Composable
fun SearchAreaScreen(navController: NavController) {
    Column(modifier = Modifier
        .fillMaxSize()
        .statusBarsPadding()) {
        val searchAreaViewModel: SearchAreaViewModel = hiltViewModel()
        val searchResult by searchAreaViewModel.searchResult.collectAsStateWithLifecycle()
        var query by remember { mutableStateOf("") }
        val showSearchHistory by remember {
            derivedStateOf {
                mutableStateOf(true)
            }
        }

        TitleTextWithNavigation(title = stringResource(id = io.github.pknujsp.weatherwizard.feature.favorite.R.string.add_new_area)) {
            navController.popBackStack()
        }
        SearchBar(Modifier.padding(horizontal = 16.dp), query, onChangeQuery = {
            if (it.isEmpty())
                showSearchHistory.value = true
        }) {
            searchAreaViewModel.search(it)
            showSearchHistory.value = false
        }

        if (showSearchHistory.value) {
            SearchHistoryScreen {
                searchAreaViewModel.search(it)
                query = it
                showSearchHistory.value = false
            }
        } else {
            SearchResultScreen(navController, searchResult) {

            }
        }
    }
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun SearchBar(modifier: Modifier, query: String, onChangeQuery: (String) -> Unit, onSendQuery: (String) -> Unit) {
    Row(modifier = modifier
        .fillMaxWidth()
        .background(Color(0xFFD8D8D8), shape = RoundedCornerShape(30.dp))
        .padding(horizontal = 14.dp, vertical = 4.dp), verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)) {

        val context = LocalContext.current
        val messageIfEmpty = stringResource(id = R.string.search_area)
        val keyboardController = LocalSoftwareKeyboardController.current
        var text by remember { mutableStateOf("") }

        LaunchedEffect(query) {
            text = query
        }

        Icon(imageVector = Icons.Rounded.Search,
            contentDescription = stringResource(R.string.search), tint = Color.DarkGray)
        TextField(value = text, label = { Text(text = stringResource(id = R.string.search_area)) }, onValueChange = {
            text = it
            onChangeQuery(it)
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
                onSearch = {
                    if (text.isNotEmpty()) {
                        keyboardController?.hide()
                        onSendQuery(text)
                    } else {
                        Toast.makeText(context, messageIfEmpty, Toast.LENGTH_SHORT).show()
                    }
                }
            ),
            trailingIcon = {
                IconButton(onClick = {
                    text = ""
                    onChangeQuery("")
                }) {
                    Icon(imageVector = Icons.Rounded.Clear, contentDescription =
                    stringResource(id = R.string.clear_query))
                }
            }
        )
    }
}