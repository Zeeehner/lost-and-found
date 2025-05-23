package de.syntax_institut.androidabschlussprojekt.ui.component.create

import android.R.attr.minLines
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.Send
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import de.syntax_institut.androidabschlussprojekt.R

@Composable
fun LostItemForm(
    title: String,
    description: String,
    onTitleChange: (String) -> Unit,
    onDescChange: (String) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(stringResource(R.string.add_entry_description), style = MaterialTheme.typography.titleMedium)

            OutlinedTextField(
                value = title,
                onValueChange = onTitleChange,
                label = { Text(stringResource(R.string.title)) },
                leadingIcon = { Icon(Icons.Outlined.Send, contentDescription = null) },
                isError = title.isNotBlank() && title.length < 3,
                supportingText = {
                    if (title.isNotBlank() && title.length < 3) {
                        Text(stringResource(R.string.title_too_short))
                    }
                },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = description,
                onValueChange = onDescChange,
                label = { Text(stringResource(R.string.description)) },
                leadingIcon = { Icon(Icons.Outlined.Info, contentDescription = null) },
                modifier = Modifier.fillMaxWidth().heightIn(min = 20.dp),
                minLines = 3
            )
        }
    }
}