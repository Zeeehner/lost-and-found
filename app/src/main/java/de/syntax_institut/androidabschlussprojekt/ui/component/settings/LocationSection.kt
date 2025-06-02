package de.syntax_institut.androidabschlussprojekt.ui.component.settings

import android.Manifest
import androidx.activity.result.ActivityResultLauncher
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.rounded.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import de.syntax_institut.androidabschlussprojekt.R
import de.syntax_institut.androidabschlussprojekt.ui.util.PermissionUtils
import de.syntax_institut.androidabschlussprojekt.ui.viewmodel.SettingsViewModel

/**
 * Anzeige eines Standorteinstellungsabschnitts, inkl. Berechtigungsanforderung und Stadtname.
 *
 * @param viewModel ViewModel für Zugriff auf Standortdaten und Sichtbarkeit.
 * @param requestLocationPermissionsLauncher Launcher zur Berechtigungsabfrage.
 */
@Composable
fun LocationSection(
    viewModel: SettingsViewModel,
    requestLocationPermissionsLauncher: ActivityResultLauncher<Array<String>>
) {
    val uiState = viewModel.uiState.collectAsState().value
    val context = LocalContext.current

    Column(modifier = Modifier.fillMaxWidth()) {
        SettingsItem(
            icon = Icons.Default.LocationOn,
            title = stringResource(R.string.location_permission),
            subtitle = stringResource(R.string.location_permission_sub),
            onClick = { viewModel.toggleLocationInfo() }
        )

        AnimatedVisibility(
            visible = uiState.showLocationInfo,
            enter = fadeIn() + expandVertically(),
            exit = fadeOut() + shrinkVertically()
        ) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                shape = RoundedCornerShape(12.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    // Anzeige der aktuellen Stadt
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.LocationOn,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp),
                            tint = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = stringResource(
                                R.string.current_location,
                                uiState.cityName ?: stringResource(R.string.location_not_available)
                            ),
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Medium
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Button zur Standortaktualisierung mit Berechtigungsprüfung
                    Button(
                        onClick = {
                            PermissionUtils.handleActionWithPermissions(
                                context = context,
                                requestMultiplePermissionLauncher = requestLocationPermissionsLauncher,
                                permissions = listOf(
                                    Manifest.permission.ACCESS_COARSE_LOCATION,
                                    Manifest.permission.ACCESS_FINE_LOCATION
                                ),
                                action = @androidx.annotation.RequiresPermission(
                                    allOf = [
                                        Manifest.permission.ACCESS_FINE_LOCATION,
                                        Manifest.permission.ACCESS_COARSE_LOCATION
                                    ]
                                ) {
                                    viewModel.updateLocation(context)
                                },
                                showRationale = {
                                    viewModel.updatePermissionStatus(
                                        context = context,
                                        statusResId = R.string.location_permission_needed
                                    )
                                }
                            )
                        },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.8f)
                        )
                    ) {
                        Icon(
                            Icons.Rounded.Refresh,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(stringResource(R.string.update_location))
                    }
                }
            }
        }
    }
}