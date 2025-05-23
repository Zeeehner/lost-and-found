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
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import de.syntax_institut.androidabschlussprojekt.ui.viewmodel.SettingsViewModel
import de.syntax_institut.androidabschlussprojekt.R

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
                        viewModel.requestNotificationPermission(context, notificationPermissionLauncher)
                    },
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.secondaryContainer,
                        contentColor = MaterialTheme.colorScheme.onSecondaryContainer
                    ),
                    modifier = Modifier.padding(start = 4.dp)
                ) {
                    Icon(
                        imageVector = Icons.Rounded.Notifications,
                        contentDescription = null,
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