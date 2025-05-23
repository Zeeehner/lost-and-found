package de.syntax_institut.androidabschlussprojekt.repository

import android.graphics.Bitmap
import de.syntax_institut.androidabschlussprojekt.data.local.Item
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume

class ItemCreateRepository(
    private val LostItemRepository: LostItemRepository = LostItemRepository()
) {
    suspend fun getLocationName(lat: Double, lon: Double): String? {
        return LostItemRepository.getLocationName(lat, lon)
    }

    suspend fun addLostItem(item: Item, bitmap: Bitmap?): Boolean {
        return suspendCancellableCoroutine { cont ->
            LostItemRepository.addLostItem(item, bitmap) { success ->
                cont.resume(success)
            }
        }
    }

    suspend fun getCoordinatesForLocation(location: String): Pair<Double, Double>? {
        return LostItemRepository.getCoordinatesForLocation(location)
    }
}