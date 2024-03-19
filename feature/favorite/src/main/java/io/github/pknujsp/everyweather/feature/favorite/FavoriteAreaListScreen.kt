package io.github.pknujsp.everyweather.feature.favorite

import androidx.activity.ComponentActivity
import androidx.activity.compose.LocalOnBackPressedDispatcherOwner
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Clear
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import io.github.pknujsp.everyweather.core.common.FeatureType
import io.github.pknujsp.everyweather.core.data.favorite.SelectedLocationModel
import io.github.pknujsp.everyweather.core.model.coordinate.LocationType
import io.github.pknujsp.everyweather.core.model.favorite.FavoriteArea
import io.github.pknujsp.everyweather.core.resource.R
import io.github.pknujsp.everyweather.core.ui.MainRoutes
import io.github.pknujsp.everyweather.core.ui.RootNavControllerViewModel
import io.github.pknujsp.everyweather.core.ui.TitleTextWithNavigation
import io.github.pknujsp.everyweather.core.ui.button.SecondaryButton
import io.github.pknujsp.everyweather.core.ui.dialog.CustomModalBottomSheet
import io.github.pknujsp.everyweather.core.ui.dialog.DialogScreen
import io.github.pknujsp.everyweather.core.ui.list.EmptyListScreen
import io.github.pknujsp.everyweather.core.ui.theme.AppShapes
import io.github.pknujsp.everyweather.feature.favorite.model.LoadCurrentLocationState
import io.github.pknujsp.everyweather.feature.favorite.model.LocationUiState
import io.github.pknujsp.everyweather.feature.permoptimize.feature.ShowAppSettingsActivity
import io.github.pknujsp.everyweather.feature.permoptimize.feature.SmallFeatureStateScreen

@Composable
fun FavoriteAreaListScreen(navController: NavController, viewModel: FavoriteAreaViewModel = hiltViewModel()) {
    val favoriteLocations by viewModel.favoriteLocations.collectAsStateWithLifecycle()
    val targetLocation = viewModel.locationUiState
    var showSettingsActivity by remember(targetLocation) { mutableStateOf(false) }
    var selectedLocationToDelete by remember { mutableStateOf<FavoriteArea?>(null) }

    val rootNavControllerViewModel: RootNavControllerViewModel =
        hiltViewModel(viewModelStoreOwner = (LocalContext.current as ComponentActivity))

    val onBackPressedDispatcherOwner = LocalOnBackPressedDispatcherOwner.current
    val backDispatcher = remember {
        onBackPressedDispatcherOwner?.onBackPressedDispatcher
    }

    LaunchedEffect(favoriteLocations) {
        if (favoriteLocations.isEmpty() && selectedLocationToDelete != null) {
            selectedLocationToDelete = null
        }
    }

    LaunchedEffect(targetLocation.isChanged) {
        if (targetLocation.isChanged) {
            rootNavControllerViewModel.navigate(MainRoutes.Weather)
        }
    }

    if (showSettingsActivity && targetLocation.loadCurrentLocationState is LoadCurrentLocationState.Failed && (targetLocation.loadCurrentLocationState as LoadCurrentLocationState.Failed).statefulFeature.hasRepairAction) {
        ShowAppSettingsActivity(featureType = (targetLocation.loadCurrentLocationState as LoadCurrentLocationState.Failed)
            .statefulFeature as FeatureType<*>) {
            showSettingsActivity = false
            viewModel.loadCurrentLocation()
        }
    }

    Column(modifier = Modifier.fillMaxSize()) {
        TitleTextWithNavigation(title = stringResource(id = R.string.nav_favorite_areas), onClickNavigation = {
            backDispatcher?.onBackPressed()
        })
        LazyColumn(modifier = Modifier.weight(1f),
            state = rememberLazyListState(),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 6.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp, Alignment.Top)) {
            item {
                CurrentLocationItem(targetLocation, onClick = {
                    viewModel.updateTargetLocation(SelectedLocationModel(LocationType.CurrentLocation))
                }, onClickAction = {
                    showSettingsActivity = true
                }, onClickRetry = {
                    viewModel.loadCurrentLocation()
                })
            }
            if (favoriteLocations.isEmpty()) {
                item {
                    EmptyListScreen(message = R.string.no_favorite_location)
                }
            } else {
                items(
                    items = favoriteLocations,
                    key = { item ->
                        item.id
                    },
                ) { item ->
                    FavoriteLocationItem(item, targetLocation.locationId, onClick = {
                        viewModel.updateTargetLocation(SelectedLocationModel(LocationType.CustomLocation, item.id))
                    }, onClickDelete = {
                        selectedLocationToDelete = item
                    })
                }
            }
        }

        Box(modifier = Modifier.padding(12.dp)) {
            SecondaryButton(text = stringResource(id = R.string.add_new_area), modifier = Modifier.fillMaxWidth()) {
                navController.navigate(FavoriteRoutes.AreaSearch.route)
            }
        }
    }

    val openDeleteDialog by remember {
        derivedStateOf {
            selectedLocationToDelete != null && favoriteLocations.any { it.id == selectedLocationToDelete?.id }
        }
    }
    if (openDeleteDialog) {
        DeleteDialog(address = selectedLocationToDelete!!.areaName,
            onDismissRequest = { selectedLocationToDelete = null },
            onConfirmation = {
                viewModel.deleteFavoriteLocation(selectedLocationToDelete!!.id)
                selectedLocationToDelete = null
            })
    }
}


@Composable
private fun FavoriteLocationItem(
    favoriteLocation: FavoriteArea, targetLocationId: Long?, onClick: () -> Unit, onClickDelete: (Long) -> Unit
) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .shadow(elevation = 6.dp, shape = AppShapes.medium)
            .background(color = Color.White, shape = AppShapes.medium),
    ) {
        Row(horizontalArrangement = Arrangement.Start, verticalAlignment = Alignment.CenterVertically) {
            IconButton(
                onClick = {
                    onClickDelete(favoriteLocation.id)
                },
                colors = IconButtonDefaults.iconButtonColors(containerColor = Color.Transparent, contentColor = Color.Gray),
            ) {
                Icon(imageVector = Icons.Rounded.Clear, contentDescription = null)
            }
            Row(modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp)
                .clickable {
                    if (targetLocationId == null || targetLocationId != favoriteLocation.id) {
                        onClick()
                    }
                }, verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.Start) {
                Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.Center) {
                    Text(text = favoriteLocation.countryName, style = TextStyle(fontSize = 12.sp, color = Color.Gray))
                    Text(text = favoriteLocation.areaName, style = TextStyle(fontSize = 15.sp, color = Color.Black))
                }
                Checkbox(checked = targetLocationId == favoriteLocation.id, onCheckedChange = {
                    if (targetLocationId == null || targetLocationId != favoriteLocation.id) {
                        onClick()
                    }
                })
            }
        }
    }
}


@Composable
private fun CurrentLocationItem(
    locationUiState: LocationUiState, onClickRetry: () -> Unit, onClickAction: () -> Unit, onClick: () -> Unit
) {
    Box(modifier = Modifier
        .clickable {
            if (locationUiState.locationType !is LocationType.CurrentLocation) {
                onClick()
            }
        }
        .shadow(elevation = 4.dp, shape = AppShapes.extraLarge)
        .background(color = Color.White, shape = AppShapes.extraLarge)
        .padding(horizontal = 16.dp, vertical = 8.dp)) {
        Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.Start) {
            Icon(
                painter = painterResource(id = R.drawable.ic_location),
                contentDescription = stringResource(id = R.string.current_location),
                modifier = Modifier.size(22.dp),
            )
            Column(modifier = Modifier
                .weight(1f)
                .padding(horizontal = 12.dp),
                horizontalAlignment = Alignment.Start,
                verticalArrangement = Arrangement.Center) {
                Text(
                    text = stringResource(id = R.string.current_location),
                    style = TextStyle(fontSize = 15.sp, color = Color.Gray),
                )

                if (locationUiState.isLoading) {
                    Text(
                        text = stringResource(id = R.string.finding_current_location),
                        style = TextStyle(fontSize = 15.sp, color = Color.Black),
                    )
                } else {

                    when (val state = locationUiState.loadCurrentLocationState) {
                        is LoadCurrentLocationState.Success -> {
                            Text(
                                text = state.addressName,
                                style = TextStyle(fontSize = 15.sp, color = Color.Black),
                            )
                        }

                        is LoadCurrentLocationState.Failed -> {
                            SmallFeatureStateScreen(state = state.statefulFeature, onClickRetry = {
                                onClickRetry()
                            }, onClickAction = {
                                onClickAction()
                            })
                        }

                        else -> {}
                    }
                }
            }
            Checkbox(checked = locationUiState.locationType is LocationType.CurrentLocation, onCheckedChange = {
                if (locationUiState.locationType !is LocationType.CurrentLocation) {
                    onClick()
                }
            })
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DeleteDialog(
    address: String, onDismissRequest: () -> Unit,
    onConfirmation: () -> Unit,
) {
    CustomModalBottomSheet(
        onDismissRequest = onDismissRequest,
    ) {
        val message = stringResource(id = R.string.delete_favorite_location_message).let {
            "$it\n\n$address"
        }
        DialogScreen(title = stringResource(id = R.string.delete_favorite_location_title),
            message = message,
            negative = stringResource(id = R.string.cancel),
            positive = stringResource(id = R.string.delete),
            onClickNegative = onDismissRequest,
            onClickPositive = onConfirmation)
    }
}