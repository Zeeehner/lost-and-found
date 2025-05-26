package de.syntax_institut.androidabschlussprojekt.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import de.syntax_institut.androidabschlussprojekt.data.local.model.ChatMessage
import de.syntax_institut.androidabschlussprojekt.repository.ChatRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class ChatViewModel(
    private val chatRepository: ChatRepository
) : ViewModel() {

    private val _messages = MutableStateFlow<List<ChatMessage>>(emptyList())
    val messages: StateFlow<List<ChatMessage>> = _messages

    fun loadMessages(itemId: String) {
        viewModelScope.launch {
            _messages.value = chatRepository.getMessages(itemId)
        }
    }

    fun sendMessage(itemId: String, message: ChatMessage) {
        viewModelScope.launch {
            chatRepository.sendMessage(itemId, message)
            loadMessages(itemId)
        }
    }
}