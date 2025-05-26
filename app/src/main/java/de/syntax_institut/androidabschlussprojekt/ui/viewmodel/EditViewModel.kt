package de.syntax_institut.androidabschlussprojekt.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import de.syntax_institut.androidabschlussprojekt.data.local.model.Item
import de.syntax_institut.androidabschlussprojekt.repository.EditRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class EditViewModel(
    private val repository: EditRepository
) : ViewModel() {

    private val _item = MutableStateFlow<Item?>(null)
    val item: StateFlow<Item?> = _item

    fun loadItem(itemId: String) {
        repository.getLostItemById(itemId) { result ->
            _item.value = result
        }
    }

    fun updateItem(
        title: String,
        description: String,
        onSuccess: () -> Unit,
        onFailure: () -> Unit
    ) {
        val current = _item.value ?: return
        val updatedItem = current.copy(title = title, description = description)

        viewModelScope.launch {
            repository.updateItem(updatedItem, onSuccess, onFailure)
        }
    }
}