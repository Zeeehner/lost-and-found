package de.syntax_institut.androidabschlussprojekt.ui.component.detail

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import de.syntax_institut.androidabschlussprojekt.R
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * TopAppBar für die Detailansicht eines Items mit Debounce für den Zurück-Button.
 *
 * Zeigt je nach Eigentümerschaft verschiedene Aktionen an:
 * - Besitzer: Bearbeiten + Teilen
 * - Andere: Nachricht senden + Teilen
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailTopAppBar(
    isOwner: Boolean,
    onBackClick: () -> Unit,
    onEditClick: () -> Unit,
    onShareClick: () -> Unit,
    onMessageClick: () -> Unit
) {
    // Debounce Mechanismus für den Zurück-Button
    val coroutineScope = rememberCoroutineScope()
    var backPressedOnce by remember { mutableStateOf(false) }

    CenterAlignedTopAppBar(
        title = {
            Text(
                text = stringResource(R.string.details),
                fontWeight = FontWeight.SemiBold,
                style = MaterialTheme.typography.titleLarge
            )
        },
        navigationIcon = {
            // Debounce Mechanismus
            IconButton(onClick = {
                if (!backPressedOnce) {
                    backPressedOnce = true
                    onBackClick()
                    coroutineScope.launch {
                        delay(1000)
                        backPressedOnce = false
                    }
                }
            }) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = stringResource(R.string.back),
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        },
        actions = {
            if (isOwner) {
                IconButton(onClick = onEditClick) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = stringResource(R.string.edit),
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            } else {
                IconButton(onClick = onMessageClick) {
                    Icon(
                        imageVector = Icons.Default.Email,
                        contentDescription = stringResource(R.string.message),
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            }
            IconButton(onClick = onShareClick) {
                Icon(
                    imageVector = Icons.Default.Share,
                    contentDescription = stringResource(R.string.share),
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        },
        colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
            containerColor = MaterialTheme.colorScheme.surface,
            titleContentColor = MaterialTheme.colorScheme.onSurface
        )
    )
}
