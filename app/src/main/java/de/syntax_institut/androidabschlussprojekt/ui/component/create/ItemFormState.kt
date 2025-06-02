package de.syntax_institut.androidabschlussprojekt.ui.component.create

import android.Manifest
import android.content.Context
import android.net.Uri
import androidx.annotation.RequiresPermission
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import de.syntax_institut.androidabschlussprojekt.data.local.model.Item
import de.syntax_institut.androidabschlussprojekt.repository.ItemCreateRepository
import kotlinx.coroutines.tasks.await

/**
 * UI-Statusmodell für das Formular zum Erstellen eines Items.
 *
 * Enthält Felder für alle Eingaben sowie Methoden zur Validierung, Standortermittlung
 * und Umwandlung in ein [Item]-Datenmodell.
 */
data class LostItemFormState(
    val title: String = "",
    val description: String = "",
    val location: String = "",
    val latitude: String = "",
    val longitude: String = "",
    val status: String = "lost",
    val imageUri: Uri? = null,
    val showLocationDetails: Boolean = false
) {

    /**
     * Prüft, ob alle Pflichtfelder gefüllt und ein Bild vorhanden ist.
     *
     * @param bitmap Das ausgewählte Bild.
     * @return `true`, wenn alle Felder valide sind.
     */
    fun isValid(bitmap: android.graphics.Bitmap?): Boolean {
        return title.isNotBlank() && title.length >= 3 &&
                description.isNotBlank() &&
                location.isNotBlank() &&
                bitmap != null
    }

    /**
     * Holt den aktuellen Standort des Geräts und ergänzt ihn mit einem Ortsnamen.
     *
     * @param context Anwendungskontext.
     * @param repository Repository zur Ortsauflösung (Reverse-Geocoding).
     * @return Eine neue Instanz mit aktualisierten Koordinaten und Ortsname.
     */
    @RequiresPermission(allOf = [Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION])
    suspend fun setCurrentLocation(context: Context, repository: ItemCreateRepository): LostItemFormState {
        return try {
            val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)
            val locationResult = fusedLocationClient
                .getCurrentLocation(Priority.PRIORITY_HIGH_ACCURACY, null)
                .await()

            locationResult?.let {
                val lat = it.latitude.toString()
                val lon = it.longitude.toString()
                val locationName = repository.getLocationName(it.latitude, it.longitude) ?: "Unknown Place"

                println("Ort aus API: $locationName")

                copy(latitude = lat, longitude = lon, location = locationName)
            } ?: this
        } catch (e: Exception) {
            e.printStackTrace()
            this
        }
    }

    /**
     * Führt eine Forward-Geocoding-Anfrage basierend auf dem eingegebenen Ort durch.
     *
     * @param repository Repository zur Koordinatenbestimmung.
     * @return Eine neue Instanz mit aktualisierten Koordinaten.
     */
    suspend fun updateCoordinatesFromLocation(repository: ItemCreateRepository): LostItemFormState {
        return try {
            val coords = repository.getCoordinatesForLocation(location)
            coords?.let {
                copy(latitude = it.first.toString(), longitude = it.second.toString())
            } ?: this
        } catch (e: Exception) {
            e.printStackTrace()
            this
        }
    }

    /**
     * Wandelt den aktuellen Formularzustand in ein persistierbares [Item] um.
     *
     * @param userId ID des Erstellers.
     * @param userName Name des Erstellers.
     * @return Neues [Item] mit allen eingegebenen Daten.
     */
    fun toLostItem(userId: String, userName: String): Item {
        return Item(
            userId = userId,
            userName = userName,
            title = title,
            description = description,
            status = status,
            locationName = location,
            latitude = latitude.toDoubleOrNull() ?: 0.0,
            longitude = longitude.toDoubleOrNull() ?: 0.0
        )
    }
}