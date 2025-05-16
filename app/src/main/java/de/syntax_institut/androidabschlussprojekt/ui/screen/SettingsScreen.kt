package de.syntax_institut.androidabschlussprojekt.ui.screen

import android.widget.Toast
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
import org.koin.androidx.compose.koinViewModel
import de.syntax_institut.androidabschlussprojekt.R
import de.syntax_institut.androidabschlussprojekt.ui.component.settings.LogoutButton
import de.syntax_institut.androidabschlussprojekt.ui.component.settings.LogoutDialog
import de.syntax_institut.androidabschlussprojekt.ui.navigation.Screen
import de.syntax_institut.androidabschlussprojekt.ui.viewmodel.SettingsViewModel

@Composable
fun SettingsScreen(
    navController: NavController,
    modifier: Modifier = Modifier,
    viewModel: SettingsViewModel = koinViewModel(),
) {

    val context = LocalContext.current
    val uiState by viewModel.uiState.collectAsState()

    // Logout
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
        Divider()
        Text(
            stringResource(R.string.settings),
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.weight(1f))
        Divider(modifier = Modifier.padding(vertical = 8.dp))
        Text(
            stringResource(R.string.account),
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )

        LogoutButton(onClick = { viewModel.showLogoutDialog() })

        Spacer(modifier = Modifier.weight(1f))

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
