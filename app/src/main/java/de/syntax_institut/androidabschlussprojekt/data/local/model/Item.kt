package de.syntax_institut.androidabschlussprojekt.data.local.model

/**
 * Repräsentiert ein gefundenes oder verlorenes Item, das vom Nutzer gemeldet wurde.
 *
 * @property id Die eindeutige ID des Items.
 * @property title Der Titel bzw. Name des Items.
 * @property description Eine Beschreibung des Items.
 * @property status Der Status des Items, z. B. `"lost"` oder `"found"`.
 * @property latitude Geografischer Breitengrad, wo das Item gemeldet wurde.
 * @property longitude Geografischer Längengrad, wo das Item gemeldet wurde.
 * @property locationName Optionaler Name des Ortes.
 * @property userId Die ID des Nutzers, der das Item erstellt hat.
 * @property userName Optionaler Name des Nutzers.
 * @property imageUrl Optionaler Bild-Link zum Item.
 * @property timestamp Zeitpunkt, an dem das Item erstellt oder gemeldet wurde (Millisekunden seit Epoch).
 */
data class Item(
    val id: String = "",
    val title: String = "",
    val description: String = "",
    val status: String = "", // "lost" oder "found"
    val latitude: Double = 0.0,
    val longitude: Double = 0.0,
    val locationName: String? = null,
    val userId: String = "",
    val userName: String? = null,
    val imageUrl: String? = null,
    val timestamp: Long = System.currentTimeMillis()
)