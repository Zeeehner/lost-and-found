package de.syntax_institut.androidabschlussprojekt.ui.screen

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import androidx.compose.runtime.*
import androidx.compose.ui.unit.dp
import de.syntax_institut.androidabschlussprojekt.data.local.LostItem
import de.syntax_institut.androidabschlussprojekt.ui.component.list.ItemCard
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LostItemListScreen(
    navController: NavController) {
    var searchQuery by remember { mutableStateOf("") }

    // Dummy-Daten
    val lostItems = remember {
        listOf(
            LostItem("1", "Schlüssel", "Silberner Haustürschlüssel", "lost"),
            LostItem("2", "Handy", "Schwarzes Samsung Galaxy", "found"),
            LostItem("3", "Rucksack", "Blauer Eastpak Rucksack", "lost")
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Lost & Found") }
            )
        },
    ) { padding ->
        LazyColumn(
            contentPadding = padding,
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            items(lostItems.filter { it.title.contains(searchQuery, ignoreCase = true) }) { item ->
                ItemCard(item)
                Spacer(modifier = Modifier.height(8.dp))
            }
        }
    }
}