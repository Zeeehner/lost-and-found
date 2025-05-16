package de.syntax_institut.androidabschlussprojekt.ui.util

data class SettingsUiState(
    val darkMode: Boolean = false,
    val cityName: String? = null,
    val permissionStatus: String = "",
    val showLogoutDialog: Boolean = false,
    val showLocationInfo: Boolean = false
)