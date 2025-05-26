package de.syntax_institut.androidabschlussprojekt.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import de.syntax_institut.androidabschlussprojekt.data.local.model.Item
import de.syntax_institut.androidabschlussprojekt.repository.MapRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlin.collections.filter

class MapViewModel(
    private val repository: MapRepository
) : ViewModel() {

    private val _allItems = MutableStateFlow<List<Item>>(emptyList())
    private val _items = MutableStateFlow<List<Item>>(emptyList())
    val items: StateFlow<List<Item>> = _items

    init {
        viewModelScope.launch {
            repository.getLostItemsFlow().collect { fetched ->
                _allItems.value = fetched
                _items.value = fetched
            }
        }
    }

    fun search(query: String) {
        val lowerQuery = query.trim().lowercase()
        _items.value = _allItems.value.filter { item ->
            item.title.lowercase().contains(lowerQuery) ||
                    item.description.lowercase().contains(lowerQuery) ||
                    item.locationName?.lowercase()?.contains(lowerQuery) == true
        }
    }

    fun clearSearch() {
        _items.value = _allItems.value
    }
}
