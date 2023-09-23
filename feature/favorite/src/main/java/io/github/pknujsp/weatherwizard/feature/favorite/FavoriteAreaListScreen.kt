package io.github.pknujsp.weatherwizard.feature.favorite

import androidx.activity.ComponentActivity
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.LocationOn
import androidx.compose.material.icons.rounded.MoreVert
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import io.github.pknujsp.weatherwizard.core.model.favorite.FavoriteArea
import io.github.pknujsp.weatherwizard.core.model.favorite.TargetAreaType
import io.github.pknujsp.weatherwizard.core.model.onSuccess
import io.github.pknujsp.weatherwizard.core.ui.MainRoutes
import io.github.pknujsp.weatherwizard.core.ui.RootNavControllerViewModel
import io.github.pknujsp.weatherwizard.core.ui.RoundedButton
import io.github.pknujsp.weatherwizard.core.ui.TitleTextWithoutNavigation


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FavoriteAreaListScreen(navController: NavController) {
    val lazyListState = rememberLazyListState()
    val viewModel: FavoriteAreaViewModel = hiltViewModel()
    val favoriteAreaList by viewModel.favoriteAreaList.collectAsStateWithLifecycle()
    val targetArea by viewModel.targetArea.collectAsStateWithLifecycle()
    val rootNavControllerViewModel: RootNavControllerViewModel = hiltViewModel(viewModelStoreOwner =
    (LocalContext.current as ComponentActivity))

    Column(modifier = Modifier
        .fillMaxSize()) {
        TitleTextWithoutNavigation(title = stringResource(id = R.string.favorite_area_list))

        LazyColumn(modifier = Modifier.weight(1f), state = lazyListState) {
            favoriteAreaList.onSuccess {
                item {
                    CurrentLocationItem(checked = { targetArea.id }) {
                        viewModel.updateTargetArea(TargetAreaType.CurrentLocation)
                        rootNavControllerViewModel.navigate(MainRoutes.Weather)
                    }
                }
                items(it) { favoriteArea ->
                    AreaItem(favoriteArea, checked = { targetArea.id }) {
                        viewModel.updateTargetArea(TargetAreaType.CustomLocation(favoriteArea.id))
                        rootNavControllerViewModel.navigate(MainRoutes.Weather)
                    }
                }
            }
        }

        RoundedButton(text = stringResource(id = R.string.add_new_area),
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp)) {
            navController.navigate(FavoriteRoutes.AreaSearch.route)
        }
    }

}


@Composable
private fun AreaItem(favoriteArea: FavoriteArea, checked: () -> Long, onClick: () -> Unit) {
    Box(modifier = Modifier
        .fillMaxWidth()
        .wrapContentHeight()
        .padding(horizontal = 16.dp, vertical = 8.dp)) {
        Row(modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .shadow(elevation = 2.dp, shape = RoundedCornerShape(8.dp))
            .background(color = Color.White, shape = RoundedCornerShape(8.dp))
            .padding(horizontal = 8.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween) {
            IconButton(onClick = { /*TODO*/ }) {
                Icon(imageVector = Icons.Rounded.MoreVert, contentDescription = null)
            }
            Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.Center) {
                Text(text = favoriteArea.countryName, style = TextStyle(fontSize = 12.sp, color = Color.Gray))
                Text(text = favoriteArea.areaName, style = TextStyle(fontSize = 15.sp, color = Color.Black))
            }
            Checkbox(checked = checked() == favoriteArea.id, onCheckedChange = { checked ->
                if (checked) {
                    onClick()
                }
            })
        }
    }
}


@Composable
private fun CurrentLocationItem(checked: () -> Long, onClick: () -> Unit) {
    Box(modifier = Modifier
        .fillMaxWidth()
        .wrapContentHeight()
        .padding(horizontal = 16.dp, vertical = 8.dp)) {
        Row(modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .shadow(elevation = 2.dp, shape = RoundedCornerShape(8.dp))
            .background(color = Color.White, shape = RoundedCornerShape(8.dp))
            .padding(horizontal = 8.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween) {
            Icon(imageVector = Icons.Rounded.LocationOn,
                contentDescription = stringResource(id = io.github.pknujsp.weatherwizard.core.common.R.string.current_location),
                tint = Color.Blue,
                modifier = Modifier.size(16.dp))
            Text(text = stringResource(id = io.github.pknujsp.weatherwizard.core.common.R.string.current_location),
                style = TextStyle(fontSize = 16.sp, color = Color.Blue, textAlign = TextAlign.Left),
                modifier = Modifier.weight(1f))
            Checkbox(checked = checked() == TargetAreaType.CurrentLocation.id, onCheckedChange = {
                onClick()
            })
        }
    }
}