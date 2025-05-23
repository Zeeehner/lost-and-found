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

    fun updateField(update: LostItemFormState.() -> LostItemFormState) {
        _formState.value = _formState.value.update()
    }

    fun setImage(uri: Uri?, bitmap: Bitmap?) {
        _formState.value = _formState.value.copy(imageUri = uri)
        _bitmap.value = bitmap
    }

    fun toggleLocationDetails() {
        _formState.value = _formState.value.copy(
            showLocationDetails = !_formState.value.showLocationDetails
        )
    }

    fun updateLatLongFromLocation() {
        viewModelScope.launch {
            _formState.value = _formState.value.updateCoordinatesFromLocation(repository)
        }
    }

    fun fetchCurrentLocation(context: Context) {
        viewModelScope.launch @androidx.annotation.RequiresPermission(allOf = [android.Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.ACCESS_COARSE_LOCATION]) {
            _formState.value = _formState.value.setCurrentLocation(context, repository)
        }
    }

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
