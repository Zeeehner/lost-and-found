package de.syntax_institut.androidabschlussprojekt.data.local.model

data class ChatMessage(
    val id: String = "",
    val itemId: String = "",
    val userId: String = "",
    val userName: String = "",
    val message: String = "",
    val timestamp: Long = System.currentTimeMillis()
)