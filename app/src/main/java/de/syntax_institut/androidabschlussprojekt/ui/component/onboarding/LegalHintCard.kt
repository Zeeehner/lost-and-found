package de.syntax_institut.androidabschlussprojekt.ui.component.onboarding

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import de.syntax_institut.androidabschlussprojekt.R

/**
 * Eine Karte, die einen rechtlichen Hinweis oder eine wichtige Information darstellt,
 * die vom Nutzer bestätigt werden muss.
 *
 * @param hint Der anzuzeigende Text.
 * @param isAcknowledged Gibt an, ob der Nutzer den Hinweis bestätigt hat.
 * @param onAcknowledge Callback, wenn der Nutzer die Checkbox ändert.
 */
@Composable
fun LegalHintCard(
    hint: String,
    isAcknowledged: Boolean,
    onAcknowledge: () -> Unit
) {
    val cardModifier = Modifier
        .fillMaxWidth()
        .shadow(
            elevation = if (isAcknowledged) 2.dp else 8.dp,
            shape = RoundedCornerShape(16.dp)
        )
        .then(
            if (!isAcknowledged)
                Modifier.border(
                    1.dp,
                    MaterialTheme.colorScheme.outline.copy(alpha = 0.3f),
                    RoundedCornerShape(16.dp)
                )
            else Modifier
        )

    Card(
        modifier = cardModifier,
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.Top
            ) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .background(
                            color = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                            shape = CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Filled.Info,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(20.dp)
                    )
                }

                Text(
                    text = hint,
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.weight(1f),
                    lineHeight = MaterialTheme.typography.bodyLarge.lineHeight * 1.2f
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                CustomCheckbox(
                    checked = isAcknowledged,
                    onCheckedChange = onAcknowledge
                )

                Text(
                    text = if (isAcknowledged)
                        stringResource(id = R.string.acknowledged)
                    else
                        stringResource(id = R.string.acknowledge_hint),
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = if (isAcknowledged) FontWeight.SemiBold else FontWeight.Normal,
                    color = if (isAcknowledged)
                        MaterialTheme.colorScheme.primary
                    else
                        MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)
                )
            }
        }
    }
}