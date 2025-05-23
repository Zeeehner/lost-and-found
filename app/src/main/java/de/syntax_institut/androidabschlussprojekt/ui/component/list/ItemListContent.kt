package de.syntax_institut.androidabschlussprojekt.ui.component.list

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import de.syntax_institut.androidabschlussprojekt.R
import de.syntax_institut.androidabschlussprojekt.data.local.Item

@Composable
fun ItemListContent(
    items: List<Item>,
    searchQuery: String,
    selectedFilter: String,
    onFilterChange: (String) -> Unit,
    onItemClick: (Item) -> Unit,
    onItemLongClick: (Item) -> Unit,
    currentUserId: String?,
    modifier: Modifier = Modifier
) {
    val filteredItems = items.filter { item ->
        val matchesQuery = item.title.contains(searchQuery, ignoreCase = true) ||
                item.description.contains(searchQuery, ignoreCase = true)
        val matchesFilter = selectedFilter == "all" || item.status == selectedFilter
        matchesQuery && matchesFilter
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
    ) {
        Text(
            text = stringResource(R.string.filter_status),
            style = MaterialTheme.typography.labelLarge,
            modifier = Modifier.padding(vertical = 8.dp)
        )

        ItemFilterChips(
            selectedFilter = selectedFilter,
            onFilterChange = onFilterChange
        )

        when {
            items.isEmpty() -> {
                EmptyStateMessage()
            }
            filteredItems.isEmpty() -> {
                NoResultsMessage(selectedFilter, searchQuery)
            }
            else -> {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    contentPadding = PaddingValues(bottom = 80.dp)
                ) {
                    items(filteredItems) { item ->
                        ItemCard(
                            item = item,
                            onClick = { onItemClick(item) },
                            onLongClick = { onItemLongClick(item) },
                            currentUserId = currentUserId
                        )
                    }
                }
            }
        }
    }
}