package de.syntax_institut.androidabschlussprojekt.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import de.syntax_institut.androidabschlussprojekt.data.local.model.ChatMessage
import de.syntax_institut.androidabschlussprojekt.repository.ChatRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

/**
 * ViewModel zur Verwaltung von Chat-Nachrichten eines Items.
 *
 * @property chatRepository Repository zur Kommunikation mit den Chat-Daten.
 */
class ChatViewModel(
    private val chatRepository: ChatRepository
) : ViewModel() {

    private val _messages = MutableStateFlow<List<ChatMessage>>(emptyList())
    val messages: StateFlow<List<ChatMessage>> = _messages

    /**
     * Lädt die Chat-Nachrichten für ein bestimmtes Item.
     *
     * @param itemId Die ID des Items, dessen Nachrichten geladen werden sollen.
     */
    fun loadMessages(itemId: String) {
        viewModelScope.launch {
            _messages.value = chatRepository.getMessages(itemId)
        }
    }

    /**
     * Sendet eine neue Nachricht für ein bestimmtes Item und lädt anschließend die Nachrichten neu.
     *
     * @param itemId Die ID des Items, für das die Nachricht gesendet wird.
     * @param message Die zu sendende Chat-Nachricht.
     */
    fun sendMessage(itemId: String, message: ChatMessage) {
        viewModelScope.launch {
            chatRepository.sendMessage(itemId, message)
            loadMessages(itemId)
        }
    }
}