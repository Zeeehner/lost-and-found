package de.syntax_institut.androidabschlussprojekt.data.local.model

data class PrivateChatMessage(
    val id: String = "",
    val senderId: String = "",
    val receiverId: String = "",
    val message: String = "",
    val timestamp: Long = System.currentTimeMillis()
)