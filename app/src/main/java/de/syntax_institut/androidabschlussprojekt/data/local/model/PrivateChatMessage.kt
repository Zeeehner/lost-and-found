package de.syntax_institut.androidabschlussprojekt.data.local.model

/**
 * Repräsentiert eine private Nachricht zwischen zwei Benutzern.
 *
 * @property id Die eindeutige ID der Nachricht.
 * @property senderId Die ID des sendenden Benutzers.
 * @property receiverId Die ID des empfangenden Benutzers.
 * @property message Der Textinhalt der Nachricht.
 * @property timestamp Zeitpunkt, zu dem die Nachricht gesendet wurde (Millisekunden seit Epoch).
 */
data class PrivateChatMessage(
    val id: String = "",
    val senderId: String = "",
    val receiverId: String = "",
    val message: String = "",
    val timestamp: Long = System.currentTimeMillis()
)