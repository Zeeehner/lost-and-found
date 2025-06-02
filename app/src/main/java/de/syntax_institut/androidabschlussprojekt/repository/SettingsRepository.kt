package de.syntax_institut.androidabschlussprojekt.repository

import android.Manifest
import android.content.Context
import android.location.Location
import androidx.annotation.RequiresPermission
import de.syntax_institut.androidabschlussprojekt.ui.util.LocationUtils
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume

/**
 * Repository für die Einstellungen, insbesondere Standort- und Benachrichtigungsfunktionen.
 */
class SettingsRepository {

    private val lostItemRepository = LostItemRepository()

    /**
     * Ermittelt den aktuellen Standort und dazugehörigen Ortsnamen.
     *
     * @param context Der Anwendungskontext.
     * @return Ein Paar aus [Location] und Ortsname (oder `null`, falls unbekannt).
     *
     * Erfordert Laufzeitberechtigungen für Standortzugriff.
     */
    @RequiresPermission(allOf = [Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION])
    suspend fun getLocation(context: Context): Pair<Location, String?> {
        val location = suspendCancellableCoroutine<Location> { cont ->
            LocationUtils.getLocation(context) { loc ->
                cont.resume(loc)
            }
        }

        val cityName = try {
            lostItemRepository.getLocationName(location.latitude, location.longitude)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }

        return location to cityName
    }

    /**
     * Platzhalter für das Speichern der Benachrichtigungs-Einstellung.
     * Zur Umsetzung wird empfohlen, Jetpack DataStore zu verwenden.
     *
     * @param context Kontext der App.
     * @param enabled `true`, wenn Benachrichtigungen aktiviert sein sollen.
     */
    fun saveNotificationEnabled(context: Context, enabled: Boolean) {
        // TODO: Mit Jetpack DataStore oder Room persistieren
        // DataStore.writeBoolean("notifications_enabled", enabled)
    }
}