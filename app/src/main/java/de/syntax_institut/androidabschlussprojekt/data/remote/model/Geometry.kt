package de.syntax_institut.androidabschlussprojekt.data.remote.model

/**
 * Repräsentiert die geografischen Koordinaten eines Ortes.
 *
 * @property lat Breitengrad.
 * @property lng Längengrad.
 */
data class Geometry(
    val lat: Double?,
    val lng: Double?
)