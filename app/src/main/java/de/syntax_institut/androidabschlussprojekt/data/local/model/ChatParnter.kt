package de.syntax_institut.androidabschlussprojekt.data.local.model

/**
 * Repräsentiert einen Chatpartner mit Metadaten zur letzten Unterhaltung.
 *
 * @property userId Die eindeutige ID des Chatpartners.
 * @property userName Der Anzeigename des Chatpartners.
 * @property lastMessage Der Text der letzten Nachricht in diesem Chat.
 * @property lastMessageTime Zeitpunkt der letzten Nachricht (in Millisekunden seit Epoch).
 * @property unreadCount Anzahl der ungelesenen Nachrichten in diesem Chat.
 * @property lastSeen Zeitpunkt, zu dem der Chatpartner zuletzt online war (in Millisekunden seit Epoch).
 */
data class ChatPartner(
    val userId: String = "",
    val userName: String = "",
    val lastMessage: String = "",
    val lastMessageTime: Long = System.currentTimeMillis(),
    val unreadCount: Int = 0,
    val lastSeen: Long = System.currentTimeMillis()
)