package de.syntax_institut.androidabschlussprojekt.ui.viewmodel

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import de.syntax_institut.androidabschlussprojekt.repository.ItemCreateRepository
import de.syntax_institut.androidabschlussprojekt.ui.component.create.LostItemFormState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

/**
 * ViewModel zur Verwaltung des Formulars für das Erstellen von verlorenen Gegenständen.
 *
 * @property repository Repository für das Erstellen von Items.
 */
class CreateViewModel(
    private val repository: ItemCreateRepository
) : ViewModel() {

    private val _formState = MutableStateFlow(LostItemFormState())
    val formState: StateFlow<LostItemFormState> = _formState

    private val _bitmap = MutableStateFlow<Bitmap?>(null)
    val bitmap: StateFlow<Bitmap?> = _bitmap

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _success = MutableStateFlow<Boolean?>(null)
    val success: StateFlow<Boolean?> = _success

    /**
     * Aktualisiert ein Feld im Formularzustand.
     *
     * @param update Lambda mit der Logik zur Aktualisierung.
     */
    fun updateField(update: LostItemFormState.() -> LostItemFormState) {
        _formState.value = _formState.value.update()
    }

    /**
     * Setzt das Bild für das Formular.
     *
     * @param uri URI des Bildes.
     * @param bitmap Bitmap des Bildes.
     */
    fun setImage(uri: Uri?, bitmap: Bitmap?) {
        _formState.value = _formState.value.copy(imageUri = uri)
        _bitmap.value = bitmap
    }

    /**
     * Schaltet die Anzeige der Standortdetails im Formular um.
     */
    fun toggleLocationDetails() {
        _formState.value = _formState.value.copy(
            showLocationDetails = !_formState.value.showLocationDetails
        )
    }

    /**
     * Aktualisiert die Koordinaten basierend auf der aktuellen Standortinformation.
     */
    fun updateLatLongFromLocation() {
        viewModelScope.launch {
            _formState.value = _formState.value.updateCoordinatesFromLocation(repository)
        }
    }

    /**
     * Holt die aktuelle Standortinformation und aktualisiert das Formular.
     *
     * @param context Kontext zur Standortabfrage.
     */
    fun fetchCurrentLocation(context: Context) {
        viewModelScope.launch @androidx.annotation.RequiresPermission(allOf = [android.Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.ACCESS_COARSE_LOCATION]) {
            _formState.value = _formState.value.setCurrentLocation(context, repository)
        }
    }

    /**
     * Sendet das Formular zum Erstellen eines neuen verlorenen Items.
     *
     * @param userId Die ID des Nutzers, der das Item erstellt.
     * @param userName Der Name des Nutzers.
     * @param onDone Callback bei erfolgreichem Erstellen.
     */
    fun submit(userId: String, userName: String, onDone: () -> Unit) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val form = _formState.value
                val image = _bitmap.value

                if (!form.isValid(image)) {
                    _success.value = false
                    _isLoading.value = false
                    return@launch
                }

                val item = form.toLostItem(userId, userName)
                val result = repository.addLostItem(item, image)

                _success.value = result
                if (result) onDone()
            } catch (e: Exception) {
                _success.value = false
            } finally {
                _isLoading.value = false
            }
        }
    }
}