package de.syntax_institut.androidabschlussprojekt.ui.viewmodel

import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.firestore.FirebaseFirestore
import de.syntax_institut.androidabschlussprojekt.repository.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.util.regex.Pattern
import de.syntax_institut.androidabschlussprojekt.R

/**
 * ViewModel für die Authentifizierung (Login/Registrierung) und Benutzerverwaltung.
 *
 * @property repository Repository für Authentifizierungs-Operationen
 */
class AuthViewModel(private val repository: AuthRepository) : ViewModel() {

    /** E-Mail Eingabe */
    var emailInput by mutableStateOf("")

    /** Passwort Eingabe */
    var passwordInput by mutableStateOf("")

    /** Name Eingabe (für Registrierung) */
    var nameInput by mutableStateOf("")

    private val _navigateToOnboarding = MutableStateFlow(false)
    val navigateToOnboarding: StateFlow<Boolean> = _navigateToOnboarding.asStateFlow()

    private val _isRegistrationMode = MutableStateFlow(false)
    val isRegistrationMode: StateFlow<Boolean> = _isRegistrationMode.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _loginResult = MutableStateFlow<Boolean?>(null)
    val loginResult: StateFlow<Boolean?> = _loginResult.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    private val _phoneNumber = MutableStateFlow<String?>(null)
    val phoneNumber: StateFlow<String?> = _phoneNumber.asStateFlow()

    /** Aktuell eingeloggter Firebase Benutzer */
    val currentUser: FirebaseUser?
        get() = FirebaseAuth.getInstance().currentUser

    /** Aktueller Benutzername */
    fun getCurrentUserName(): String? = currentUser?.displayName

    /** Aktuelle E-Mail Adresse */
    fun getCurrentUserEmail(): String? = currentUser?.email

    /** Aktuelle User-ID */
    fun getCurrentUserId(): String? = currentUser?.uid

    init {
        if (currentUser != null) {
            _loginResult.value = true
            loadPhoneNumber()
        }
    }

    /** Wechsel zwischen Login- und Registrierungsmodus */
    fun toggleMode() {
        _isRegistrationMode.value = !_isRegistrationMode.value
        nameInput = ""
        passwordInput = ""
        clearError()
    }

    /** Fehlernachricht löschen */
    fun clearError() {
        _errorMessage.value = null
    }

    /**
     * Startet Login oder Registrierung je nach Modus.
     *
     * @param context Kontext für Ressourcen und Toasts
     */
    fun onLoginClick(context: Context) {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                _errorMessage.value = null

                if (_isRegistrationMode.value) {
                    registerUser(context)
                } else {
                    loginUser(context)
                }
            } catch (e: Exception) {
                handleAuthError(e, context)
            } finally {
                _isLoading.value = false
            }
        }
    }

    /** Intern: Login mit E-Mail und Passwort */
    private suspend fun loginUser(context: Context) {
        try {
            val result = repository.loginUser(emailInput, passwordInput)
            _loginResult.value = result
            if (result) loadPhoneNumber()
            if (!result) _errorMessage.value = context.getString(R.string.login_failed)
        } catch (e: Exception) {
            _loginResult.value = false
            throw e
        }
    }

    /** Intern: Registrierung mit E-Mail, Passwort und Name */
    private suspend fun registerUser(context: Context) {
        try {
            val result = repository.registerUser(emailInput, passwordInput)
            if (result) {
                val profileUpdates = UserProfileChangeRequest.Builder()
                    .setDisplayName(nameInput)
                    .build()

                currentUser?.updateProfile(profileUpdates)?.await()
                _navigateToOnboarding.value = true
            } else {
                _errorMessage.value = context.getString(R.string.registration_failed)
            }
        } catch (e: Exception) {
            _errorMessage.value = context.getString(R.string.registration_failed_with_reason, e.message ?: "")
        }
    }

    /** Navigation Flags zurücksetzen */
    fun resetNavigationFlags() {
        _navigateToOnboarding.value = false
        _loginResult.value = null
    }

    /** Fehlerhandling bei Authentifizierungsfehlern */
    private fun handleAuthError(e: Exception, context: Context) {
        val errorMessage = when {
            e.message?.contains("email-already-in-use") == true -> context.getString(R.string.error_email_in_use)
            e.message?.contains("weak-password") == true -> context.getString(R.string.error_weak_password)
            e.message?.contains("invalid-email") == true -> context.getString(R.string.error_invalid_email)
            e.message?.contains("user-not-found") == true -> context.getString(R.string.error_user_not_found)
            e.message?.contains("wrong-password") == true -> context.getString(R.string.error_wrong_password)
            e.message?.contains("network") == true -> context.getString(R.string.error_network)
            else -> context.getString(R.string.error_auth_generic, e.message ?: "")
        }
        _errorMessage.value = errorMessage
    }

    /** Überprüft ob alle Eingaben gültig sind (E-Mail, Passwort und ggf. Name) */
    fun validateInputs(): Boolean =
        isValidEmail() && isValidPassword() && (!_isRegistrationMode.value || isValidName())

    /** Prüft, ob die E-Mail-Adresse gültig ist */
    fun isValidEmail(): Boolean {
        val emailPattern = Pattern.compile(
            "[a-zA-Z0-9+._%-+]{1,256}@[a-zA-Z0-9][a-zA-Z0-9-]{0,64}(\\.[a-zA-Z0-9][a-zA-Z0-9-]{0,25})+"
        )
        return emailInput.isNotBlank() && emailPattern.matcher(emailInput).matches()
    }

    /** Prüft, ob das Passwort mindestens 6 Zeichen lang ist */
    fun isValidPassword(): Boolean = passwordInput.length >= 6

    /** Prüft, ob der Name mindestens 3 Zeichen lang ist */
    fun isValidName(): Boolean = nameInput.length >= 3

    /** Führt Logout durch und setzt Status zurück */
    fun logout() {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                FirebaseAuth.getInstance().signOut()
                _loginResult.value = false
                emailInput = ""
                passwordInput = ""
                nameInput = ""
                _phoneNumber.value = null
            } finally {
                _isLoading.value = false
            }
        }
    }

    /** Lädt die Telefonnummer des aktuellen Benutzers aus Firestore */
    fun loadPhoneNumber() {
        viewModelScope.launch {
            try {
                currentUser?.uid?.let { uid ->
                    val doc = FirebaseFirestore.getInstance()
                        .collection("users")
                        .document(uid)
                        .get()
                        .await()
                    _phoneNumber.value = doc.getString("phoneNumber")
                }
            } catch (e: Exception) {
                // Fehler ignorieren oder loggen, hier einfach false zurück
                false
            }
        }
    }
}