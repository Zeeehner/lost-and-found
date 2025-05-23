package de.syntax_institut.androidabschlussprojekt.ui.screen

import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import de.syntax_institut.androidabschlussprojekt.R
import de.syntax_institut.androidabschlussprojekt.ui.component.settings.DarkModeSetting
import de.syntax_institut.androidabschlussprojekt.ui.component.settings.LocationSection
import de.syntax_institut.androidabschlussprojekt.ui.component.settings.LogoutButton
import de.syntax_institut.androidabschlussprojekt.ui.component.settings.LogoutDialog
import de.syntax_institut.androidabschlussprojekt.ui.component.settings.NotificationPermissionSection
import de.syntax_institut.androidabschlussprojekt.ui.component.settings.PermissionInfoCard
import de.syntax_institut.androidabschlussprojekt.ui.component.settings.PhoneNumberSection
import de.syntax_institut.androidabschlussprojekt.ui.component.settings.ProfileSection
import de.syntax_institut.androidabschlussprojekt.ui.viewmodel.SettingsViewModel
import org.koin.androidx.compose.koinViewModel
import de.syntax_institut.androidabschlussprojekt.ui.navigation.Screen

@Composable
fun SettingsScreen(
    navController: NavController,
    modifier: Modifier = Modifier,
    viewModel: SettingsViewModel = koinViewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current
    var showLocationRationale by remember { mutableStateOf(false) }

    val locationAction: () -> Unit = {
        viewModel.updateLocation(context)
    }

    val requestLocationPermissionsLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions: Map<String, Boolean> ->
        if (permissions.values.all { it }) {
            viewModel.updatePermissionStatus(context, R.string.permission_granted)

            locationAction()
        } else {
            viewModel.updatePermissionStatus(context, R.string.permission_denied)

            showLocationRationale = true
        }
    }

    val notificationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { isGranted ->
            if (isGranted) {
                viewModel.onNotificationPermissionGranted(context)
                Toast.makeText(context, "Benachrichtigungen aktiviert", Toast.LENGTH_SHORT).show()
            } else {
                viewModel.updatePermissionStatus(context, R.string.permission_denied)

            }
        }
    )

    if (uiState.showLogoutDialog) {
        LogoutDialog(onConfirm = {
            viewModel.logout()
            Toast.makeText(context, context.getString(R.string.logout_success), Toast.LENGTH_SHORT)
                .show()
            navController.navigate(Screen.Login.route) { popUpTo(0) { inclusive = true } }
        }, onDismiss = { viewModel.dismissLogoutDialog() })
    }
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        ProfileSection(viewModel.authViewModel, uiState.cityName)
        Divider()
        Text(
            stringResource(R.string.settings),
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )

        DarkModeSetting(darkMode = uiState.darkMode, onToggle = { viewModel.toggleDarkMode() })
        LocationSection(
            viewModel,
            requestLocationPermissionsLauncher = requestLocationPermissionsLauncher
        )
        NotificationPermissionSection(
            viewModel = viewModel,
            context = context,
            notificationPermissionLauncher = notificationPermissionLauncher
        )
        if (uiState.permissionStatus.isNotEmpty()) {
            PermissionInfoCard(uiState.permissionStatus)
        }
        Divider(modifier = Modifier.padding(vertical = 8.dp))
        Text(
            stringResource(R.string.account),
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.weight(1f))

        PhoneNumberSection(authViewModel = viewModel.authViewModel)

        LogoutButton(onClick = { viewModel.showLogoutDialog() })

        Row(
            modifier = Modifier.align(Alignment.CenterHorizontally)
        ) {
            Text(
                stringResource(R.string.app_version, "1.0.0"),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
            )
        }
    }
}
