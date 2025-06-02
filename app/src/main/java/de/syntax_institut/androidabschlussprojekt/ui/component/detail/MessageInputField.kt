package de.syntax_institut.androidabschlussprojekt.ui.component.detail

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import de.syntax_institut.androidabschlussprojekt.R

/**
 * Eingabefeld für Chat-Nachrichten mit Senden-Button und optionaler Löschfunktion.
 *
 * @param message Der aktuelle Inhalt des Textfelds.
 * @param onMessageChange Callback bei Textänderung.
 * @param onSend Callback beim Klick auf den Senden-Button.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MessageInputField(
    message: String,
    onMessageChange: (String) -> Unit,
    onSend: () -> Unit
) {
    val isSendEnabled = message.isNotBlank()

    Surface(
        tonalElevation = 3.dp,
        shadowElevation = 2.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            TextField(
                value = message,
                onValueChange = onMessageChange,
                modifier = Modifier.weight(1f),
                placeholder = {
                    Text(
                        text = stringResource(R.string.write_message),
                        style = MaterialTheme.typography.bodyMedium
                    )
                },
                maxLines = 3,
                shape = RoundedCornerShape(24.dp),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                    unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                    focusedIndicatorColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.2f),
                    unfocusedIndicatorColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0f)
                ),
                trailingIcon = {
                    AnimatedVisibility(
                        visible = message.isNotBlank(),
                        enter = fadeIn(),
                        exit = fadeOut()
                    ) {
                        IconButton(onClick = { onMessageChange("") }) {
                            Icon(
                                imageVector = Icons.Default.Clear,
                                contentDescription = stringResource(R.string.clear),
                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            )

            Spacer(modifier = Modifier.width(12.dp))

            FloatingActionButton(
                onClick = { if (isSendEnabled) onSend() },
                shape = CircleShape,
                containerColor = if (isSendEnabled)
                    MaterialTheme.colorScheme.primary
                else
                    MaterialTheme.colorScheme.surfaceVariant,
                contentColor = if (isSendEnabled)
                    MaterialTheme.colorScheme.onPrimary
                else
                    MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                modifier = Modifier.size(48.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Send,
                    contentDescription = stringResource(R.string.send)
                )
            }
        }
    }
}