package de.syntax_institut.androidabschlussprojekt.ui.screen

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import de.syntax_institut.androidabschlussprojekt.AdMobBanner
import de.syntax_institut.androidabschlussprojekt.R
import de.syntax_institut.androidabschlussprojekt.ui.component.detail.*
import de.syntax_institut.androidabschlussprojekt.ui.component.edit.EditScreenContent
import de.syntax_institut.androidabschlussprojekt.ui.viewmodel.AuthViewModel
import de.syntax_institut.androidabschlussprojekt.ui.viewmodel.DetailViewModel
import de.syntax_institut.androidabschlussprojekt.ui.viewmodel.EditViewModel
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailScreen(
    itemId: String,
    navController: NavController,
    viewModel: DetailViewModel = koinViewModel(),
    authViewModel: AuthViewModel = koinViewModel(),
    editViewModel: EditViewModel = koinViewModel()
) {
    val context = LocalContext.current
    val currentUser = authViewModel.currentUser
    val currentUserId = currentUser?.uid ?: ""
    val currentUserName = currentUser?.displayName ?: stringResource(R.string.anonymous_user)
    val item by viewModel.item.collectAsState()
    val chatMessages by viewModel.chatMessages.collectAsState()
    var message by remember { mutableStateOf("") }

    val editItem by editViewModel.item.collectAsState()
    val isEditSheetOpen = remember { mutableStateOf(false) }
    var title by remember { mutableStateOf(TextFieldValue("")) }
    var description by remember { mutableStateOf(TextFieldValue("")) }

    // Detail-Item laden
    LaunchedEffect(itemId) {
        viewModel.loadItem(itemId)
    }

    // Wenn das Item geladen ist, dann auch fürs Bearbeiten vorbereiten
    LaunchedEffect(item) {
        item?.let {
            viewModel.loadCreatorPhoneNumber(it.userId)
        }
    }

    // Wenn das Edit-BottomSheet geöffnet wird, Item fürs Bearbeiten laden
    LaunchedEffect(isEditSheetOpen.value) {
        if (isEditSheetOpen.value) {
            editViewModel.loadItem(itemId)
        }
    }

    // Wenn das Edit-Item geladen ist, Felder befüllen
    LaunchedEffect(editItem) {
        editItem?.let {
            title = TextFieldValue(it.title)
            description = TextFieldValue(it.description)
        }
    }

    // 👉 Scaffold + Hauptinhalt
    Scaffold(
        topBar = {
            DetailTopAppBar(
                isOwner = item?.userId == currentUserId,
                onBackClick = { navController.popBackStack() },
                onEditClick = {
                    if (item?.userId == currentUserId) {
                        isEditSheetOpen.value = true
                    }
                },
                onShareClick = { viewModel.onShareClick(context, item) }
            )
        }
    ) { paddingValues ->
        if (item == null) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(bottom = 60.dp)
                ) {
                    LazyColumn(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth()
                    ) {
                        item {
                            LostItemCard(
                                item = item!!,
                                onMapClick = {
                                    navController.navigate("map_with_location/${item!!.latitude}/${item!!.longitude}")
                                }
                            )
                        }

                        item {
                            Divider(
                                modifier = Modifier.padding(horizontal = 16.dp),
                                color = MaterialTheme.colorScheme.outlineVariant
                            )

                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 16.dp, vertical = 12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = stringResource(R.string.chat),
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold
                                )

                                if (chatMessages.isNotEmpty()) {
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Surface(
                                        color = MaterialTheme.colorScheme.primary,
                                        shape = MaterialTheme.shapes.small
                                    ) {
                                        Text(
                                            text = "${chatMessages.size}",
                                            style = MaterialTheme.typography.labelSmall,
                                            color = MaterialTheme.colorScheme.onPrimary,
                                            modifier = Modifier.padding(
                                                horizontal = 6.dp,
                                                vertical = 2.dp
                                            )
                                        )
                                    }
                                }
                            }

                            if (chatMessages.isEmpty()) {
                                Text(
                                    text = stringResource(R.string.no_messages_yet),
                                    modifier = Modifier.padding(
                                        horizontal = 16.dp,
                                        vertical = 8.dp
                                    ),
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }

                        items(chatMessages) { msg ->
                            MessageList(
                                message = msg,
                                currentUserId = currentUserId
                            )
                        }

                        item {
                            Spacer(modifier = Modifier.height(8.dp))
                        }
                    }

                    MessageInputField(
                        message = message,
                        onMessageChange = { message = it },
                        onSend = {
                            if (message.isNotBlank()) {
                                viewModel.sendMessage(item!!.id, currentUserName, message)
                                message = ""
                            }
                        }
                    )
                }

                AdMobBanner(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .fillMaxWidth()
                        .height(50.dp)
                )
            }
        }
    }

    // 👉 ModalBottomSheet außerhalb von Scaffold!
    if (isEditSheetOpen.value && editItem != null) {
        ModalBottomSheet(
            onDismissRequest = { isEditSheetOpen.value = false },
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
                                isEditSheetOpen.value = false
                                viewModel.loadItem(itemId)
                                Toast.makeText(
                                    context,
                                    context.getString(R.string.entry_updated),
                                    Toast.LENGTH_SHORT
                                ).show()
                            },
                            onFailure = {
                                isEditSheetOpen.value = false
                                Toast.makeText(
                                    context,
                                    context.getString(R.string.entry_update_failed),
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        )
                    },
                    onBack = { isEditSheetOpen.value = false }
                )
            }
        }
    }
}