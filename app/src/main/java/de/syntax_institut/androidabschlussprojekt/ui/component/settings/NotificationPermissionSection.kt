package de.syntax_institut.androidabschlussprojekt.ui.component.settings

import android.content.Context
import android.os.Build
import androidx.activity.result.ActivityResultLauncher
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.rounded.Notifications
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import de.syntax_institut.androidabschlussprojekt.R
import de.syntax_institut.androidabschlussprojekt.ui.viewmodel.SettingsViewModel

/**
 * Zeigt eine Sektion zur Anfrage der Benachrichtigungsberechtigung (nur Android 13+).
 */
@Composable
fun NotificationPermissionSection(
    viewModel: SettingsViewModel,
    context: Context,
    notificationPermissionLauncher: ActivityResultLauncher<String>
) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        SettingsItem(
            icon = Icons.Outlined.Notifications,
            title = stringResource(R.string.notification_permission),
            subtitle = stringResource(R.string.notification_permission_sub),
            trailingContent = {
                Button(
                    onClick = {
                        viewModel.requestNotificationPermission(
                            context = context,
                            launcher = notificationPermissionLauncher
                        )
                    },
                    modifier = Modifier.padding(start = 4.dp),
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.secondaryContainer,
                        contentColor = MaterialTheme.colorScheme.onSecondaryContainer
                    )
                ) {
                    Icon(
                        imageVector = Icons.Rounded.Notifications,
                        contentDescription = stringResource(R.string.notification_permission),
                        modifier = Modifier.size(18.dp)
                    )
                    Text(
                        text = stringResource(R.string.allow),
                        modifier = Modifier.padding(start = 4.dp)
                    )
                }
            }
        )
    }
}