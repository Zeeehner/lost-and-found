package de.syntax_institut.androidabschlussprojekt.repository

import com.google.firebase.firestore.FirebaseFirestore
import de.syntax_institut.androidabschlussprojekt.data.local.model.ChatMessage
import kotlinx.coroutines.tasks.await
import java.util.UUID
import kotlin.jvm.java

class ChatRepository {
    private val firestore = FirebaseFirestore.getInstance()

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