package de.syntax_institut.androidabschlussprojekt.data.remote.model

/**
 * Repräsentiert einzelne Bestandteile einer Adresse, wie sie von der Geocoding-API zurückgegeben werden.
 *
 * Nur einer der Ortswerte (city, town oder village) ist in der Regel befüllt – je nach Kontext.
 *
 * @property city Name der Stadt (falls vorhanden).
 * @property town Name der Kleinstadt (optional).
 * @property village Name des Dorfs (optional).
 * @property state Bundesland oder Region.
 * @property country Land.
 */
data class Components(
    val city: String?,
    val town: String?,
    val village: String?,
    val state: String?,
    val country: String?
)