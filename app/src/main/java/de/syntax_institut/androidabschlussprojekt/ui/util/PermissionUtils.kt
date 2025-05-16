package de.syntax_institut.androidabschlussprojekt.ui.util

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.result.ActivityResultLauncher
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat.shouldShowRequestPermissionRationale
import androidx.core.content.ContextCompat

object PermissionUtils {

    fun handleActionWithPermission(
        context: Context,
        requestSinglePermissionLauncher: ActivityResultLauncher<String>,
        permission: String,
        action: () -> Unit,
        showRationale: () -> Unit
    ) {
        when {
            ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED -> {
                action()
            }
            (context as? Activity)?.let {
                shouldShowRequestPermissionRationale(it, permission)
            } == true -> {
                showRationale()
            }
            else -> {
                requestSinglePermissionLauncher.launch(permission)
            }
        }
    }

    fun handleActionWithPermissions(
        context: Context,
        requestMultiplePermissionLauncher: ActivityResultLauncher<Array<String>>,
        permissions: List<String>,
        action: () -> Unit,
        showRationale: () -> Unit
    ) {
        val missingPermissions = permissions.filter {
            ContextCompat.checkSelfPermission(context, it) != PackageManager.PERMISSION_GRANTED
        }

        when {
            missingPermissions.isEmpty() -> {
                action()
            }
            (context as? Activity)?.let { activity ->
                missingPermissions.any { shouldShowRequestPermissionRationale(activity, it) }
            } == true -> {
                showRationale()
            }
            else -> {
                requestMultiplePermissionLauncher.launch(missingPermissions.toTypedArray())
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    fun handleNotificationPermission(
        context: Context,
        requestPermissionLauncher: ActivityResultLauncher<String>,
        showRationale: () -> Unit,
        onGranted: () -> Unit
    ) {
        val permission = Manifest.permission.POST_NOTIFICATIONS

        when {
            ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED -> {
                onGranted()
            }
            (context as? Activity)?.let {
                shouldShowRequestPermissionRationale(it, permission)
            } == true -> {
                showRationale()
            }
            else -> {
                requestPermissionLauncher.launch(permission)
            }
        }
    }
}
