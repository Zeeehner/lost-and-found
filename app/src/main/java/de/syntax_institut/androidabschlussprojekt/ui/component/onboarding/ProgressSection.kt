package de.syntax_institut.androidabschlussprojekt.ui.component.onboarding

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import de.syntax_institut.androidabschlussprojekt.R
import de.syntax_institut.androidabschlussprojekt.ui.viewmodel.OnboardingViewModel

/**
 * Zeigt den Fortschritt des Onboarding-Prozesses in Form eines Textes und eines Fortschrittsbalkens.
 *
 * @param viewModel Das OnboardingViewModel, das den aktuellen Fortschritt liefert.
 * @param totalHints Die Gesamtanzahl der Onboarding-Hinweise.
 */
@Composable
fun ProgressSection(viewModel: OnboardingViewModel, totalHints: Int) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = stringResource(
                id = R.string.hint_progress,
                viewModel.currentHintIndex + 1,
                totalHints
            ),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.primary,
            fontWeight = FontWeight.Medium
        )

        LinearProgressIndicator(
            progress = {
                (viewModel.currentHintIndex + 1).toFloat() / totalHints
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(6.dp)
                .clip(RoundedCornerShape(1.dp)),
            color = MaterialTheme.colorScheme.primary,
            trackColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)
        )
    }
}