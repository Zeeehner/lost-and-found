package de.syntax_institut.androidabschlussprojekt.ui.component.login

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import de.syntax_institut.androidabschlussprojekt.R
import de.syntax_institut.androidabschlussprojekt.ui.util.spacing
import de.syntax_institut.androidabschlussprojekt.ui.viewmodel.AuthViewModel

/**
 * Composable für den Login- oder Registrierungsbildschirm.
 *
 * @param authViewModel ViewModel für Authentifizierung.
 * @param onLoginSuccess Callback, der bei erfolgreichem Login aufgerufen wird.
 */
@Composable
fun LoginScreenContent(
    authViewModel: AuthViewModel,
    onLoginSuccess: () -> Unit
) {
    val loginResult by authViewModel.loginResult.collectAsState()
    val isLoading by authViewModel.isLoading.collectAsState()
    val errorMessage by authViewModel.errorMessage.collectAsState()
    val isRegistrationMode by authViewModel.isRegistrationMode.collectAsState()

    // Login-Erfolg abfangen
    LaunchedEffect(loginResult) {
        if (loginResult == true) onLoginSuccess()
    }

    // Fehler zurücksetzen, wenn Modus gewechselt wird
    LaunchedEffect(isRegistrationMode) {
        authViewModel.clearError()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        MaterialTheme.colorScheme.background,
                        MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                    )
                )
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(MaterialTheme.spacing.large),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(MaterialTheme.spacing.extraLarge))

            // App Icon
            Box(
                modifier = Modifier
                    .size(120.dp)
                    .clip(RoundedCornerShape(60.dp))
                    .background(MaterialTheme.colorScheme.primaryContainer),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.LocationOn,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onPrimaryContainer,
                    modifier = Modifier.size(64.dp)
                )
            }

            Spacer(modifier = Modifier.height(MaterialTheme.spacing.medium))

            // App Name
            Text(
                text = stringResource(R.string.app_name),
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )

            Spacer(modifier = Modifier.height(MaterialTheme.spacing.large))

            // Überschrift: Login oder Registrierung
            Text(
                text = stringResource(if (isRegistrationMode) R.string.register else R.string.login),
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Medium
            )

            Spacer(modifier = Modifier.height(MaterialTheme.spacing.large))

            // Fehleranzeige
            AnimatedVisibility(visible = errorMessage != null, enter = fadeIn(), exit = fadeOut()) {
                errorMessage?.let { error ->
                    ErrorCard(error)
                }
            }

            // Eingabeformular
            LoginForm(authViewModel = authViewModel, isLoading = isLoading)

            Spacer(modifier = Modifier.height(MaterialTheme.spacing.large))

            // Umschalten zwischen Login und Registrierung
            TextButton(onClick = { authViewModel.toggleMode() }) {
                Text(
                    text = stringResource(
                        if (isRegistrationMode) R.string.login_prompt else R.string.register_prompt
                    ),
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.primary
                )
            }

            Spacer(Modifier.weight(1f))

            // App-Version anzeigen
            Text(
                text = stringResource(R.string.app_version, "1.0.0"),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(bottom = MaterialTheme.spacing.medium)
            )
        }
    }
}