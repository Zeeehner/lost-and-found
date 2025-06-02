package de.syntax_institut.androidabschlussprojekt.repository

import android.graphics.Bitmap
import de.syntax_institut.androidabschlussprojekt.data.local.model.Item
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume

/**
 * Repository für die Erstellung neuer Items.
 * Nutzt [LostItemRepository] für tatsächliche Datenoperationen.
 */
class ItemCreateRepository(
    private val lostItemRepository: LostItemRepository = LostItemRepository()
) {

    /**
     * Ermittelt den Ortsnamen basierend auf Koordinaten.
     *
     * @param lat Breitengrad.
     * @param lon Längengrad.
     * @return Name des Ortes oder `null`, wenn nicht ermittelbar.
     */
    suspend fun getLocationName(lat: Double, lon: Double): String? {
        return lostItemRepository.getLocationName(lat, lon)
    }

    /**
     * Fügt ein neues Item (optional mit Bild) hinzu.
     *
     * @param item Das zu speichernde [Item].
     * @param bitmap Optionales Bild zum Item.
     * @return `true`, wenn das Hinzufügen erfolgreich war.
     */
    suspend fun addLostItem(item: Item, bitmap: Bitmap?): Boolean {
        return suspendCancellableCoroutine { cont ->
            lostItemRepository.addLostItem(item, bitmap) { success ->
                cont.resume(success)
            }
        }
    }

    /**
     * Holt die geografischen Koordinaten für eine Ortsbezeichnung.
     *
     * @param location Der Ortsname (z. B. „Berlin“).
     * @return Breiten- und Längengrad oder `null`, wenn nicht gefunden.
     */
    suspend fun getCoordinatesForLocation(location: String): Pair<Double, Double>? {
        return lostItemRepository.getCoordinatesForLocation(location)
    }
}