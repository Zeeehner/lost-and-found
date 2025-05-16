package de.syntax_institut.androidabschlussprojekt.ui.screen

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import de.syntax_institut.androidabschlussprojekt.ui.component.login.LoginScreenContent
import de.syntax_institut.androidabschlussprojekt.ui.viewmodel.AuthViewModel
import org.koin.androidx.compose.koinViewModel

@Composable
fun LoginScreen(
    authViewModel: AuthViewModel = koinViewModel(),
    onLoginSuccess: () -> Unit,
    onNavigateToOnboarding: () -> Unit
) {
    val loginResult = authViewModel.loginResult.collectAsState().value
    val navigateToOnboarding = authViewModel.navigateToOnboarding.collectAsState().value

    // Registrierung erfolgreich → Starte Onboarding
    LaunchedEffect(navigateToOnboarding) {
        if (navigateToOnboarding) {
            onNavigateToOnboarding()
            authViewModel.resetNavigationFlags()
        }
    }


    // Login erfolgreich → Direkt ins Dashboard
    LaunchedEffect(loginResult) {
        if (loginResult == true) {
            onLoginSuccess()
            authViewModel.resetNavigationFlags()
        }
    }

    // UI Content
    LoginScreenContent(
        authViewModel = authViewModel,
        onLoginSuccess = onLoginSuccess
    )
}
