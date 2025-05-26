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
    fun isValid(bitmap: android.graphics.Bitmap?): Boolean {
        return title.isNotBlank() && title.length >= 3 &&
                description.isNotBlank() &&
                location.isNotBlank() &&
                bitmap != null
    }

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