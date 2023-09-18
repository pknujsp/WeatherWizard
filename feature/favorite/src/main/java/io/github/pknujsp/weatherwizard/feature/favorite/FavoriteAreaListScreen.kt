package io.github.pknujsp.weatherwizard.feature.favorite

import android.app.Activity
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.rounded.LocationOn
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FabPosition
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MediumTopAppBar
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.view.WindowCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import io.github.pknujsp.weatherwizard.core.model.favorite.FavoriteArea
import io.github.pknujsp.weatherwizard.core.model.favorite.TargetAreaType
import io.github.pknujsp.weatherwizard.core.model.onSuccess

private val fabColor = Color(0xFF296DF6)


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FavoriteAreaListScreen() {
    val view = LocalView.current
    LaunchedEffect(Unit) {
        (view.context as Activity).window.run {
            WindowCompat.getInsetsController(this, decorView).apply {
                isAppearanceLightStatusBars = true
            }
        }
    }

    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()
    val floatingButtonVisibility by remember { derivedStateOf { mutableStateOf(true) } }
    val nestedScrollConnection = remember {
        object : NestedScrollConnection {
            override fun onPreScroll(available: Offset, source: NestedScrollSource): Offset {
                if (available.y < -1) {
                    floatingButtonVisibility.value = false
                } else if (available.y > 1) {
                    floatingButtonVisibility.value = true
                }

                return Offset.Zero
            }
        }
    }

    Scaffold(modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            MediumTopAppBar(title = { Text(text = stringResource(id = R.string.favorite_area_list)) },
                scrollBehavior = scrollBehavior,
                colors = TopAppBarDefaults.mediumTopAppBarColors(
                    containerColor = Color.White,
                    scrolledContainerColor = Color.White,
                ))
        }, floatingActionButtonPosition = FabPosition.End, floatingActionButton = {
            AnimatedVisibility(
                visible = floatingButtonVisibility.value,
            ) {
                FloatingActionButton(
                    onClick = { /*TODO*/ },
                    containerColor = fabColor,
                    contentColor = Color.White,
                    shape = CircleShape,
                    modifier = Modifier.padding(end = 20.dp)
                ) {
                    Icon(painter = painterResource(id = io.github.pknujsp.weatherwizard.core.common.R.drawable.add),
                        contentDescription = null)
                }
            }
        }
    ) { innerPadding ->
        val lazyListState = rememberLazyListState()
        val viewModel: FavoriteAreaViewModel = hiltViewModel()
        val favoriteAreaList by viewModel.favoriteAreaList.collectAsStateWithLifecycle()
        val targetArea by viewModel.targetArea.collectAsStateWithLifecycle()

        LazyColumn(
            modifier = Modifier
                .padding(top = innerPadding.calculateTopPadding())
                .nestedScroll(nestedScrollConnection),
            state = lazyListState
        ) {
            favoriteAreaList.onSuccess {
                item {
                    CurrentLocationItem(checked = { targetArea.id }) {
                        viewModel.updateTargetArea(TargetAreaType.CurrentLocation)
                    }
                }
                items(it) { favoriteArea ->
                    AreaItem(favoriteArea, checked = { targetArea.id }) {
                        viewModel.updateTargetArea(TargetAreaType.CustomLocation(favoriteArea.id))
                    }
                }
            }
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
                Icon(imageVector = Icons.Default.MoreVert, contentDescription = null)
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
            Icon(imageVector = Icons.Rounded.LocationOn, contentDescription = stringResource(id = io.github.pknujsp.weatherwizard.core
                .common.R.string.current_location),
                tint = Color.Blue, modifier = Modifier.size(16.dp))
            Text(text = stringResource(id = io.github.pknujsp.weatherwizard.core.common.R.string.current_location),
                style = TextStyle(fontSize = 16.sp, color = Color.Blue, textAlign = TextAlign.Left),
                modifier = Modifier.weight(1f))
            Checkbox(checked = checked() == TargetAreaType.CurrentLocation.id, onCheckedChange = {
                onClick()
            })
        }
    }
}