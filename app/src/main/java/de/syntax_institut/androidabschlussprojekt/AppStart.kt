package de.syntax_institut.androidabschlussprojekt

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import de.syntax_institut.androidabschlussprojekt.ui.navigation.NavGraph
import de.syntax_institut.androidabschlussprojekt.ui.theme.AppTheme
import de.syntax_institut.androidabschlussprojekt.ui.viewmodel.SettingsViewModel
import org.koin.androidx.compose.koinViewModel
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember

/**
 * Einstiegspunkt der App.
 *
 * Setzt das App-Theme und enthält das Scaffold-Layout,
 * in dem die Navigationsgraph-Komponente platziert wird.
 *
 * @param modifier Modifier zur Anpassung der Darstellung des Composables
 */
@Composable
fun AppStart(modifier: Modifier = Modifier) {

    val viewModel: SettingsViewModel = koinViewModel()
    val uiState by viewModel.uiState.collectAsState()

    AppTheme(darkTheme = uiState.darkMode) {
        Scaffold(modifier = Modifier.fillMaxSize()) { paddingValues ->
            NavGraph(
                modifier = Modifier.padding(paddingValues)
            )
        }
    }
}