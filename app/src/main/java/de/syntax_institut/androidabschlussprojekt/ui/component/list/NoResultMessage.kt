package de.syntax_institut.androidabschlussprojekt.ui.component.list

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import de.syntax_institut.androidabschlussprojekt.R

/**
 * Zeigt eine Nachricht an, wenn keine Suchergebnisse für eine Filter-/Suchkombination gefunden wurden.
 *
 * @param filter Der aktuell ausgewählte Filter ("all", "lost", "found" o.ä.)
 * @param query Der aktuelle Suchtext
 */
@Composable
fun NoResultsMessage(filter: String, query: String) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                Icons.Outlined.Info,
                contentDescription = stringResource(R.string.no_results),
                modifier = Modifier
                    .size(64.dp)
                    .padding(bottom = 16.dp),
                tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.7f)
            )

            // Wählt die passende Nachricht je nach Filter- und Suchtext
            val message = when {
                query.isNotEmpty() ->
                    stringResource(R.string.no_results_for_query, query)
                filter != "all" ->
                    stringResource(R.string.no_results_for_filter, filter.lowercase())
                else ->
                    stringResource(R.string.no_items)
            }

            Text(
                text = message,
                style = MaterialTheme.typography.titleMedium,
                textAlign = TextAlign.Center
            )
        }
    }
}