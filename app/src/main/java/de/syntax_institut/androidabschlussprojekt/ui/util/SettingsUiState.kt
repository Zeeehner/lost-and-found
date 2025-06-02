package de.syntax_institut.androidabschlussprojekt.ui.util

/**
 * UI-Statusklasse für die Einstellungen.
 *
 * @property darkMode Gibt an, ob der Dunkelmodus aktiviert ist.
 * @property cityName Name der aktuellen Stadt, falls verfügbar.
 * @property permissionStatus Statusmeldung zu Berechtigungen.
 * @property showLogoutDialog Gibt an, ob der Logout-Bestätigungsdialog angezeigt wird.
 * @property showLocationInfo Gibt an, ob Standort-Informationen sichtbar sind.
 */
data class SettingsUiState(
    val darkMode: Boolean = false,
    val cityName: String? = null,
    val permissionStatus: String = "",
    val showLogoutDialog: Boolean = false,
    val showLocationInfo: Boolean = false
)