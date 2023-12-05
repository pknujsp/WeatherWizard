package io.github.pknujsp.weatherwizard.feature.favorite

import androidx.activity.ComponentActivity
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.LocationOn
import androidx.compose.material.icons.rounded.MoreVert
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
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
import io.github.pknujsp.weatherwizard.core.data.favorite.SelectedLocationModel
import io.github.pknujsp.weatherwizard.core.model.favorite.FavoriteArea
import io.github.pknujsp.weatherwizard.core.model.coordinate.LocationType
import io.github.pknujsp.weatherwizard.core.ui.MainRoutes
import io.github.pknujsp.weatherwizard.core.ui.RootNavControllerViewModel
import io.github.pknujsp.weatherwizard.core.ui.SecondaryButton
import io.github.pknujsp.weatherwizard.core.ui.list.EmptyListScreen
import io.github.pknujsp.weatherwizard.core.ui.theme.AppShapes
import kotlinx.coroutines.flow.filter


@Composable
fun FavoriteAreaListScreen(navController: NavController, viewModel: FavoriteAreaViewModel = hiltViewModel()) {
    val favoriteAreaList by viewModel.favoriteLocationList.collectAsStateWithLifecycle()
    val targetLocation by viewModel.targetLocation.collectAsStateWithLifecycle()
    val rootNavControllerViewModel: RootNavControllerViewModel =
        hiltViewModel(viewModelStoreOwner = (LocalContext.current as ComponentActivity))

    LaunchedEffect(Unit) {
        viewModel.onChanged.filter { it }.collect {
            rootNavControllerViewModel.navigate(MainRoutes.Weather)
        }
    }

    Column(modifier = Modifier.fillMaxSize()) {
        LazyColumn(modifier = Modifier.weight(1f), state = rememberLazyListState()) {
            item {
                if (targetLocation != null) {
                    CurrentLocationItem(targetLocation!!) {
                        viewModel.updateTargetLocation(SelectedLocationModel(LocationType.CurrentLocation))
                    }
                }
            }
            if (favoriteAreaList.isEmpty()) {
                item {
                    EmptyListScreen(message = R.string.no_favorite_location)
                }
            } else {
                if (targetLocation != null) {
                    items(favoriteAreaList) { favoriteLocation ->
                        FavoriteLocationItem(favoriteLocation, targetLocation!!, onClick = {
                            viewModel.updateTargetLocation(SelectedLocationModel(LocationType.CustomLocation, favoriteLocation.id))
                        }, onClickMore = {

                        })
                    }
                }
            }
        }

        Box(modifier = Modifier.padding(12.dp)) {
            SecondaryButton(text = stringResource(id = R.string.add_new_area), modifier = Modifier.fillMaxWidth()) {
                navController.navigate(FavoriteRoutes.AreaSearch.route)
            }
        }
    }

}


@Composable
private fun FavoriteLocationItem(
    favoriteLocation: FavoriteArea, currentLocationModel: SelectedLocationModel, onClick: () -> Unit, onClickMore: () -> Unit
) {
    val isCurrentLocation = remember {
        if (currentLocationModel.locationType is LocationType.CustomLocation) {
            currentLocationModel.locationId == favoriteLocation.id
        } else {
            false
        }
    }
    Box(modifier = Modifier
        .fillMaxWidth()
        .wrapContentHeight()
        .padding(horizontal = 16.dp, vertical = 8.dp)) {
        Row(modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .shadow(elevation = 4.dp, shape = AppShapes.large)
            .background(color = Color.White, shape = AppShapes.large)
            .padding(horizontal = 8.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween) {
            IconButton(onClick = onClickMore) {
                Icon(imageVector = Icons.Rounded.MoreVert, contentDescription = null)
            }
            Column(modifier = Modifier
                .weight(1f)
                .clickable {
                    if (!isCurrentLocation) {
                        onClick()
                    }
                }, verticalArrangement = Arrangement.Center) {
                Text(text = favoriteLocation.countryName, style = TextStyle(fontSize = 12.sp, color = Color.Gray))
                Text(text = favoriteLocation.areaName, style = TextStyle(fontSize = 15.sp, color = Color.Black))
            }
            Checkbox(checked = isCurrentLocation, onCheckedChange = {
                if (!isCurrentLocation) {
                    onClick()
                }
            })
        }
    }
}


@Composable
private fun CurrentLocationItem(currentLocationModel: SelectedLocationModel, onClick: () -> Unit) {
    Box(modifier = Modifier
        .fillMaxWidth()
        .wrapContentHeight()
        .padding(horizontal = 16.dp, vertical = 8.dp)) {
        Row(modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .clickable {
                if (currentLocationModel.locationType !is LocationType.CurrentLocation) {
                    onClick()
                }
            }
            .shadow(elevation = 4.dp, shape = AppShapes.large)
            .background(color = Color.White, shape = AppShapes.large)
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
            Checkbox(checked = currentLocationModel.locationType is LocationType.CurrentLocation, onCheckedChange = {
                if (currentLocationModel.locationType !is LocationType.CurrentLocation) {
                    onClick()
                }
            })
        }
    }
}