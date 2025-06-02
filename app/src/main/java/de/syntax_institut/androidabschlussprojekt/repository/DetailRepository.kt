package de.syntax_institut.androidabschlussprojekt.repository

import com.google.firebase.firestore.FirebaseFirestore
import de.syntax_institut.androidabschlussprojekt.data.local.model.ChatMessage
import de.syntax_institut.androidabschlussprojekt.data.local.model.Item
import java.util.UUID

/**
 * Repository für Detailansichten von Items.
 * Beinhaltet Logik zur Item-Abfrage sowie Chat-Funktionalität innerhalb eines Items.
 */
class DetailRepository {

    private val firestore = FirebaseFirestore.getInstance()

    /**
     * Holt ein bestimmtes Item aus Firestore anhand seiner ID.
     *
     * @param itemId Die ID des gesuchten Items.
     * @param onResult Callback mit dem gefundenen Item oder `null` bei Fehler.
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
     * Beobachtet den Chatverlauf für ein bestimmtes Item in Echtzeit.
     *
     * @param itemId Die ID des Items.
     * @param onResult Callback mit der aktuellen Liste an [ChatMessage]s.
     */
    fun observeChatMessages(itemId: String, onResult: (List<ChatMessage>) -> Unit) {
        firestore.collection("lost_items")
            .document(itemId)
            .collection("chat")
            .orderBy("timestamp")
            .addSnapshotListener { snapshot, _ ->
                val messages = snapshot?.toObjects(ChatMessage::class.java) ?: emptyList()
                onResult(messages)
            }
    }

    /**
     * Sendet eine neue Chatnachricht zum Item. Erstellt automatisch eine ID, falls keine gesetzt ist.
     *
     * @param itemId Die ID des zugehörigen Items.
     * @param chat Die zu sendende [ChatMessage].
     */
    fun sendChatMessage(itemId: String, chat: ChatMessage) {
        val docId = chat.id.ifEmpty { UUID.randomUUID().toString() }
        firestore.collection("lost_items")
            .document(itemId)
            .collection("chat")
            .document(docId)
            .set(chat.copy(id = docId))
    }
}