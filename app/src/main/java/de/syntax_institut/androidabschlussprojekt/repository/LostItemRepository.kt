package de.syntax_institut.androidabschlussprojekt.repository

import android.graphics.Bitmap
import android.util.Base64
import de.syntax_institut.androidabschlussprojekt.BuildConfig
import com.google.firebase.firestore.FirebaseFirestore
import de.syntax_institut.androidabschlussprojekt.data.local.Item
import de.syntax_institut.androidabschlussprojekt.data.remote.api.GeocodingApiClient
import java.io.ByteArrayOutputStream
import java.util.UUID
import kotlin.jvm.java

class LostItemRepository {

    private val firestore = FirebaseFirestore.getInstance()

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

    suspend fun getCoordinatesForLocation(locationName: String): Pair<Double, Double>? {
        return try {
            val apiKey = BuildConfig.OPENCAGE_API_KEY

            val response = GeocodingApiClient.api.forwardGeocode(
                locationName,
                apiKey
            )

            val result = response.results.firstOrNull()
            val lat = result?.geometry?.lat
            val lng = result?.geometry?.lng

            if (lat != null && lng != null) {
                Pair(lat, lng)
            } else {
                null
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }



    suspend fun getLocationName(lat: Double, lon: Double): String? {
        return try {
            val apiKey = BuildConfig.OPENCAGE_API_KEY
            val response = GeocodingApiClient.api.reverseGeocode(
                "$lat,$lon",
                apiKey
            )

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

    fun deleteLostItem(itemId: String) {
        firestore.collection("lost_items").document(itemId).delete()
    }
}
