package de.syntax_institut.androidabschlussprojekt.ui.viewmodel

import androidx.lifecycle.ViewModel
import de.syntax_institut.androidabschlussprojekt.ui.util.SettingsUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update

class SettingsViewModel(internal val authViewModel: AuthViewModel) : ViewModel() {

    private val _uiState = MutableStateFlow(SettingsUiState())
    val uiState: StateFlow<SettingsUiState> = _uiState

    /**
     * Logout Button
     */
    fun showLogoutDialog() {
        _uiState.update { it.copy(showLogoutDialog = true) }
    }

    fun dismissLogoutDialog() {
        _uiState.update { it.copy(showLogoutDialog = false) }
    }

    fun logout() {
        authViewModel.logout()
    }
}