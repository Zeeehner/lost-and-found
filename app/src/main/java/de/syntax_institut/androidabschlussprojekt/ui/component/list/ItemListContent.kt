package de.syntax_institut.androidabschlussprojekt.ui.component.list

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import de.syntax_institut.androidabschlussprojekt.R
import de.syntax_institut.androidabschlussprojekt.data.local.model.Item

@Composable
fun ItemListContent(
    items: List<Item>,
    searchQuery: String,
    selectedFilter: String,
    onFilterChange: (String) -> Unit,
    onItemClick: (Item) -> Unit,
    onItemLongClick: (Item) -> Unit,
    currentUserId: String?,
    modifier: Modifier = Modifier,
    listState: LazyListState
) {
    val filteredItems = when (selectedFilter) {
        "all" -> items.filter {
            it.title.contains(searchQuery, ignoreCase = true) ||
                    it.description.contains(searchQuery, ignoreCase = true)
        }
        else -> items.filter {
            it.status == selectedFilter &&
                    (it.title.contains(searchQuery, ignoreCase = true) ||
                            it.description.contains(searchQuery, ignoreCase = true))
        }
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

        val filters = listOf("all", "lost", "found")

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            filters.forEach { filter ->
                FilterChip(
                    selected = selectedFilter == filter,
                    onClick = { onFilterChange(filter) },
                    label = {
                        Text(
                            text = when (filter) {
                                "all" -> stringResource(R.string.filter_all)
                                "lost" -> stringResource(R.string.filter_lost)
                                "found" -> stringResource(R.string.filter_found)
                                else -> filter
                            }
                        )
                    },
                    leadingIcon = if (selectedFilter == filter) {
                        {
                            Icon(
                                Icons.Default.Check,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    } else null
                )
            }
        }

        if (items.isEmpty()) {
            EmptyStateMessage()
        } else if (filteredItems.isEmpty()) {
            NoResultsMessage(selectedFilter, searchQuery)
        } else {
            LazyColumn(
                state = listState,
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