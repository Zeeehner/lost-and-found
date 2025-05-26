package de.syntax_institut.androidabschlussprojekt.repository

import com.google.firebase.firestore.FirebaseFirestore
import de.syntax_institut.androidabschlussprojekt.data.local.model.ChatMessage
import de.syntax_institut.androidabschlussprojekt.data.local.model.Item
import java.util.UUID
import kotlin.jvm.java

class DetailRepository {

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

    fun sendChatMessage(itemId: String, chat: ChatMessage) {
        val docId = chat.id.ifEmpty { UUID.randomUUID().toString() }
        firestore.collection("lost_items")
            .document(itemId)
            .collection("chat")
            .document(docId)
            .set(chat.copy(id = docId))
    }
}