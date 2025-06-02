package de.syntax_institut.androidabschlussprojekt.data.remote.model

/**
 * Repräsentiert ein einzelnes Ergebnis einer Geocoding-Abfrage.
 *
 * @property components Enthält die einzelnen Adressbestandteile wie Stadt, Land usw.
 * @property formatted Die formatierte Adresse als String.
 * @property geometry Die geografischen Koordinaten (Breite und Länge) des Ergebnisses.
 */
data class GeocodingResult(
    val components: Components,
    val formatted: String,
    val geometry: Geometry
)