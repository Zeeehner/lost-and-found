package de.syntax_institut.androidabschlussprojekt.data.remote.model

/**
 * Antwortmodell für Anfragen an die Geocoding-API.
 *
 * Enthält eine Liste von [GeocodingResult]-Objekten, die mögliche Übereinstimmungen zur Anfrage repräsentieren.
 *
 * @property results Liste der gefundenen Ergebnisse.
 */
data class GeocodingResponse(
    val results: List<GeocodingResult>
)