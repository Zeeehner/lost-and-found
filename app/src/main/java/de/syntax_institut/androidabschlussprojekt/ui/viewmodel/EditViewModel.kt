package de.syntax_institut.androidabschlussprojekt.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import de.syntax_institut.androidabschlussprojekt.data.local.model.Item
import de.syntax_institut.androidabschlussprojekt.repository.EditRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

/**
 * ViewModel für das Bearbeiten eines Items.
 *
 * Verwaltet das Laden und Aktualisieren eines Items.
 *
 * @property repository Repository zur Item-Verwaltung.
 */
class EditViewModel(
    private val repository: EditRepository
) : ViewModel() {

    private val _item = MutableStateFlow<Item?>(null)
    /** Das aktuell geladene Item als StateFlow */
    val item: StateFlow<Item?> = _item

    /**
     * Lädt ein Item anhand der übergebenen ID.
     *
     * @param itemId Die ID des zu ladenden Items.
     */
    fun loadItem(itemId: String) {
        repository.getLostItemById(itemId) { result ->
            _item.value = result
        }
    }

    /**
     * Aktualisiert das aktuell geladene Item mit neuen Werten.
     *
     * @param title Neuer Titel
     * @param description Neue Beschreibung
     * @param onSuccess Callback bei erfolgreichem Update
     * @param onFailure Callback bei Fehlschlag des Updates
     */
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