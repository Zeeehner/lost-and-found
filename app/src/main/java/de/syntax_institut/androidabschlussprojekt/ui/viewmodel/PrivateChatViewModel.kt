package de.syntax_institut.androidabschlussprojekt.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import de.syntax_institut.androidabschlussprojekt.data.local.model.ChatPartner
import de.syntax_institut.androidabschlussprojekt.data.local.model.PrivateChatMessage
import de.syntax_institut.androidabschlussprojekt.repository.PrivateChatRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class PrivateChatViewModel(
    private val repository: PrivateChatRepository
) : ViewModel() {

    private val _messages = MutableStateFlow<List<PrivateChatMessage>>(emptyList())
    val messages: StateFlow<List<PrivateChatMessage>> = _messages

    private val _chatPartners = MutableStateFlow<List<ChatPartner>>(emptyList())
    val chatPartners: StateFlow<List<ChatPartner>> = _chatPartners

    fun loadMessages(currentUserId: String, partnerId: String) {
        repository.observeMessages(currentUserId, partnerId) { msgList ->
            _messages.value = msgList
        }
    }

    override fun onCleared() {
        super.onCleared()
        repository.stopObserving()
    }

    fun loadChatPartners(currentUserId: String) {
        viewModelScope.launch {
            repository.getChatPartners(currentUserId).collect {
                _chatPartners.value = it
            }
        }
    }

    fun sendMessage(message: PrivateChatMessage, senderName: String, receiverName: String) {
        repository.sendMessage(message, senderName, receiverName)
    }
}