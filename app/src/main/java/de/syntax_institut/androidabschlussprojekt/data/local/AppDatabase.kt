package de.syntax_institut.androidabschlussprojekt.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import de.syntax_institut.androidabschlussprojekt.data.local.dao.SettingsDao
import de.syntax_institut.androidabschlussprojekt.data.local.entity.SettingsEntity

@Database(entities = [SettingsEntity::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun settingsDao(): SettingsDao
}