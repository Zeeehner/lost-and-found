package de.syntax_institut.androidabschlussprojekt.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import de.syntax_institut.androidabschlussprojekt.data.local.model.Item
import de.syntax_institut.androidabschlussprojekt.repository.LostItemRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * ViewModel für die Verwaltung verlorener Items.
 *
 * Stellt Funktionen zum Laden, Löschen und Bereitstellen von Items bereit.
 *
 * @property repository Repository zum Zugriff auf die Item-Daten.
 */
class LostItemViewModel(private val repository: LostItemRepository) : ViewModel() {

    private val _item = MutableStateFlow<Item?>(null)
    /** Aktuelles ausgewähltes Item als StateFlow */
    val item: StateFlow<Item?> = _item.asStateFlow()

    private val _items = MutableStateFlow<List<Item>>(emptyList())
    /** Liste aller Items als StateFlow */
    val items: StateFlow<List<Item>> = _items.asStateFlow()

    init {
        loadItems()
    }

    /**
     * Lädt die Liste aller verlorenen Items aus dem Repository
     * und sortiert sie nach Zeitstempel absteigend.
     */
    fun loadItems() {
        repository.getLostItems { result ->
            _items.value = result.sortedByDescending { it.timestamp }
        }
    }

    /**
     * Löscht ein Item mit der angegebenen ID und lädt die
     * Liste der Items danach erneut.
     *
     * @param itemId ID des zu löschenden Items.
     */
    fun deleteItem(itemId: String) {
        viewModelScope.launch {
            try {
                repository.deleteLostItem(itemId)
                loadItems()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}