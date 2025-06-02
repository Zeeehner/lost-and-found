package de.syntax_institut.androidabschlussprojekt.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import de.syntax_institut.androidabschlussprojekt.data.local.model.ChatPartner
import de.syntax_institut.androidabschlussprojekt.data.local.model.PrivateChatMessage
import de.syntax_institut.androidabschlussprojekt.repository.PrivateChatRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

/**
 * ViewModel für private Chats.
 *
 * Verwaltert Chat-Nachrichten, Chat-Partner und Benachrichtigungszähler.
 *
 * @property repository Repository für Chat-Daten.
 */
class PrivateChatViewModel(
    private val repository: PrivateChatRepository
) : ViewModel() {

    private val _messages = MutableStateFlow<List<PrivateChatMessage>>(emptyList())
    /** Liste aller Chat-Nachrichten mit dem aktuellen Chat-Partner */
    val messages: StateFlow<List<PrivateChatMessage>> = _messages

    private val _chatPartners = MutableStateFlow<List<ChatPartner>>(emptyList())
    /** Liste aller Chat-Partner */
    val chatPartners: StateFlow<List<ChatPartner>> = _chatPartners

    /** Anzahl der ungelesenen Nachrichten über alle Chats */
    val unreadCount: StateFlow<Int> = chatPartners
        .map { list -> list.sumOf { it.unreadCount } }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    /**
     * Aktualisiert den Zeitstempel des letzten Sichtbarkeitszeitpunkts des aktuellen Nutzers.
     *
     * @param currentUserId Id des aktuellen Nutzers.
     */
    fun updateMyLastSeen(currentUserId: String) {
        repository.updateLastSeen(currentUserId)
    }

    private val _partnerLastSeen = MutableStateFlow<Long>(0L)
    /** Zeitpunkt der letzten Sichtbarkeit des Chat-Partners */
    val partnerLastSeen: StateFlow<Long> = _partnerLastSeen

    /**
     * Beobachtet den letzten Sichtbarkeitszeitpunkt des Chat-Partners.
     *
     * @param currentUserId Id des aktuellen Nutzers.
     * @param partnerId Id des Chat-Partners.
     */
    fun observePartnerLastSeen(currentUserId: String, partnerId: String) {
        repository.getUserLastSeen(currentUserId, partnerId) { time ->
            _partnerLastSeen.value = time
        }
    }

    /**
     * Lädt die Nachrichten für den Chat zwischen aktuellem Nutzer und Partner.
     *
     * @param currentUserId Id des aktuellen Nutzers.
     * @param partnerId Id des Chat-Partners.
     */
    fun loadMessages(currentUserId: String, partnerId: String) {
        repository.observeMessages(currentUserId, partnerId) { msgList ->
            _messages.value = msgList
        }
    }

    /**
     * Lädt alle Chat-Partner des aktuellen Nutzers.
     *
     * @param currentUserId Id des aktuellen Nutzers.
     */
    fun loadChatPartners(currentUserId: String) {
        viewModelScope.launch {
            repository.getChatPartners(currentUserId).collect { partners ->
                _chatPartners.value =  partners.sortedByDescending { it.lastMessageTime }
            }
        }
    }

    /**
     * Sendet eine Chat-Nachricht.
     *
     * @param message Die Chat-Nachricht.
     * @param senderName Name des Absenders.
     * @param receiverName Name des Empfängers.
     */
    fun sendMessage(message: PrivateChatMessage, senderName: String, receiverName: String) {
        repository.sendMessage(message, senderName, receiverName)
    }

    /**
     * Setzt den ungelesenen Nachrichten-Zähler für den Chat mit dem Partner zurück.
     *
     * @param currentUserId Id des aktuellen Nutzers.
     * @param partnerId Id des Chat-Partners.
     */
    fun resetUnread(currentUserId: String, partnerId: String) {
        repository.resetUnreadCount(currentUserId, partnerId)
    }

    /**
     * Beendet die Beobachtung der Chat-Daten.
     */
    override fun onCleared() {
        super.onCleared()
        repository.stopObserving()
    }
}