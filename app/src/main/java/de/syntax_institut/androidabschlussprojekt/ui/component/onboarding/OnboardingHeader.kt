package de.syntax_institut.androidabschlussprojekt.ui.component.onboarding

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import de.syntax_institut.androidabschlussprojekt.ui.viewmodel.OnboardingViewModel
import androidx.compose.ui.res.stringResource
import de.syntax_institut.androidabschlussprojekt.R

@Composable
fun OnboardingHeader(
    allHintsDone: Boolean,
    viewModel: OnboardingViewModel
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = if (allHintsDone)
                stringResource(id = R.string.onboarding_header_contact)
            else
                stringResource(id = R.string.onboarding_header_legal),
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )

        Text(
            text = if (allHintsDone)
                stringResource(id = R.string.onboarding_subtext_contact)
            else
                stringResource(id = R.string.onboarding_subtext_legal),
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
            textAlign = TextAlign.Center
        )
    }
}
