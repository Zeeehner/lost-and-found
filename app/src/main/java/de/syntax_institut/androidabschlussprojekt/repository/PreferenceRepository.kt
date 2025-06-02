package de.syntax_institut.androidabschlussprojekt.repository

import android.content.Context
import de.syntax_institut.androidabschlussprojekt.data.local.dao.SettingsDao
import de.syntax_institut.androidabschlussprojekt.data.local.entity.SettingsEntity

/**
 * Repository zur Verwaltung von Benutzereinstellungen wie Dark Mode und (zukünftig) Benachrichtigungen.
 */
class PreferencesRepository(private val settingsDao: SettingsDao) {

    /**
     * Speichert den aktuellen Dark-Mode-Zustand in der Datenbank.
     *
     * @param enabled `true`, wenn Dark Mode aktiviert sein soll.
     */
    suspend fun saveDarkMode(enabled: Boolean) {
        val currentSettings = settingsDao.getSettings() ?: SettingsEntity()
        settingsDao.insertSettings(currentSettings.copy(darkMode = enabled))
    }

    /**
     * Gibt zurück, ob der Dark Mode aktiviert ist.
     *
     * @return `true`, wenn aktiviert; sonst `false`.
     */
    suspend fun getDarkMode(): Boolean {
        return settingsDao.getSettings()?.darkMode ?: false
    }

    /**
     * Platzhalter zur zukünftigen Speicherung von Benachrichtigungseinstellungen.
     * Hierfür müsste [SettingsEntity] um ein entsprechendes Feld erweitert werden.
     *
     * @param context Aktueller Context (für spätere Nutzung, z. B. DataStore).
     * @param enabled `true`, wenn Benachrichtigungen erlaubt sein sollen.
     */
    suspend fun saveNotificationEnabled(context: Context, enabled: Boolean) {
        // TODO: Erweiterung von SettingsEntity um notificationsEnabled:Boolean
        // Dann analoge Speicherung wie bei DarkMode
    }
}