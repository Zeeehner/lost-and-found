package de.syntax_institut.androidabschlussprojekt.repository

import android.graphics.Bitmap
import android.util.Base64
import de.syntax_institut.androidabschlussprojekt.BuildConfig
import com.google.firebase.firestore.FirebaseFirestore
import de.syntax_institut.androidabschlussprojekt.data.local.model.Item
import de.syntax_institut.androidabschlussprojekt.data.remote.api.GeocodingApiClient
import java.io.ByteArrayOutputStream
import java.util.UUID

/**
 * Repository zur Verwaltung verlorener Gegenstände.
 * Beinhaltet Funktionen zum Speichern, Abrufen, Löschen und zur Standortauflösung.
 */
class LostItemRepository {

    private val firestore = FirebaseFirestore.getInstance()

    /**
     * Fügt ein verlorenes Item in Firestore hinzu (optional mit Bild als Base64).
     *
     * @param item Das Item-Objekt.
     * @param bitmap Optionales Bild zum Item.
     * @param onResult Callback mit `true`, wenn erfolgreich gespeichert.
     */
    fun addLostItem(item: Item, bitmap: Bitmap?, onResult: (Boolean) -> Unit) {
        val itemWithImage = if (bitmap != null) {
            val stream = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.JPEG, 80, stream)
            val base64Image = Base64.encodeToString(stream.toByteArray(), Base64.DEFAULT)
            item.copy(imageUrl = base64Image)
        } else {
            item
        }

        val documentId = UUID.randomUUID().toString()

        firestore.collection("lost_items")
            .document(documentId)
            .set(itemWithImage.copy(id = documentId))
            .addOnSuccessListener { onResult(true) }
            .addOnFailureListener { onResult(false) }
    }

    /**
     * Beobachtet die Sammlung verlorener Items und liefert bei Änderungen eine Liste.
     *
     * @param onResult Callback mit aktueller Liste der Items.
     */
    fun getLostItems(onResult: (List<Item>) -> Unit) {
        firestore.collection("lost_items")
            .addSnapshotListener { snapshot, _ ->
                if (snapshot != null) {
                    val items = snapshot.documents.mapNotNull { it.toObject(Item::class.java) }
                    onResult(items)
                } else {
                    onResult(emptyList())
                }
            }
    }

    /**
     * Holt die geografischen Koordinaten zu einem gegebenen Ortsnamen.
     *
     * @param locationName Ortsname (z. B. „Berlin“).
     * @return Ein Paar aus (Breitengrad, Längengrad) oder `null`.
     */
    suspend fun getCoordinatesForLocation(locationName: String): Pair<Double, Double>? {
        return try {
            val apiKey = BuildConfig.OPENCAGE_API_KEY
            val response = GeocodingApiClient.api.forwardGeocode(locationName, apiKey)
            val result = response.results.firstOrNull()
            val lat = result?.geometry?.lat
            val lng = result?.geometry?.lng
            if (lat != null && lng != null) Pair(lat, lng) else null
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    /**
     * Holt einen Ortsnamen zu den gegebenen Koordinaten (Reverse-Geocoding).
     *
     * @param lat Breitengrad.
     * @param lon Längengrad.
     * @return Ortsname oder `null`, wenn nicht ermittelbar.
     */
    suspend fun getLocationName(lat: Double, lon: Double): String? {
        return try {
            val apiKey = BuildConfig.OPENCAGE_API_KEY
            val response = GeocodingApiClient.api.reverseGeocode("$lat,$lon", apiKey)
            val result = response.results.firstOrNull()

            println("🧩 Components: ${result?.components}")
            println("🧩 Formatted: ${result?.formatted}")

            val name = result?.components?.city
                ?: result?.components?.town
                ?: result?.components?.village
                ?: result?.components?.state
                ?: result?.components?.country
                ?: result?.formatted

            println("🌍 Name gefunden: $name")
            name
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    /**
     * Löscht ein Item aus Firestore anhand seiner ID.
     *
     * @param itemId Die ID des zu löschenden Items.
     */
    fun deleteLostItem(itemId: String) {
        firestore.collection("lost_items")
            .document(itemId)
            .delete()
    }
}