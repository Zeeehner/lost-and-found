package de.syntax_institut.androidabschlussprojekt.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import de.syntax_institut.androidabschlussprojekt.data.local.model.Item
import de.syntax_institut.androidabschlussprojekt.repository.MapRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

/**
 * ViewModel zur Verwaltung von Items, die auf der Karte angezeigt werden.
 *
 * Verwaltet die gesamte Liste der Items und eine gefilterte Liste basierend auf Suchanfragen.
 *
 * @property repository Repository zur Abfrage von Lost Items.
 */
class MapViewModel(
    private val repository: MapRepository
) : ViewModel() {

    private val _allItems = MutableStateFlow<List<Item>>(emptyList())

    private val _items = MutableStateFlow<List<Item>>(emptyList())
    /** Gefilterte Liste der Items, z.B. basierend auf Suchanfragen */
    val items: StateFlow<List<Item>> = _items

    init {
        viewModelScope.launch {
            repository.getLostItemsFlow().collect { fetched ->
                _allItems.value = fetched
                _items.value = fetched
            }
        }
    }

    /**
     * Filtert die Items anhand der Suchanfrage.
     * Es wird in Titel, Beschreibung und Standortname gesucht.
     *
     * @param query Suchbegriff
     */
    fun search(query: String) {
        val lowerQuery = query.trim().lowercase()
        _items.value = _allItems.value.filter { item ->
            item.title.lowercase().contains(lowerQuery) ||
                    item.description.lowercase().contains(lowerQuery) ||
                    item.locationName?.lowercase()?.contains(lowerQuery) == true
        }
    }

    /** Setzt den Filter zurück und zeigt alle Items an. */
    fun clearSearch() {
        _items.value = _allItems.value
    }
}