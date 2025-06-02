package de.syntax_institut.androidabschlussprojekt.ui.util

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * Datenklasse, die Standardabstände (Spacing) für die UI definiert.
 *
 * @property extraSmall Sehr kleiner Abstand (4.dp)
 * @property small Kleiner Abstand (8.dp)
 * @property medium Mittlerer Abstand (16.dp)
 * @property large Großer Abstand (24.dp)
 * @property extraLarge Sehr großer Abstand (32.dp)
 */
data class Spacing(
    val extraSmall: Dp = 4.dp,
    val small: Dp = 8.dp,
    val medium: Dp = 16.dp,
    val large: Dp = 24.dp,
    val extraLarge: Dp = 32.dp
)

/**
 * CompositionLocal, das den aktuellen Spacing-Wert hält.
 */
val LocalSpacing = compositionLocalOf { Spacing() }

/**
 * Erweiterung auf [MaterialTheme], um den aktuellen [Spacing]-Wert bequem abzurufen.
 */
val MaterialTheme.spacing: Spacing
    @Composable
    @ReadOnlyComposable
    get() = LocalSpacing.current