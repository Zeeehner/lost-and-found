package de.syntax_institut.androidabschlussprojekt.ui.screen

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import de.syntax_institut.androidabschlussprojekt.AdMobBanner
import de.syntax_institut.androidabschlussprojekt.R
import de.syntax_institut.androidabschlussprojekt.data.local.model.Item
import de.syntax_institut.androidabschlussprojekt.ui.component.edit.EditScreenContent
import de.syntax_institut.androidabschlussprojekt.ui.component.list.ItemActionDialog
import de.syntax_institut.androidabschlussprojekt.ui.component.list.ItemListContent
import de.syntax_institut.androidabschlussprojekt.ui.component.list.ItemListTopBar
import de.syntax_institut.androidabschlussprojekt.ui.viewmodel.AuthViewModel
import de.syntax_institut.androidabschlussprojekt.ui.viewmodel.EditViewModel
import de.syntax_institut.androidabschlussprojekt.ui.viewmodel.LostItemViewModel
import org.koin.androidx.compose.koinViewModel

/**
 * Haupt-Listen-Screen für verlorene/fundene Gegenstände.
 * Zeigt Items, Filter, Suchfunktion und ermöglicht Item-Erstellung und Bearbeitung.
 *
 * @param navController Navigation Controller zur Steuerung der Navigation
 * @param viewModel ViewModel zur Verwaltung der LostItem-Daten
 * @param authViewModel ViewModel für Authentifizierungsinformationen
 * @param editViewModel ViewModel zur Bearbeitung von Items
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ListScreen(
    navController: NavController,
    viewModel: LostItemViewModel = koinViewModel(),
    authViewModel: AuthViewModel = koinViewModel(),
    editViewModel: EditViewModel = koinViewModel()
) {
    val context = LocalContext.current
    val lostItems by viewModel.items.collectAsState()
    val currentUserId = authViewModel.currentUser?.uid

    var selectedFilter by remember { mutableStateOf("all") }
    var searchQuery by remember { mutableStateOf("") }
    var showSearch by remember { mutableStateOf(false) }

    var showItemActionDialog by remember { mutableStateOf(false) }
    var showEditSheet by remember { mutableStateOf(false) }
    var selectedItem by remember { mutableStateOf<Item?>(null) }

    val editItem by editViewModel.item.collectAsState()
    var title by remember { mutableStateOf(TextFieldValue("")) }
    var description by remember { mutableStateOf(TextFieldValue("")) }

    val listState = rememberLazyListState()
    val showBanner by remember {
        derivedStateOf {
            val lastVisible = listState.layoutInfo.visibleItemsInfo.lastOrNull()
            lastVisible != null && lastVisible.index == listState.layoutInfo.totalItemsCount - 1
        }
    }

    LaunchedEffect(editItem) {
        editItem?.let {
            title = TextFieldValue(it.title)
            description = TextFieldValue(it.description)
        }
    }

    Box {
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
                FloatingActionButton(
                    onClick = { navController.navigate("create") },
                    modifier = Modifier.padding(end = 16.dp, bottom = 10.dp)
                ) {
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
                modifier = Modifier.padding(padding),
                listState = listState
            )
        }

        if (showBanner) {
            AdMobBanner(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .height(50.dp)
            )
        }
    }

    if (showItemActionDialog && selectedItem != null) {
        ItemActionDialog(
            onDismiss = { showItemActionDialog = false },
            onEdit = {
                selectedItem?.let {
                    editViewModel.loadItem(it.id)
                    showItemActionDialog = false
                    showEditSheet = true
                }
            },
            onDelete = {
                viewModel.deleteItem(selectedItem!!.id)
                showItemActionDialog = false
            }
        )
    }

    // ModalBottomSheet für Item-Bearbeitung
    if (showEditSheet && editItem != null) {
        ModalBottomSheet(
            onDismissRequest = { showEditSheet = false },
            dragHandle = null,
            shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(300.dp)
            ) {
                EditScreenContent(
                    title = title,
                    onTitleChange = { title = it },
                    description = description,
                    onDescriptionChange = { description = it },
                    onSave = {
                        editViewModel.updateItem(
                            title = title.text,
                            description = description.text,
                            onSuccess = {
                                showEditSheet = false
                                viewModel.loadItems()
                                Toast.makeText(
                                    context,
                                    context.getString(R.string.entry_updated),
                                    Toast.LENGTH_SHORT
                                ).show()
                            },
                            onFailure = {
                                showEditSheet = false
                                Toast.makeText(
                                    context,
                                    context.getString(R.string.entry_update_failed),
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        )
                    },
                    onBack = { showEditSheet = false }
                )
            }
        }
    }
}