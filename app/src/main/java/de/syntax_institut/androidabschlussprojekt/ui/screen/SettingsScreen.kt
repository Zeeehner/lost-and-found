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
import de.syntax_institut.androidabschlussprojekt.AdMobBanner
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

/**
 * SettingsScreen zeigt alle Einstellungen des Nutzers an, inklusive Profilinfo,
 * Dark Mode, Standortberechtigungen, Benachrichtigungserlaubnisse,
 * Telefonnummer und Logout-Option.
 *
 * @param navController Navigation Controller für Navigation innerhalb der App.
 * @param modifier Modifier zum Anpassen des Composables.
 * @param viewModel ViewModel zur Steuerung der UI-Logik (Standard: Koin Injection).
 */
@Composable
fun SettingsScreen(
    navController: NavController,
    modifier: Modifier = Modifier,
    viewModel: SettingsViewModel = koinViewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current
    var showLocationRationale by remember { mutableStateOf(false) }

    // Aktion zur Aktualisierung des Standorts
    val locationAction: () -> Unit = {
        viewModel.updateLocation(context)
    }

    // Launcher für Standortberechtigungen
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

    // Launcher für Benachrichtigungsberechtigungen
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

    // Logout Dialog anzeigen, falls gewünscht
    if (uiState.showLogoutDialog) {
        LogoutDialog(
            onConfirm = {
                viewModel.logout()
                Toast.makeText(context, context.getString(R.string.logout_success), Toast.LENGTH_SHORT).show()
                navController.navigate(Screen.Login.route) { popUpTo(0) { inclusive = true } }
            },
            onDismiss = { viewModel.dismissLogoutDialog() }
        )
    }

    Box(modifier = modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = 60.dp)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Profil-Übersicht
            ProfileSection(viewModel.authViewModel, uiState.cityName)
            Divider()

            Text(
                stringResource(R.string.settings),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            // Dark Mode Einstellung
            DarkModeSetting(darkMode = uiState.darkMode, onToggle = { viewModel.toggleDarkMode() })

            // Standort-Berechtigungen
            LocationSection(viewModel, requestLocationPermissionsLauncher)

            // Benachrichtigungs-Berechtigungen
            NotificationPermissionSection(viewModel, context, notificationPermissionLauncher)

            // Info Karte bei Berechtigungsstatus
            if (uiState.permissionStatus.isNotEmpty()) {
                PermissionInfoCard(uiState.permissionStatus)
            }

            Divider(modifier = Modifier.padding(vertical = 8.dp))

            Text(
                stringResource(R.string.account),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            // Telefonnummer Eingabe / Anzeige
            PhoneNumberSection(authViewModel = viewModel.authViewModel)

            // Logout Button
            LogoutButton(onClick = { viewModel.showLogoutDialog() })

            // App Versionsinfo
            Text(
                stringResource(R.string.app_version, "1.0.0"),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )
        }
        // AdMob Banner am unteren Rand
        AdMobBanner(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .height(50.dp)
        )
    }
}