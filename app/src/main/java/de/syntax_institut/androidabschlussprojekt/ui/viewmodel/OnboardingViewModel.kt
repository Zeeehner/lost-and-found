package de.syntax_institut.androidabschlussprojekt.ui.viewmodel

import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import de.syntax_institut.androidabschlussprojekt.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class OnboardingViewModel : ViewModel() {

    var phoneNumber by mutableStateOf("")
    var isSaving by mutableStateOf(false)
    var saveResult by mutableStateOf<Boolean?>(null)
    var errorMessage by mutableStateOf<String?>(null)

    private val firestore = FirebaseFirestore.getInstance()
    private val currentUser = FirebaseAuth.getInstance().currentUser

    fun savePhoneNumber(context: Context, onSuccess: () -> Unit) {
        if (phoneNumber.isNotBlank() && !isValidPhoneNumber(phoneNumber)) {
            errorMessage = context.getString(R.string.invalid_phone_number)
            return
        }

        viewModelScope.launch {
            try {
                isSaving = true
                errorMessage = null

                currentUser?.uid?.let { uid ->
                    val userRef = firestore.collection("users").document(uid)
                    userRef.set(
                        mapOf("phoneNumber" to phoneNumber.takeIf { it.isNotBlank() }),
                        SetOptions.merge()
                    ).await()
                }

                saveResult = true
                onSuccess()
            } catch (e: Exception) {
                saveResult = false
                errorMessage = context.getString(R.string.save_failed)
            } finally {
                isSaving = false
            }
        }
    }

    // Basic international phone number validation
    private fun isValidPhoneNumber(phone: String): Boolean {
        val cleanedPhone = phone.replace("""\s+""".toRegex(), "")
        val phoneRegex = """^(\+\d{1,3}[- ]?)?\d{10,14}$""".toRegex()
        return phoneRegex.matches(cleanedPhone)
    }

    fun clearErrorState() {
        errorMessage = null
        saveResult = null
    }
}
