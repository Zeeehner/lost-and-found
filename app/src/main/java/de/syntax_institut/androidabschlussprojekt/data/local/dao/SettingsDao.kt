package de.syntax_institut.androidabschlussprojekt.data.local.dao

import androidx.room.*
import de.syntax_institut.androidabschlussprojekt.data.local.entity.SettingsEntity

/**
 * Data Access Object (DAO) für den Zugriff auf die Einstellungen in der lokalen Room-Datenbank.
 */
@Dao
interface SettingsDao {

    /**
     * Gibt die gespeicherten Einstellungen zurück. Erwartet genau einen Eintrag mit der ID = 1.
     *
     * @return [SettingsEntity] mit den aktuellen Einstellungen oder `null`, wenn keine gespeichert sind.
     */
    @Query("SELECT * FROM settings WHERE id = 1")
    suspend fun getSettings(): SettingsEntity?

    /**
     * Fügt die Einstellungen in die Datenbank ein oder ersetzt sie, falls bereits welche vorhanden sind.
     *
     * @param settings Die zu speichernden Einstellungen.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSettings(settings: SettingsEntity)
}