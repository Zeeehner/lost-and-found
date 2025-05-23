package de.syntax_institut.androidabschlussprojekt.ui.component.list

import de.syntax_institut.androidabschlussprojekt.R
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource

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
                    placeholder = { Text(stringResource(R.string.search_placeholder))},
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    trailingIcon = {
                        IconButton(onClick = onClearSearch) {
                            Icon(Icons.Default.Clear, contentDescription = null)
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
                    contentDescription = null
                )
            }
            IconButton(onClick = onNavigateToMap) {
                Icon(Icons.Default.LocationOn, contentDescription = null)
            }
        }
    )
}