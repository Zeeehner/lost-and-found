package de.syntax_institut.androidabschlussprojekt.ui.screen

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import org.koin.androidx.compose.koinViewModel
import androidx.compose.runtime.*
import de.syntax_institut.androidabschlussprojekt.data.local.Item
import de.syntax_institut.androidabschlussprojekt.ui.component.list.ItemActionDialog
import de.syntax_institut.androidabschlussprojekt.ui.component.list.ItemListContent
import de.syntax_institut.androidabschlussprojekt.ui.component.list.ItemListTopBar
import de.syntax_institut.androidabschlussprojekt.ui.viewmodel.AuthViewModel
import de.syntax_institut.androidabschlussprojekt.ui.viewmodel.LostItemViewModel


@Composable
fun ItemListScreen(
    navController: NavController,
    viewModel: LostItemViewModel = koinViewModel(),
    authViewModel: AuthViewModel = koinViewModel()
) {
    val lostItems by viewModel.items.collectAsState()
    val currentUserId = authViewModel.currentUser?.uid
    var selectedFilter by remember { mutableStateOf("all") }
    var searchQuery by remember { mutableStateOf("") }
    var showSearch by remember { mutableStateOf(false) }
    var showItemActionDialog by remember { mutableStateOf(false) }
    var selectedItem by remember { mutableStateOf<Item?>(null) }

    Scaffold(
        topBar = {
            ItemListTopBar(
                showSearch = showSearch,
                searchQuery = searchQuery,
                onToggleSearch = { showSearch = !showSearch },
                onSearchChange = { searchQuery = it },
                onClearSearch = { searchQuery = "" },
                onNavigateToMap = { navController.navigate("map") }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { navController.navigate("create") }) {
                Icon(Icons.Default.Add, contentDescription = null)
            }
        }
    ) { padding ->
        ItemListContent(
            items = lostItems,
            searchQuery = searchQuery,
            selectedFilter = selectedFilter,
            onFilterChange = { selectedFilter = it },
            onItemClick = { navController.navigate("detail/${it.id}") },
            onItemLongClick = {
                selectedItem = it
                showItemActionDialog = true
            },
            currentUserId = currentUserId,
            modifier = Modifier.padding(padding)
        )
    }

    if (showItemActionDialog && selectedItem != null) {
        ItemActionDialog(
            onDismiss = { showItemActionDialog = false },
            onEdit = {
                navController.navigate("edit/${selectedItem!!.id}")
                showItemActionDialog = false
            },
            onDelete = {
                viewModel.deleteItem(selectedItem!!.id)
                showItemActionDialog = false
            }
        )
    }
}
