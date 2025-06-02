package de.syntax_institut.androidabschlussprojekt.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Entität für die Einstellungen, die in der lokalen Room-Datenbank gespeichert werden.
 *
 * @property id Primärschlüssel, standardmäßig auf 1 gesetzt (nur ein Settings-Eintrag wird erwartet).
 * @property darkMode Gibt an, ob der Dark Mode aktiviert ist.
 */
@Entity(tableName = "settings")
data class SettingsEntity(
    @PrimaryKey val id: Int = 1,
    val darkMode: Boolean = false
)