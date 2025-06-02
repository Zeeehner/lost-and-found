package de.syntax_institut.androidabschlussprojekt.repository

import com.google.firebase.firestore.FirebaseFirestore
import de.syntax_institut.androidabschlussprojekt.data.local.model.ChatMessage
import kotlinx.coroutines.tasks.await
import java.util.UUID

/**
 * Repository zur Verwaltung von Chatnachrichten innerhalb eines Item-Chats.
 * Kommuniziert mit Firebase Firestore.
 */
class ChatRepository {

    private val firestore = FirebaseFirestore.getInstance()

    /**
     * Holt alle Chatnachrichten zu einem bestimmten Item sortiert nach Zeitstempel.
     *
     * @param itemId ID des Items, zu dem der Chat gehört.
     * @return Liste der Nachrichten oder eine leere Liste bei Fehlern.
     */
    suspend fun getMessages(itemId: String): List<ChatMessage> {
        return try {
            val snapshot = firestore.collection("lost_items")
                .document(itemId)
                .collection("chat")
                .orderBy("timestamp")
                .get()
                .await()
            snapshot.toObjects(ChatMessage::class.java)
        } catch (e: Exception) {
            emptyList()
        }
    }

    /**
     * Sendet eine neue Nachricht in den Item-Chat. Generiert eine ID, falls keine vorhanden ist.
     *
     * @param itemId ID des zugehörigen Items.
     * @param message Die zu sendende [ChatMessage].
     */
    suspend fun sendMessage(itemId: String, message: ChatMessage) {
        val docId = message.id.ifEmpty { UUID.randomUUID().toString() }
        firestore.collection("lost_items")
            .document(itemId)
            .collection("chat")
            .document(docId)
            .set(message.copy(id = docId))
            .await()
    }
}