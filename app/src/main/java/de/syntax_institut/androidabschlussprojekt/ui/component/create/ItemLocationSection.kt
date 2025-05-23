package de.syntax_institut.androidabschlussprojekt.ui.component.create

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.runtime.getValue
import de.syntax_institut.androidabschlussprojekt.R
import de.syntax_institut.androidabschlussprojekt.ui.viewmodel.CreateViewModel

@Composable
fun ItemLocationSection(
    location: String,
    latitude: String,
    longitude: String,
    onLocationChange: (String) -> Unit,
    onLatChange: (String) -> Unit,
    onLongChange: (String) -> Unit,
    showDetails: Boolean,
    onToggleDetails: () -> Unit,
    onAutoLocate: () -> Unit,
    viewModel: CreateViewModel = viewModel()
) {
    val context = LocalContext.current
    val formState by viewModel.formState.collectAsState()

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(stringResource(R.string.location), style = MaterialTheme.typography.titleMedium)
                TextButton(onClick = onToggleDetails) {
                    Text(if (showDetails) stringResource(R.string.less) else stringResource(R.string.more))
                    Icon(
                        if (showDetails) Icons.Default.Clear else Icons.Default.LocationOn,
                        contentDescription = null
                    )
                }
            }

            OutlinedTextField(
                value = formState.location,
                onValueChange = { newValue -> viewModel.updateField { copy(location = newValue) } },
                label = { Text(stringResource(R.string.location)) },
                modifier = Modifier.fillMaxWidth(),
                leadingIcon = { Icon(Icons.Default.LocationOn, contentDescription = null) }
            )

            AnimatedVisibility(visible = showDetails) {
                Column {
                    OutlinedTextField(
                        value = latitude,
                        onValueChange = onLatChange,
                        label = { Text(stringResource(R.string.latitude)) },
                        modifier = Modifier.fillMaxWidth(),
                        leadingIcon = { Icon(Icons.Default.LocationOn, contentDescription = null) }
                    )
                    OutlinedTextField(
                        value = longitude,
                        onValueChange = onLongChange,
                        label = { Text(stringResource(R.string.longitude)) },
                        modifier = Modifier.fillMaxWidth(),
                        leadingIcon = { Icon(Icons.Default.LocationOn, contentDescription = null) }
                    )

                    Button(
                        onClick = { viewModel.updateLatLongFromLocation() },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(stringResource(R.string.set_coordinates_automatically))
                    }

                    Button(
                        onClick = { viewModel.fetchCurrentLocation(context) },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(stringResource(R.string.set_location_automatically))
                    }
                }
            }
        }
    }
}
