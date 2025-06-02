package de.syntax_institut.androidabschlussprojekt.ui.screen

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.unit.dp
import org.koin.androidx.compose.koinViewModel
import de.syntax_institut.androidabschlussprojekt.ui.component.onboarding.LegalHintStep
import de.syntax_institut.androidabschlussprojekt.ui.component.onboarding.OnboardingHeader
import de.syntax_institut.androidabschlussprojekt.ui.component.onboarding.OnboardingPhoneSection
import de.syntax_institut.androidabschlussprojekt.ui.component.onboarding.ProgressSection
import de.syntax_institut.androidabschlussprojekt.ui.viewmodel.OnboardingViewModel

/**
 * Zeigt den Onboarding-Bildschirm mit:
 * - Rechtlichen Hinweisen (LegalHintStep),
 * - Eingabemöglichkeit der Telefonnummer (OnboardingPhoneSection),
 * - Fortschrittsanzeige (ProgressSection),
 * - und einem Header (OnboardingHeader).
 *
 * @param onFinish Callback, der ausgeführt wird, wenn das Onboarding abgeschlossen ist.
 * @param viewModel Das ViewModel zur Steuerung des Onboardingprozesses (Standard: Koin Injection).
 */
@Composable
fun OnboardingScreen(
    onFinish: () -> Unit,
    viewModel: OnboardingViewModel = koinViewModel()
) {
    val context = LocalContext.current
    val legalHints = viewModel.getLegalHints(context)
    val keyboardController = LocalSoftwareKeyboardController.current
    val allHintsDone = viewModel.allHintsAcknowledged
    val isSaving = viewModel.isSaving
    val phoneNumber = viewModel.phoneNumber
    val errorMessage = viewModel.errorMessage

    // Vertikaler Farbverlauf als Hintergrund
    val backgroundGradient = Brush.verticalGradient(
        colors = listOf(
            MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
            MaterialTheme.colorScheme.background,
            MaterialTheme.colorScheme.secondary.copy(alpha = 0.05f)
        )
    )

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(backgroundGradient)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp),
                verticalArrangement = Arrangement.spacedBy(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                Spacer(modifier = Modifier.height(32.dp))

                // Header mit Titel und Untertitel
                OnboardingHeader(allHintsDone = allHintsDone)

                // Animierter Wechsel zwischen rechtlichen Hinweisen und Telefonnummern-Eingabe
                AnimatedContent(
                    targetState = allHintsDone,
                    transitionSpec = {
                        slideInHorizontally(initialOffsetX = { if (targetState) it else -it }) + fadeIn() togetherWith
                                slideOutHorizontally(targetOffsetX = { if (targetState) -it else it }) + fadeOut()
                    },
                    label = "onboarding_content"
                ) { hintsCompleted ->
                    if (!hintsCompleted) {
                        LegalHintStep(viewModel = viewModel)
                    } else {
                        OnboardingPhoneSection(
                            phoneNumber = phoneNumber,
                            isSaving = isSaving,
                            errorMessage = errorMessage,
                            onPhoneNumberChange = { viewModel.phoneNumber = it },
                            onSave = {
                                keyboardController?.hide()
                                viewModel.savePhoneNumber(context, onFinish)
                            }
                        )
                    }
                }

                Spacer(modifier = Modifier.weight(1f))

                // Fortschrittsanzeige während der rechtlichen Hinweise
                if (!allHintsDone) {
                    ProgressSection(viewModel = viewModel, totalHints = legalHints.size + 1)
                    Spacer(modifier = Modifier.height(16.dp))
                }
            }
        }
    }
}