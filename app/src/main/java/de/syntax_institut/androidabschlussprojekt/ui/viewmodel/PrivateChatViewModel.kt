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

    val unreadCount: StateFlow<Int> = chatPartners
        .map { list -> list.sumOf { it.unreadCount } }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    fun loadMessages(currentUserId: String, partnerId: String) {
        repository.observeMessages(currentUserId, partnerId) { msgList ->
            _messages.value = msgList
        }
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

    fun resetUnread(currentUserId: String, partnerId: String) {
        repository.resetUnreadCount(currentUserId, partnerId)
    }

    override fun onCleared() {
        super.onCleared()
        repository.stopObserving()
    }
}
