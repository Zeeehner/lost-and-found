package de.syntax_institut.androidabschlussprojekt.repository

import com.google.firebase.firestore.FirebaseFirestore
import de.syntax_institut.androidabschlussprojekt.data.local.model.Item
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

/**
 * Repository zur Bereitstellung eines kontinuierlichen Datenstroms (Flow) von verlorenen Items für die Karte.
 */
class MapRepository {

    private val firestore = FirebaseFirestore.getInstance()

    /**
     * Gibt einen [Flow] mit allen verlorenen Items aus Firestore zurück.
     * Aktualisiert sich automatisch bei Änderungen in der Datenbank.
     *
     * @return Ein Flow mit einer Liste von [Item]-Objekten.
     */
    fun getLostItemsFlow(): Flow<List<Item>> = callbackFlow {
        val listener = firestore.collection("lost_items")
            .addSnapshotListener { snapshot, _ ->
                val items = snapshot?.documents?.mapNotNull {
                    it.toObject(Item::class.java)
                } ?: emptyList()
                trySend(items).isSuccess
            }

        awaitClose { listener.remove() }
    }
}