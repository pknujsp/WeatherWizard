package io.github.pknujsp.everyweather.feature.favorite.search

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
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
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
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
import io.github.pknujsp.everyweather.core.resource.R
import io.github.pknujsp.everyweather.core.ui.TitleTextWithNavigation
import io.github.pknujsp.everyweather.feature.permoptimize.feature.FeatureStateScreen
import io.github.pknujsp.everyweather.feature.permoptimize.network.rememberNetworkStateManager

@Composable
fun SearchAreaScreen(
    navController: NavController,
    searchAreaViewModel: SearchAreaViewModel = hiltViewModel(),
) {
    val networkManager = rememberNetworkStateManager()
    var showSearchHistory by rememberSaveable { mutableStateOf(true) }
    val context = LocalContext.current

    Column(modifier = Modifier.fillMaxSize()) {
        TitleTextWithNavigation(title = stringResource(id = R.string.add_new_area)) {
            if (showSearchHistory || !networkManager.isEnabled(context)) {
                navController.popBackStack()
            } else {
                showSearchHistory = true
            }
        }

        if (networkManager.isEnabled(LocalContext.current)) {
            val searchResult by searchAreaViewModel.searchResult.collectAsStateWithLifecycle()
            var query by rememberSaveable { mutableStateOf("" to 0L) }

            SearchBar(Modifier.padding(horizontal = 16.dp), query, onChangeQuery = {
                if (it.isEmpty()) {
                    showSearchHistory = true
                }
            }) {
                showSearchHistory = false
                searchAreaViewModel.search(it)
            }

            if (showSearchHistory) {
                SearchHistoryScreen {
                    searchAreaViewModel.search(it)
                    query = it to System.currentTimeMillis()
                    showSearchHistory = false
                }
            } else {
                SearchResultScreen(searchResult) {
                    showSearchHistory = true
                }
            }
        } else {
            FeatureStateScreen(networkManager)
        }
    }
}

@Composable
fun SearchBar(
    modifier: Modifier,
    query: Pair<String, Long>,
    onChangeQuery: (String) -> Unit,
    onSendQuery: (String) -> Unit,
) {
    val currentOnSendQuery by rememberUpdatedState(newValue = onSendQuery)
    val currentOnChangeQuery by rememberUpdatedState(newValue = onChangeQuery)

    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(Color.Transparent, shape = RectangleShape)
            .padding(horizontal = 12.dp, vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        val context = LocalContext.current
        val messageIfEmpty = stringResource(id = R.string.search_area)
        val keyboardController = LocalSoftwareKeyboardController.current
        var text by rememberSaveable { mutableStateOf(query.first) }

        Icon(imageVector = Icons.Rounded.Search, contentDescription = stringResource(R.string.search), tint = Color.Black)
        TextField(
            value = text,
            label = { Text(text = stringResource(id = R.string.search_area)) },
            onValueChange = {
                text = it
                currentOnChangeQuery(it)
            },
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
            colors = TextFieldDefaults.colors(
                unfocusedContainerColor = Color.Transparent,
                focusedContainerColor = Color.Transparent,
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
                focusedLabelColor = Color.Gray,
            ),
            shape = RectangleShape,
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
            keyboardActions = KeyboardActions(onSearch = {
                if (text.isNotEmpty()) {
                    keyboardController?.hide()
                    currentOnSendQuery(text)
                } else {
                    Toast.makeText(context, messageIfEmpty, Toast.LENGTH_SHORT).show()
                }
            }),
            trailingIcon = {
                if (text.isNotEmpty()) {
                    IconButton(onClick = {
                        text = ""
                        currentOnChangeQuery("")
                    }) {
                        Icon(imageVector = Icons.Rounded.Clear, contentDescription = stringResource(id = R.string.clear_query))
                    }
                }
            },
        )
    }
}