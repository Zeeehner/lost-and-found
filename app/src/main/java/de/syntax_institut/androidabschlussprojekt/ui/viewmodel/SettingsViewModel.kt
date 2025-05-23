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
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class SettingsViewModel(
    internal val authViewModel: AuthViewModel,
    private val locationRepository: LocationRepository,
    private val preferencesRepository: PreferencesRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(SettingsUiState())
    val uiState: StateFlow<SettingsUiState> = _uiState

    init {
        // Direkt beim Start laden
        loadDarkMode()
    }

    fun toggleDarkMode() {
        viewModelScope.launch {
            val newValue = !_uiState.value.darkMode
            preferencesRepository.saveDarkMode(newValue)
            println("🌙 DarkMode toggled to: $newValue")
            _uiState.update { it.copy(darkMode = newValue) }
        }
    }

    fun loadDarkMode() {
        viewModelScope.launch {
            val dark = preferencesRepository.getDarkMode()
            _uiState.update { it.copy(darkMode = dark) }
        }
    }

    fun toggleLocationInfo() {
        _uiState.update { it.copy(showLocationInfo = !it.showLocationInfo) }
    }

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

    fun showLogoutDialog() {
        _uiState.update { it.copy(showLogoutDialog = true) }
    }

    fun dismissLogoutDialog() {
        _uiState.update { it.copy(showLogoutDialog = false) }
    }

    fun logout() {
        authViewModel.logout()
    }

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
                        it.copy(permissionStatus = context.getString(R.string.notification_permission_sub))
                    }
                },
                onGranted = {
                    _uiState.update {
                        it.copy(permissionStatus = context.getString(R.string.notification_permission))
                    }
                }
            )
        }
    }

    fun onNotificationPermissionGranted(context: Context) {
        _uiState.update {
            it.copy(permissionStatus = context.getString(R.string.notification_permission))
        }

        viewModelScope.launch {
            preferencesRepository.saveNotificationEnabled(context, true)
        }
    }

    fun updatePermissionStatus(context: Context, statusResId: Int) {
        _uiState.update { it.copy(permissionStatus = context.getString(statusResId)) }
    }
}
