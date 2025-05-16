package de.syntax_institut.androidabschlussprojekt.data.local

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

// Dummy-Datenmodell
data class LostItem(
    val id: String = "",
    val title: String = "",
    val description: String = "",
    val status: String = "" // "lost" oder "found"
)