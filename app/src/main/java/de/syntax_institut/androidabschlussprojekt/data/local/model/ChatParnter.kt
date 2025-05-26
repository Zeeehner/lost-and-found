package de.syntax_institut.androidabschlussprojekt.data.local.model

data class ChatPartner(
    val userId: String = "",
    val userName: String = "",
    val lastMessage: String = "",
    val lastMessageTime: Long = System.currentTimeMillis()
)