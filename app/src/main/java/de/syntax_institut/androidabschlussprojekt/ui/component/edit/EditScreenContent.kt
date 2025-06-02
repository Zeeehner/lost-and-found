package de.syntax_institut.androidabschlussprojekt.ui.component.edit

import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.TextFieldValue

/**
 * Kombiniert die TopBar und das Bearbeitungsformular zu einem vollständigen Screen.
 *
 * @param title Der aktuelle Titel als [TextFieldValue].
 * @param onTitleChange Callback, das bei Änderung des Titels aufgerufen wird.
 * @param description Die aktuelle Beschreibung als [TextFieldValue].
 * @param onDescriptionChange Callback, das bei Änderung der Beschreibung aufgerufen wird.
 * @param onSave Callback, das beim Speichern ausgelöst wird.
 * @param onBack Callback, das beim Zurücknavigieren ausgelöst wird.
 */
@Composable
fun EditScreenContent(
    title: TextFieldValue,
    onTitleChange: (TextFieldValue) -> Unit,
    description: TextFieldValue,
    onDescriptionChange: (TextFieldValue) -> Unit,
    onSave: () -> Unit,
    onBack: () -> Unit
) {
    Column(modifier = Modifier.fillMaxSize()) {
        EditTopBar(onBack = onBack, onSave = onSave)

        EditForm(
            title = title,
            onTitleChange = onTitleChange,
            description = description,
            onDescriptionChange = onDescriptionChange
        )
    }
}