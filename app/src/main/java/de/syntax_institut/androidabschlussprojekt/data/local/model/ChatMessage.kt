package de.syntax_institut.androidabschlussprojekt.data.local.model

/**
 * Repräsentiert eine einzelne Chatnachricht innerhalb eines Item-Chats.
 *
 * @property id Eindeutige ID der Nachricht.
 * @property itemId ID des zugehörigen Items.
 * @property userId ID des Benutzers, der die Nachricht gesendet hat.
 * @property userName Name des Benutzers, der die Nachricht gesendet hat.
 * @property message Der eigentliche Nachrichtentext.
 * @property timestamp Zeitpunkt, zu dem die Nachricht erstellt wurde (in Millisekunden seit Epoch).
 */
data class ChatMessage(
    val id: String = "",
    val itemId: String = "",
    val userId: String = "",
    val userName: String = "",
    val message: String = "",
    val timestamp: Long = System.currentTimeMillis()
)