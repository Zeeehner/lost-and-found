package de.syntax_institut.androidabschlussprojekt.ui.component.edit

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import de.syntax_institut.androidabschlussprojekt.R

/**
 * Eine TopAppBar für den Bearbeitungsbildschirm mit Zurück- und Speichern-Funktionalität.
 *
 * @param onBack Callback, das beim Klicken auf den Zurück-Button ausgelöst wird.
 * @param onSave Callback, das beim Klicken auf das Speichern-Symbol ausgelöst wird.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditTopBar(
    onBack: () -> Unit,
    onSave: () -> Unit
) {
    TopAppBar(
        title = { Text(stringResource(R.string.edit_entry)) },
        navigationIcon = {
            IconButton(onClick = onBack) {
                Icon(Icons.Default.ArrowBack, contentDescription = stringResource(R.string.back))
            }
        },
        actions = {
            IconButton(onClick = onSave) {
                Icon(Icons.Default.Check, contentDescription = stringResource(R.string.save))
            }
        }
    )
}