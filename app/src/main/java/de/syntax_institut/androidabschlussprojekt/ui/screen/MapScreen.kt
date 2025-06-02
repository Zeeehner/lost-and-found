package de.syntax_institut.androidabschlussprojekt.ui.screen

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.navigation.NavHostController
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.*
import de.syntax_institut.androidabschlussprojekt.AdMobBanner
import de.syntax_institut.androidabschlussprojekt.data.local.model.Item
import de.syntax_institut.androidabschlussprojekt.ui.component.maps.BackButton
import de.syntax_institut.androidabschlussprojekt.ui.component.maps.ItemDetailsCard
import de.syntax_institut.androidabschlussprojekt.ui.component.maps.SearchBar
import de.syntax_institut.androidabschlussprojekt.ui.viewmodel.DetailViewModel
import de.syntax_institut.androidabschlussprojekt.ui.viewmodel.MapViewModel
import org.koin.androidx.compose.koinViewModel

/**
 * Composable für die Kartenansicht.
 * Zeigt alle Items als Marker auf der Karte an.
 * Ermöglicht das Anzeigen von Details zu einem ausgewählten Item.
 *
 * @param navController Navigationscontroller für Navigation
 * @param lat optionale Startbreitengrad (wenn null oder 0.0 → Deutschland-Standard)
 * @param lon optionale Startlängengrad (wenn null oder 0.0 → Deutschland-Standard)
 * @param viewModel ViewModel für Karten-Daten (Standard: Koin Injection)
 */
@Composable
fun MapScreen(
    navController: NavHostController,
    lat: Double? = null,
    lon: Double? = null,
    viewModel: MapViewModel = koinViewModel()
) {
    val items = viewModel.items.collectAsState().value
    var selectedItem by remember { mutableStateOf<Item?>(null) }
    val detailViewModel: DetailViewModel = koinViewModel()

    val targetLocation = if (lat != null && lon != null && lat != 0.0 && lon != 0.0) {
        LatLng(lat, lon)
    } else {
        LatLng(51.1657, 10.4515) // Deutschland
    }

    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(targetLocation, if (lat != null) 15f else 5f)
    }

    Box(modifier = Modifier.fillMaxSize()) {
        GoogleMap(
            modifier = Modifier.fillMaxSize(),
            cameraPositionState = cameraPositionState,
            uiSettings = MapUiSettings(zoomControlsEnabled = false),
            onMapClick = { }
        ) {
            items.forEach { item ->
                if (item.latitude != 0.0 && item.longitude != 0.0) {
                    Marker(
                        state = MarkerState(position = LatLng(item.latitude, item.longitude)),
                        title = item.title,
                        snippet = "${item.status} – ${item.locationName}",
                        icon = BitmapDescriptorFactory.defaultMarker(
                            if (item.status == "lost") 0f else 120f
                        ),
                        onClick = {
                            selectedItem = item
                            false
                        }
                    )
                }
            }
        }

        // Overlay UI: Back Button & SearchBar
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.Start
        ) {
            BackButton(navController)
            Spacer(modifier = Modifier.height(8.dp))

            SearchBar(
                onSearch = { viewModel.search(it) },
                onClose = { viewModel.clearSearch() }
            )
        }

        // Item Details anzeigen, falls ein Item ausgewählt ist
        AnimatedVisibility(
            visible = selectedItem != null,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(16.dp)
                .zIndex(1f)
        ) {
            selectedItem?.let { item ->
                ItemDetailsCard(
                    item = item,
                    onClose = { selectedItem = null },
                    viewModel = detailViewModel,
                    navController = navController
                )
            }
        }

        // AdMob Banner am unteren Rand
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
                .zIndex(0f)
        ) {
            AdMobBanner(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
                    .align(Alignment.Center)
            )
        }
    }
}