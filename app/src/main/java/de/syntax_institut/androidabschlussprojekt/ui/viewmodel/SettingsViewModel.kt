package de.syntax_institut.androidabschlussprojekt.ui.viewmodel

import android.Manifest
import android.content.Context
import android.os.Build
import androidx.activity.result.ActivityResultLauncher
import androidx.annotation.RequiresPermission
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import de.syntax_institut.androidabschlussprojekt.repository.LocationRepository
import de.syntax_institut.androidabschlussprojekt.repository.PreferencesRepository
import de.syntax_institut.androidabschlussprojekt.ui.util.PermissionUtils
import de.syntax_institut.androidabschlussprojekt.ui.util.SettingsUiState
import de.syntax_institut.androidabschlussprojekt.R
import de.syntax_institut.androidabschlussprojekt.repository.SettingsRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * ViewModel für die Einstellungen des Nutzers.
 *
 * Verwaltet UI-Status und Aktionen für Dark Mode, Standort, Berechtigungen und Logout.
 *
 * @property authViewModel Authentifizierungs-ViewModel für Logout.
 * @property locationRepository Repository für Standortdaten.
 * @property preferencesRepository Repository für lokale Einstellungen.
 * @property settingsRepository Repository für globale Einstellungen.
 */
class SettingsViewModel(
    internal val authViewModel: AuthViewModel,
    private val locationRepository: LocationRepository,
    private val preferencesRepository: PreferencesRepository,
    private val settingsRepository: SettingsRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(SettingsUiState())
    /** UI-Zustand der Einstellungen als StateFlow */
    val uiState: StateFlow<SettingsUiState> = _uiState


    /**
     * Initialisiert den Dark Mode aus den Einstellungen.
     */
    init {
        viewModelScope.launch {
            settingsRepository.observeSettings().collect { entity ->
                val darkMode = entity?.darkMode ?: false
                _uiState.update { it.copy(darkMode = darkMode) }
            }
        }
    }

    /**
     * Wechselt den Dark Mode und speichert den Wert.
     */
    fun toggleDarkMode() {
        viewModelScope.launch {
            val newValue = !_uiState.value.darkMode
            preferencesRepository.saveDarkMode(newValue)
            println("🌙 DarkMode toggled to: $newValue")
            _uiState.update { it.copy(darkMode = newValue) }
        }
    }

    /**
     * Lädt den gespeicherten Dark Mode Wert.
     */
    fun loadDarkMode() {
        viewModelScope.launch {
            val dark = preferencesRepository.getDarkMode()
            _uiState.update { it.copy(darkMode = dark) }
        }
    }

    /**
     * Schaltet die Sichtbarkeit der Standort-Informationen um.
     */
    fun toggleLocationInfo() {
        _uiState.update { it.copy(showLocationInfo = !it.showLocationInfo) }
    }

    /**
     * Aktualisiert den Standort und den Stadtnamen (vorausgesetzt die erforderlichen Berechtigungen sind erteilt).
     *
     * @param context Context zur Standortabfrage.
     */
    @RequiresPermission(allOf = [Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION])
    fun updateLocation(context: Context) {
        viewModelScope.launch {
            val (location, cityName) = locationRepository.getLocationWithCity(context)
            _uiState.update {
                it.copy(
                    cityName = cityName,
                    permissionStatus = context.getString(R.string.permission_granted)
                )
            }
        }
    }

    /**
     * Zeigt den Logout-Dialog an.
     */
    fun showLogoutDialog() {
        _uiState.update { it.copy(showLogoutDialog = true) }
    }

    /**
     * Versteckt den Logout-Dialog.
     */
    fun dismissLogoutDialog() {
        _uiState.update { it.copy(showLogoutDialog = false) }
    }

    /**
     * Führt den Logout durch.
     */
    fun logout() {
        authViewModel.logout()
    }

    /**
     * Fordert die Benachrichtigungsberechtigung an, falls erforderlich (ab Android 13).
     *
     * @param context Context für die Berechtigungsabfrage.
     * @param launcher Launcher zum Starten der Berechtigungsanfrage.
     */
    fun requestNotificationPermission(
        context: Context,
        launcher: ActivityResultLauncher<String>
    ) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            PermissionUtils.handleNotificationPermission(
                context = context,
                requestPermissionLauncher = launcher,
                showRationale = {
                    _uiState.update {
                        it.copy(permissionStatus = context.getString(R.string.permission_granted))
                    }
                },
                onGranted = {
                    _uiState.update {
                        it.copy(permissionStatus = context.getString(R.string.permission_denied))
                    }
                }
            )
        }
    }

    /**
     * Wird aufgerufen, wenn die Benachrichtigungsberechtigung erteilt wurde.
     *
     * @param context Context für Speicherung der Einstellung.
     */
    fun onNotificationPermissionGranted(context: Context) {
        _uiState.update {
            it.copy(permissionStatus = context.getString(R.string.permission_granted))
        }

        viewModelScope.launch {
            preferencesRepository.saveNotificationEnabled(context, true)
        }
    }

    /**
     * Aktualisiert die Anzeige der Berechtigungs-Statusmeldung.
     *
     * @param context Context für String-Ressource.
     * @param statusResId Ressourcen-ID der Statusmeldung.
     */
    fun updatePermissionStatus(context: Context, statusResId: Int) {
        _uiState.update { it.copy(permissionStatus = context.getString(statusResId)) }
    }
}