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

/**
 * Repository für private 1:1-Chatfunktionen.
 * Beinhaltet Echtzeitnachrichten, Partnerlisten, letzte Aktivitäten und Benachrichtigungszustände.
 */
class PrivateChatRepository {

    private val firestore = FirebaseFirestore.getInstance()
    private var messageListener: ListenerRegistration? = null

    /**
     * Liefert eine deterministische ID für eine private Chat-Konversation zwischen zwei Usern.
     */
    private fun getChatId(userA: String, userB: String): String {
        return listOf(userA, userB).sorted().joinToString("_")
    }

    /**
     * Beobachtet Nachrichten in einem privaten Chat zwischen zwei Usern.
     *
     * @param currentUserId Aktuelle User-ID.
     * @param partnerId ID des Chatpartners.
     * @param onResult Callback mit der Liste von Nachrichten.
     */
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

    /**
     * Sendet eine private Nachricht und aktualisiert ChatPartner-Daten auf beiden Seiten.
     *
     * @param message Die zu sendende Nachricht.
     * @param senderName Anzeigename des Senders.
     * @param receiverName Anzeigename des Empfängers.
     */
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
            lastMessageTime = message.timestamp,
            unreadCount = 0,
            lastSeen = System.currentTimeMillis()
        )
        firestore.collection("chat_partners")
            .document(message.senderId)
            .collection("partners")
            .document(message.receiverId)
            .set(senderPartner)

        // 3. ChatPartner für Empfänger aktualisieren (Unread +1)
        val receiverDocRef = firestore.collection("chat_partners")
            .document(message.receiverId)
            .collection("partners")
            .document(message.senderId)

        receiverDocRef.get().addOnSuccessListener { doc ->
            val currentUnread = doc.toObject(ChatPartner::class.java)?.unreadCount ?: 0
            val receiverPartner = ChatPartner(
                userId = message.senderId,
                userName = senderName,
                lastMessage = message.message,
                lastMessageTime = message.timestamp,
                unreadCount = currentUnread + 1
            )
            receiverDocRef.set(receiverPartner)
        }
    }

    /**
     * Gibt eine kontinuierlich aktualisierte Liste der Chat-Partner des aktuellen Nutzers zurück.
     *
     * @param currentUserId Die aktuelle User-ID.
     * @return Flow mit Liste der ChatPartner.
     */
    fun getChatPartners(currentUserId: String): Flow<List<ChatPartner>> = callbackFlow {
        val listener = firestore.collection("chat_partners")
            .document(currentUserId)
            .collection("partners")
            .addSnapshotListener { snapshot, _ ->
                val list = snapshot?.toObjects(ChatPartner::class.java) ?: emptyList()
                trySend(list).isSuccess
            }
        awaitClose { listener.remove() }
    }

    /**
     * Holt den Zeitstempel, zu dem der Chatpartner zuletzt online war.
     *
     * @param currentUserId Aktueller Benutzer.
     * @param partnerId ID des Chatpartners.
     * @param onResult Callback mit Unix-Timestamp.
     */
    fun getUserLastSeen(currentUserId: String, partnerId: String, onResult: (Long) -> Unit) {
        firestore.collection("chat_partners")
            .document(currentUserId)
            .collection("partners")
            .document(partnerId)
            .addSnapshotListener { snapshot, _ ->
                val lastSeen = snapshot?.getLong("lastSeen") ?: 0L
                onResult(lastSeen)
            }
    }

    /**
     * Aktualisiert den Zeitstempel, wann ein Nutzer zuletzt aktiv war.
     *
     * @param userId ID des Nutzers.
     */
    fun updateLastSeen(userId: String) {
        firestore.collection("users")
            .document(userId)
            .update("lastSeen", System.currentTimeMillis())
    }

    /**
     * Setzt den Zähler ungelesener Nachrichten eines Chatpartners zurück.
     *
     * @param currentUserId Aktueller Benutzer.
     * @param partnerId ID des Chatpartners.
     */
    fun resetUnreadCount(currentUserId: String, partnerId: String) {
        firestore.collection("chat_partners")
            .document(currentUserId)
            .collection("partners")
            .document(partnerId)
            .update("unreadCount", 0)
    }

    /**
     * Entfernt alle aktiven Listener für Chatnachrichten.
     * Sollte z. B. beim Verlassen eines Screens aufgerufen werden.
     */
    fun stopObserving() {
        messageListener?.remove()
        messageListener = null
    }
}