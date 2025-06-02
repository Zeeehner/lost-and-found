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

/**
 * Utility-Objekt zur Handhabung von Berechtigungen (Permissions).
 */
object PermissionUtils {

    /**
     * Prüft eine einzelne Berechtigung und führt die Aktion aus, wenn die Berechtigung erteilt ist.
     * Zeigt gegebenenfalls eine Berechtigungsbegründung oder startet die Berechtigungsanfrage.
     *
     * @param context Context zur Prüfung der Berechtigung.
     * @param requestSinglePermissionLauncher Launcher zur Anforderung der Berechtigung.
     * @param permission Die abzufragende Berechtigung.
     * @param action Aktion, die ausgeführt wird, wenn die Berechtigung vorhanden ist.
     * @param showRationale Aktion, die ausgeführt wird, wenn eine Berechtigungsbegründung angezeigt werden soll.
     */
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

    /**
     * Prüft mehrere Berechtigungen und führt die Aktion aus, wenn alle Berechtigungen erteilt sind.
     * Zeigt gegebenenfalls eine Berechtigungsbegründung oder startet die Berechtigungsanfrage.
     *
     * @param context Context zur Prüfung der Berechtigungen.
     * @param requestMultiplePermissionLauncher Launcher zur Anforderung der Berechtigungen.
     * @param permissions Liste der abzufragenden Berechtigungen.
     * @param action Aktion, die ausgeführt wird, wenn alle Berechtigungen vorhanden sind.
     * @param showRationale Aktion, die ausgeführt wird, wenn eine Berechtigungsbegründung angezeigt werden soll.
     */
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

    /**
     * Spezielle Berechtigungsanfrage für die Benachrichtigungsberechtigung (POST_NOTIFICATIONS) ab Android 13 (API 33).
     *
     * @param context Context zur Prüfung der Berechtigung.
     * @param requestPermissionLauncher Launcher zur Anforderung der Berechtigung.
     * @param showRationale Aktion, die ausgeführt wird, wenn eine Berechtigungsbegründung angezeigt werden soll.
     * @param onGranted Aktion, die ausgeführt wird, wenn die Berechtigung erteilt wurde.
     */
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