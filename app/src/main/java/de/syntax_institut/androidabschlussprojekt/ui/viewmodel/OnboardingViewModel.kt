package de.syntax_institut.androidabschlussprojekt.ui.viewmodel

import android.content.Context
import androidx.compose.runtime.*
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import de.syntax_institut.androidabschlussprojekt.R
import de.syntax_institut.androidabschlussprojekt.repository.UserRepository
import kotlinx.coroutines.launch

class OnboardingViewModel(
    private val userRepository: UserRepository = UserRepository()
) : ViewModel() {

    var isSaving by mutableStateOf(false)
    var saveResult by mutableStateOf<Boolean?>(null)
    var errorMessage by mutableStateOf<String?>(null)

    var currentHintIndex by mutableStateOf(0)
    var hintAcknowledged by mutableStateOf(false)
    val isLastHint: Boolean get() = currentHintIndex == 3
    val allHintsAcknowledged: Boolean get() = currentHintIndex > 3

    var phoneNumber by mutableStateOf("")

    fun getLegalHints(context: Context): List<String> {
        return listOf(
            context.getString(R.string.hint_report_to_lost_property_office),
            context.getString(R.string.hint_public_transport_lost_items),
            context.getString(R.string.hint_retaining_items_is_offense),
            context.getString(R.string.hint_found_animals_report)
        )
    }

    fun nextHint() {
        if (hintAcknowledged && currentHintIndex < 3) {
            currentHintIndex++
            hintAcknowledged = false
        } else if (hintAcknowledged && isLastHint) {
            currentHintIndex++
        }
    }

    fun acknowledgeHint() {
        hintAcknowledged = true
    }

    fun savePhoneNumber(context: Context, onSuccess: () -> Unit) {
        if (phoneNumber.isNotBlank() && !isValidPhoneNumber(phoneNumber)) {
            errorMessage = context.getString(R.string.invalid_phone_number)
            return
        }

        viewModelScope.launch {
            try {
                isSaving = true
                errorMessage = null

                userRepository.savePhoneNumber(phoneNumber.takeIf { it.isNotBlank() })

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
