package de.syntax_institut.androidabschlussprojekt.ui.component.edit

import androidx.compose.foundation.layout.*
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import de.syntax_institut.androidabschlussprojekt.R

/**
 * Formular zur Bearbeitung eines bereits bestehenden Eintrags.
 *
 * @param title Der aktuelle Titel als [TextFieldValue].
 * @param onTitleChange Callback, das bei Änderung des Titels aufgerufen wird.
 * @param description Die aktuelle Beschreibung als [TextFieldValue].
 * @param onDescriptionChange Callback, das bei Änderung der Beschreibung aufgerufen wird.
 */
@Composable
fun EditForm(
    title: TextFieldValue,
    onTitleChange: (TextFieldValue) -> Unit,
    description: TextFieldValue,
    onDescriptionChange: (TextFieldValue) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        OutlinedTextField(
            value = title,
            onValueChange = onTitleChange,
            label = { Text(stringResource(R.string.title)) },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = description,
            onValueChange = onDescriptionChange,
            label = { Text(stringResource(R.string.description)) },
            modifier = Modifier.fillMaxWidth(),
            maxLines = 5
        )
    }
}