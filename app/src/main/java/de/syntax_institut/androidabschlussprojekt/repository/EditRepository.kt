package de.syntax_institut.androidabschlussprojekt.repository

import com.google.firebase.firestore.FirebaseFirestore
import de.syntax_institut.androidabschlussprojekt.data.local.model.Item

/**
 * Repository zur Bearbeitung von Items.
 * Beinhaltet das Abrufen und Aktualisieren einzelner Items in Firestore.
 */
class EditRepository {

    private val firestore = FirebaseFirestore.getInstance()

    /**
     * Holt ein Item anhand der ID aus Firestore.
     *
     * @param itemId Die ID des Items.
     * @param onResult Callback mit dem Item oder `null`, falls nicht gefunden oder Fehler.
     */
    fun getLostItemById(itemId: String, onResult: (Item?) -> Unit) {
        firestore.collection("lost_items")
            .document(itemId)
            .get()
            .addOnSuccessListener { snapshot ->
                val item = snapshot.toObject(Item::class.java)
                onResult(item)
            }
            .addOnFailureListener {
                onResult(null)
            }
    }

    /**
     * Aktualisiert ein bestehendes Item in Firestore.
     *
     * @param item Das zu speichernde Item.
     * @param onSuccess Callback bei erfolgreichem Speichern.
     * @param onFailure Callback bei Fehlschlag.
     */
    fun updateItem(
        item: Item,
        onSuccess: () -> Unit,
        onFailure: () -> Unit
    ) {
        firestore.collection("lost_items")
            .document(item.id)
            .set(item)
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { onFailure() }
    }
}