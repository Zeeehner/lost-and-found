package de.syntax_institut.androidabschlussprojekt.repository

import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import de.syntax_institut.androidabschlussprojekt.data.local.model.ChatPartner
import de.syntax_institut.androidabschlussprojekt.data.local.model.PrivateChatMessage
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import java.util.*
import kotlin.jvm.java

class PrivateChatRepository {

    private val firestore = FirebaseFirestore.getInstance()
    private var messageListener: ListenerRegistration? = null

    private fun getChatId(userA: String, userB: String): String {
        return listOf(userA, userB).sorted().joinToString("_")
    }

    fun observeMessages(
        currentUserId: String,
        partnerId: String,
        onResult: (List<PrivateChatMessage>) -> Unit
    ) {
        val chatId = getChatId(currentUserId, partnerId)
        messageListener?.remove()

        messageListener = firestore.collection("private_chats")
            .document(chatId)
            .collection("messages")
            .orderBy("timestamp")
            .addSnapshotListener { snapshot, _ ->
                val messages = snapshot?.toObjects(PrivateChatMessage::class.java) ?: emptyList()
                onResult(messages)
            }
    }

    fun sendMessage(message: PrivateChatMessage, senderName: String, receiverName: String) {
        val chatId = getChatId(message.senderId, message.receiverId)
        val docId = UUID.randomUUID().toString()
        val fullMessage = message.copy(id = docId)

        // 1. Nachricht speichern
        firestore.collection("private_chats")
            .document(chatId)
            .collection("messages")
            .document(docId)
            .set(fullMessage)

        // 2. ChatPartner für Sender aktualisieren
        val senderPartner = ChatPartner(
            userId = message.receiverId,
            userName = receiverName,
            lastMessage = message.message,
        )
        firestore.collection("chat_partners")
            .document(message.senderId)
            .collection("partners")
            .document(message.receiverId)
            .set(senderPartner)

        // 3. ChatPartner für Empfänger aktualisieren
        val receiverPartner = ChatPartner(
            userId = message.senderId,
            userName = senderName,
            lastMessage = message.message,
        )
        firestore.collection("chat_partners")
            .document(message.receiverId)
            .collection("partners")
            .document(message.senderId)
            .set(receiverPartner)
    }

    fun getChatPartners(currentUserId: String): Flow<List<ChatPartner>> = callbackFlow {
        val listener = firestore.collection("chat_partners")
            .document(currentUserId)
            .collection("partners")
            .addSnapshotListener { snapshot, _ ->
                Log.d("PrivateChatRepository", "Snapshot received: ${snapshot?.documents}")
                val list = snapshot?.toObjects(ChatPartner::class.java) ?: emptyList()
                Log.d("PrivateChatRepository", "List: $list")
                trySend(list).isSuccess
            }

        awaitClose {
            listener.remove()
        }
    }

    fun stopObserving() {
        messageListener?.remove()
        messageListener = null
    }
}