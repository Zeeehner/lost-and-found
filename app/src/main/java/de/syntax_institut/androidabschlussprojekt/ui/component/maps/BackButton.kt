package de.syntax_institut.androidabschlussprojekt.ui.component.maps

import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * Ein runder Zurück-Button mit Debounce-Schutz für Kartenansichten.
 *
 * Verhindert versehentliches mehrfaches Poppen des BackStacks.
 *
 * @param navController Der NavController zur Navigation.
 * @param modifier Optionaler Modifier zur weiteren Anpassung.
 */
@Composable
fun BackButton(
    navController: NavController,
    modifier: Modifier = Modifier
) {
    // Debounce Mechanismus für den Back-Button
    val coroutineScope = rememberCoroutineScope()
    var backPressedOnce by remember { mutableStateOf(false) }

    FilledIconButton(
        onClick = {
            if (!backPressedOnce) {
                backPressedOnce = true
                if (navController.previousBackStackEntry != null) {
                    navController.popBackStack()
                }
                coroutineScope.launch {
                    delay(1000)
                    backPressedOnce = false
                }
            }
        },
        modifier = modifier.shadow(4.dp, CircleShape),
        shape = CircleShape,
        colors = IconButtonDefaults.filledIconButtonColors(
            containerColor = MaterialTheme.colorScheme.surface,
            contentColor = MaterialTheme.colorScheme.primary
        )
    ) {
        Icon(
            imageVector = Icons.Default.ArrowBack,
            contentDescription = "arrow back",
            modifier = Modifier.size(22.dp)
        )
    }
}
