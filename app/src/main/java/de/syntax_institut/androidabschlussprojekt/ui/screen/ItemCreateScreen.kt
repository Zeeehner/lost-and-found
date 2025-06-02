package de.syntax_institut.androidabschlussprojekt.ui.screen

import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import de.syntax_institut.androidabschlussprojekt.ui.component.create.ItemCreateContent
import de.syntax_institut.androidabschlussprojekt.ui.viewmodel.AuthViewModel
import org.koin.androidx.compose.koinViewModel

/**
 * Composable für den Bildschirm zum Erstellen eines neuen Items.
 *
 * @param navController Navigation Controller zum Navigieren zwischen Screens
 * @param authViewModel ViewModel für Authentifizierungsdaten (standardmäßig vom Koin bereitgestellt)
 */
@Composable
fun ItemCreateScreen(
    navController: NavController,
    authViewModel: AuthViewModel = koinViewModel()
) {
    ItemCreateContent(navController = navController, authViewModel = authViewModel)
}