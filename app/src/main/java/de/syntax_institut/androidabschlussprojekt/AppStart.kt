package de.syntax_institut.androidabschlussprojekt

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import de.syntax_institut.androidabschlussprojekt.ui.navigation.NavGraph
import de.syntax_institut.androidabschlussprojekt.ui.theme.AppTheme

@Composable
fun AppStart(modifier: Modifier = Modifier) {
    AppTheme {
        Scaffold(modifier = Modifier.fillMaxSize()) { paddingValues ->
            NavGraph(
                modifier = Modifier.padding(paddingValues)
            )
        }
    }
}