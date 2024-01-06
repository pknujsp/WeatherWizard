package io.github.pknujsp.weatherwizard.feature.favorite.search

import android.widget.Toast
import androidx.activity.ComponentActivity
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
import androidx.compose.runtime.LaunchedEffect
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
import io.github.pknujsp.weatherwizard.core.common.FeatureType
import io.github.pknujsp.weatherwizard.core.resource.R
import io.github.pknujsp.weatherwizard.core.ui.MainRoutes
import io.github.pknujsp.weatherwizard.core.ui.RootNavControllerViewModel
import io.github.pknujsp.weatherwizard.core.ui.TitleTextWithNavigation
import io.github.pknujsp.weatherwizard.core.ui.feature.OpenAppSettingsActivity
import io.github.pknujsp.weatherwizard.core.ui.feature.UnavailableFeatureScreen
import io.github.pknujsp.weatherwizard.core.ui.feature.rememberAppNetworkState

@Composable
fun SearchAreaScreen(
    navController: NavController,
    searchAreaViewModel: SearchAreaViewModel = hiltViewModel(),
    rootNavControllerViewModel: RootNavControllerViewModel = hiltViewModel(viewModelStoreOwner = (LocalContext.current as ComponentActivity))
) {
    val uiAction by searchAreaViewModel.uiAction.collectAsStateWithLifecycle()
    LaunchedEffect(uiAction) {
        uiAction.onOnSelectedArea {
            rootNavControllerViewModel.navigate(MainRoutes.Weather)
        }
    }

    val networkUiState = rememberAppNetworkState(searchAreaViewModel.appNetworkManager)
    var showSearchHistory by remember { mutableStateOf(true) }

    Column(modifier = Modifier.fillMaxSize()) {
        TitleTextWithNavigation(title = stringResource(id = io.github.pknujsp.weatherwizard.core.resource.R.string.add_new_area)) {
            if (showSearchHistory || !networkUiState.isNetworkAvailable) {
                navController.popBackStack()
            } else {
                showSearchHistory = true
            }
        }

        if (networkUiState.isNetworkAvailable) {
            val searchResult by searchAreaViewModel.searchResult.collectAsStateWithLifecycle()
            var query by remember { mutableStateOf("" to 0L) }

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
        } else if (networkUiState.isOpenAppSettings) {
            OpenAppSettingsActivity(featureType = FeatureType.NETWORK) {
                networkUiState.isOpenAppSettings = false
            }
        } else {
            UnavailableFeatureScreen(featureType = FeatureType.NETWORK) {
                networkUiState.isOpenAppSettings = true
            }
        }
    }
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun SearchBar(modifier: Modifier, query: Pair<String, Long>, onChangeQuery: (String) -> Unit, onSendQuery: (String) -> Unit) {
    Row(modifier = modifier
        .fillMaxWidth()
        .background(Color.Transparent, shape = RectangleShape)
        .padding(horizontal = 12.dp, vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)) {

        val context = LocalContext.current
        val messageIfEmpty = stringResource(id = R.string.search_area)
        val keyboardController = LocalSoftwareKeyboardController.current
        var text by remember { mutableStateOf(query.first) }

        LaunchedEffect(query) {
            text = query.first
        }

        Icon(imageVector = Icons.Rounded.Search, contentDescription = stringResource(R.string.search), tint = Color.Black)
        TextField(value = text,
            label = { Text(text = stringResource(id = R.string.search_area)) },
            onValueChange = {
                text = it
                onChangeQuery(it)
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
                    onSendQuery(text)
                } else {
                    Toast.makeText(context, messageIfEmpty, Toast.LENGTH_SHORT).show()
                }
            }),
            trailingIcon = {
                if (text.isNotEmpty()) {
                    IconButton(onClick = {
                        text = ""
                        onChangeQuery("")
                    }) {
                        Icon(imageVector = Icons.Rounded.Clear, contentDescription = stringResource(id = R.string.clear_query))
                    }
                }
            })
    }
}