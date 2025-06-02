package de.syntax_institut.androidabschlussprojekt.ui.component.maps

import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

/**
 * Ein runder Zurück-Button für Kartenansichten.
 *
 * Wird verwendet, um zur vorherigen Seite im Navigations-Stack zurückzukehren.
 *
 * @param navController Der NavController zur Navigation.
 * @param modifier Optionaler Modifier zur weiteren Anpassung.
 */
@Composable
fun BackButton(
    navController: NavController,
    modifier: Modifier = Modifier
) {
    FilledIconButton(
        onClick = { navController.popBackStack() },
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