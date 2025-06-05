package de.syntax_institut.androidabschlussprojekt.repository

import android.content.Context
import de.syntax_institut.androidabschlussprojekt.data.local.dao.SettingsDao
import de.syntax_institut.androidabschlussprojekt.data.local.entity.SettingsEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class PreferencesRepository(private val settingsDao: SettingsDao) {

    suspend fun saveDarkMode(enabled: Boolean) {
        val currentSettings = settingsDao.getSettings() ?: SettingsEntity()
        settingsDao.insertSettings(currentSettings.copy(darkMode = enabled))
    }

    suspend fun getDarkMode(): Boolean {
        return settingsDao.getSettings()?.darkMode ?: false
    }

    suspend fun saveNotificationEnabled(context: Context, enabled: Boolean) {
        // TODO: Erweiterung um notificationEnabled
    }
}
