package de.syntax_institut.androidabschlussprojekt.ui.component.list

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import de.syntax_institut.androidabschlussprojekt.R

/**
 * Top-App-Bar für die Item-Liste mit optionaler Suchleiste und Buttons für Suche & Kartenansicht.
 *
 * @param showSearch Gibt an, ob die Suchleiste angezeigt werden soll
 * @param searchQuery Aktueller Text in der Suchleiste
 * @param onToggleSearch Callback zum Ein-/Ausblenden der Suche
 * @param onSearchChange Callback bei Änderung des Suchtextes
 * @param onClearSearch Callback zum Leeren des Suchfeldes
 * @param onNavigateToMap Callback für den Karten-Button
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ItemListTopBar(
    showSearch: Boolean,
    searchQuery: String,
    onToggleSearch: () -> Unit,
    onSearchChange: (String) -> Unit,
    onClearSearch: () -> Unit,
    onNavigateToMap: () -> Unit
) {
    TopAppBar(
        title = {
            if (showSearch) {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = onSearchChange,
                    placeholder = { Text(stringResource(R.string.search_placeholder)) },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    trailingIcon = {
                        IconButton(onClick = onClearSearch) {
                            Icon(Icons.Default.Clear, contentDescription = stringResource(R.string.clear))
                        }
                    },
                    colors = TextFieldDefaults.colors()
                )
            } else {
                Text(stringResource(R.string.screen_title))
            }
        },
        actions = {
            IconButton(onClick = onToggleSearch) {
                Icon(
                    if (showSearch) Icons.Default.Search else Icons.Outlined.Search,
                    contentDescription = stringResource(R.string.search)
                )
            }
            IconButton(onClick = onNavigateToMap) {
                Icon(
                    Icons.Default.LocationOn,
                    contentDescription = stringResource(R.string.view_on_map)
                )
            }
        }
    )
}