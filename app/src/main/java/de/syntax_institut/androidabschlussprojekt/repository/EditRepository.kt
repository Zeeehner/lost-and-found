package de.syntax_institut.androidabschlussprojekt.repository

import com.google.firebase.firestore.FirebaseFirestore
import de.syntax_institut.androidabschlussprojekt.data.local.model.Item
import kotlin.jvm.java

class EditRepository {

    private val firestore = FirebaseFirestore.getInstance()

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