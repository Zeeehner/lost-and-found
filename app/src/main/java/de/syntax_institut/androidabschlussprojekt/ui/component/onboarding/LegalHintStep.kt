package de.syntax_institut.androidabschlussprojekt.ui.component.onboarding

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import de.syntax_institut.androidabschlussprojekt.ui.viewmodel.OnboardingViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import de.syntax_institut.androidabschlussprojekt.R

@Composable
fun LegalHintStep(viewModel: OnboardingViewModel) {
    val context = androidx.compose.ui.platform.LocalContext.current
    val legalHints = viewModel.getLegalHints(context)
    val currentHint = legalHints.getOrNull(viewModel.currentHintIndex)
    val isAcknowledged = viewModel.hintAcknowledged
    val scope = rememberCoroutineScope()
    val animateVisibility = remember { mutableStateOf(true) }

    LaunchedEffect(viewModel.currentHintIndex) {
        animateVisibility.value = true
    }

    AnimatedContent(
        targetState = currentHint,
        transitionSpec = {
            slideInHorizontally(
                initialOffsetX = { it },
                animationSpec = tween(300)
            ) + fadeIn(animationSpec = tween(300)) togetherWith
                    slideOutHorizontally(
                        targetOffsetX = { -it },
                        animationSpec = tween(300)
                    ) + fadeOut(animationSpec = tween(300))
        },
        label = "hint_transition"
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