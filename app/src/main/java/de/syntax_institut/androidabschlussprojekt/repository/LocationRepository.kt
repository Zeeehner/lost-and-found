package de.syntax_institut.androidabschlussprojekt.repository

import android.Manifest
import android.content.Context
import android.location.Location
import androidx.annotation.RequiresPermission
import de.syntax_institut.androidabschlussprojekt.ui.util.LocationUtils
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume

class LocationRepository(
    private val lostItemRepository: LostItemRepository = LostItemRepository()
) {
    @RequiresPermission(allOf = [Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION])
    suspend fun getLocationWithCity(context: Context): Pair<Location, String?> {
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
}
