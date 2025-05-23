package de.syntax_institut.androidabschlussprojekt.ui.screen

import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import de.syntax_institut.androidabschlussprojekt.ui.component.create.ItemCreateContent
import de.syntax_institut.androidabschlussprojekt.ui.viewmodel.AuthViewModel
import org.koin.androidx.compose.koinViewModel

@Composable
fun ItemCreateScreen(
    navController: NavController,
    authViewModel: AuthViewModel = koinViewModel()
) {
    ItemCreateContent(navController = navController, authViewModel = authViewModel)
}