package de.syntax_institut.androidabschlussprojekt.ui.viewmodel

import android.content.Context
import androidx.compose.runtime.*
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import de.syntax_institut.androidabschlussprojekt.R
import de.syntax_institut.androidabschlussprojekt.repository.UserRepository
import kotlinx.coroutines.launch

/**
 * ViewModel für den Onboarding-Prozess.
 *
 * Verwaltet den Fortschritt durch die rechtlichen Hinweise, die Eingabe und das Speichern der Telefonnummer.
 *
 * @property userRepository Repository für Nutzer-bezogene Aktionen, z.B. Speichern der Telefonnummer.
 */
class OnboardingViewModel(
    private val userRepository: UserRepository = UserRepository()
) : ViewModel() {

    /** Gibt an, ob die Telefonnummer gerade gespeichert wird */
    var isSaving by mutableStateOf(false)

    /** Ergebnis des Speichervorgangs */
    var saveResult by mutableStateOf<Boolean?>(null)

    /** Fehlermeldung bei Fehlern */
    var errorMessage by mutableStateOf<String?>(null)

    /** Index des aktuellen rechtlichen Hinweises */
    var currentHintIndex by mutableStateOf(0)

    /** Gibt an, ob der aktuelle Hinweis bestätigt wurde */
    var hintAcknowledged by mutableStateOf(false)

    /** Prüft, ob der aktuelle Hinweis der letzte ist */
    val isLastHint: Boolean get() = currentHintIndex == 3

    /** Prüft, ob alle Hinweise bestätigt wurden */
    val allHintsAcknowledged: Boolean get() = currentHintIndex > 3

    /** Telefonnummer, die im Onboarding eingegeben wird */
    var phoneNumber by mutableStateOf("")

    /**
     * Liefert die Liste der rechtlichen Hinweise als Strings aus den Ressourcen.
     *
     * @param context Android Context zur Ressourcenabfrage
     * @return Liste mit Hinweisen
     */
    fun getLegalHints(context: Context): List<String> {
        return listOf(
            context.getString(R.string.hint_report_to_lost_property_office),
            context.getString(R.string.hint_public_transport_lost_items),
            context.getString(R.string.hint_retaining_items_is_offense),
            context.getString(R.string.hint_found_animals_report)
        )
    }

    /**
     * Gehe zum nächsten Hinweis, wenn der aktuelle bestätigt wurde.
     */
    fun nextHint() {
        if (hintAcknowledged && currentHintIndex < 3) {
            currentHintIndex++
            hintAcknowledged = false
        } else if (hintAcknowledged && isLastHint) {
            currentHintIndex++
        }
    }

    /** Setzt den aktuellen Hinweis als bestätigt */
    fun acknowledgeHint() {
        hintAcknowledged = true
    }

    /**
     * Speichert die Telefonnummer im Repository.
     * Validiert die Telefonnummer vor dem Speichern.
     *
     * @param context Android Context für Ressourcen und UI-Interaktionen
     * @param onSuccess Callback bei erfolgreichem Speichern
     */
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

    /**
     * Validiert eine Telefonnummer anhand eines regulären Ausdrucks.
     *
     * @param phone Telefonnummer als String
     * @return true, wenn die Telefonnummer gültig ist, sonst false
     */
    private fun isValidPhoneNumber(phone: String): Boolean {
        val cleanedPhone = phone.replace("""\s+""".toRegex(), "")
        val phoneRegex = """^(\+\d{1,3}[- ]?)?\d{10,14}$""".toRegex()
        return phoneRegex.matches(cleanedPhone)
    }

    /** Setzt Fehlermeldung und Speichervalidierung zurück */
    fun clearErrorState() {
        errorMessage = null
        saveResult = null
    }
}