package de.syntax_institut.androidabschlussprojekt.ui.component.onboarding

import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import de.syntax_institut.androidabschlussprojekt.ui.viewmodel.OnboardingViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * Zeigt den aktuellen rechtlichen Hinweis an und ermöglicht es dem Nutzer, diesen zu bestätigen.
 * Nach der Bestätigung wird automatisch zum nächsten Hinweis gewechselt.
 */
@Composable
fun LegalHintStep(viewModel: OnboardingViewModel) {
    val context = LocalContext.current
    val legalHints = viewModel.getLegalHints(context)
    val currentHint = legalHints.getOrNull(viewModel.currentHintIndex)
    val isAcknowledged = viewModel.hintAcknowledged
    val scope = rememberCoroutineScope()

    AnimatedContent(
        targetState = currentHint,
        transitionSpec = {
            (slideInHorizontally { it } + fadeIn(tween(300))) togetherWith
                    (slideOutHorizontally { -it } + fadeOut(tween(300)))
        },
        label = "LegalHintTransition"
    ) { hint ->
        hint?.let {
            LegalHintCard(
                hint = it,
                isAcknowledged = isAcknowledged,
                onAcknowledge = {
                    if (!isAcknowledged) {
                        viewModel.acknowledgeHint()
                        scope.launch {
                            delay(500)
                            viewModel.nextHint()
                        }
                    }
                }
            )
        }
    }
}