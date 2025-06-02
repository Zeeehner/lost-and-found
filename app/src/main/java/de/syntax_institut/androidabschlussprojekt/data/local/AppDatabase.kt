package de.syntax_institut.androidabschlussprojekt.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import de.syntax_institut.androidabschlussprojekt.data.local.dao.SettingsDao
import de.syntax_institut.androidabschlussprojekt.data.local.entity.SettingsEntity

/**
 * Zentrale Room-Datenbankklasse der App.
 * Definiert alle eingebundenen Entitäten und DAOs.
 */
@Database(entities = [SettingsEntity::class], version = 1)
abstract class AppDatabase : RoomDatabase() {

    /**
     * DAO für den Zugriff auf gespeicherte Einstellungen.
     */
    abstract fun settingsDao(): SettingsDao
}