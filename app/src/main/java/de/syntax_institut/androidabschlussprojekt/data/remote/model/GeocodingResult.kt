package de.syntax_institut.androidabschlussprojekt.data.remote.model

data class GeocodingResult(
    val components: Components,
    val formatted: String,
    val geometry: Geometry
)