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

class AuthViewModel(private val repository: AuthRepository) : ViewModel() {

    var emailInput by mutableStateOf("")
    var passwordInput by mutableStateOf("")
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

    val currentUser: FirebaseUser?
        get() = FirebaseAuth.getInstance().currentUser

    fun getCurrentUserName(): String? = currentUser?.displayName
    fun getCurrentUserEmail(): String? = currentUser?.email
    fun getCurrentUserId(): String? = currentUser?.uid

    init {
        if (currentUser != null) {
            _loginResult.value = true
            loadPhoneNumber()
        }
    }

    fun toggleMode() {
        _isRegistrationMode.value = !_isRegistrationMode.value
        nameInput = ""
        passwordInput = ""
        clearError()
    }

    fun clearError() {
        _errorMessage.value = null
    }

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

    fun resetNavigationFlags() {
        _navigateToOnboarding.value = false
        _loginResult.value = null
    }

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

    fun validateInputs(): Boolean =
        isValidEmail() && isValidPassword() && (!_isRegistrationMode.value || isValidName())

    fun isValidEmail(): Boolean {
        val emailPattern = Pattern.compile(
            "[a-zA-Z0-9+._%-+]{1,256}@[a-zA-Z0-9][a-zA-Z0-9-]{0,64}(\\.[a-zA-Z0-9][a-zA-Z0-9-]{0,25})+"
        )
        return emailInput.isNotBlank() && emailPattern.matcher(emailInput).matches()
    }

    fun isValidPassword(): Boolean = passwordInput.length >= 6
    fun isValidName(): Boolean = nameInput.length >= 3

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
                false
            }
        }
    }
}