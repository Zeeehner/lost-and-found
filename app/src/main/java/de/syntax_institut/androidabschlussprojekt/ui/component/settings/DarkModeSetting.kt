package de.syntax_institut.androidabschlussprojekt.ui.component.settings

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Build
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import de.syntax_institut.androidabschlussprojekt.R

@Composable
fun DarkModeSetting(
    darkMode: Boolean,
    onToggle: () -> Unit,
    icon: ImageVector = Icons.Rounded.Build
) {
    SettingsItem(
        icon = icon,
        title = stringResource(R.string.dark_mode),
        subtitle = stringResource(R.string.dark_mode_sub),
        trailingContent = {
            Switch(
                checked = darkMode,
                onCheckedChange = { onToggle() },
                colors = SwitchDefaults.colors(
                    checkedThumbColor = MaterialTheme.colorScheme.primary,
                    checkedTrackColor = MaterialTheme.colorScheme.primaryContainer,
                    checkedIconColor = MaterialTheme.colorScheme.onPrimary,
                    uncheckedThumbColor = MaterialTheme.colorScheme.outline,
                    uncheckedTrackColor = MaterialTheme.colorScheme.surfaceVariant,
                    uncheckedIconColor = MaterialTheme.colorScheme.onSurfaceVariant
                )
            )
        }
    )
}