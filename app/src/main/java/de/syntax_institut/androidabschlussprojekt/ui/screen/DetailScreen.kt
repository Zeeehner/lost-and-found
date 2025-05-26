package de.syntax_institut.androidabschlussprojekt.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import de.syntax_institut.androidabschlussprojekt.AdMobBanner
import de.syntax_institut.androidabschlussprojekt.ui.viewmodel.AuthViewModel
import de.syntax_institut.androidabschlussprojekt.R
import de.syntax_institut.androidabschlussprojekt.ui.component.detail.DetailTopAppBar
import de.syntax_institut.androidabschlussprojekt.ui.component.detail.LostItemCard
import de.syntax_institut.androidabschlussprojekt.ui.component.detail.MessageInputField
import de.syntax_institut.androidabschlussprojekt.ui.component.detail.MessageList
import de.syntax_institut.androidabschlussprojekt.ui.navigation.Screen
import de.syntax_institut.androidabschlussprojekt.ui.viewmodel.DetailViewModel
import org.koin.androidx.compose.koinViewModel

@Composable
fun DetailScreen(
    itemId: String,
    navController: NavController,
    viewModel: DetailViewModel = koinViewModel(),
    authViewModel: AuthViewModel = koinViewModel()
) {
    val context = LocalContext.current
    val currentUser = authViewModel.currentUser
    val currentUserId = currentUser?.uid ?: ""
    val currentUserName = currentUser?.displayName ?: stringResource(R.string.anonymous_user)
    val item by viewModel.item.collectAsState()
    val chatMessages by viewModel.chatMessages.collectAsState()
    var message by remember { mutableStateOf("") }

    LaunchedEffect(itemId) {
        viewModel.loadItem(itemId)
        viewModel.loadCreatorPhoneNumber(item?.userId ?: "")
    }

    Scaffold(
        topBar = {
            DetailTopAppBar(
                isOwner = item?.userId == currentUserId,
                onBackClick = { navController.popBackStack() },
                onEditClick = { viewModel.onEditClick(navController, itemId) },
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
                                    navController.navigate(
                                        Screen.MapWithLocation.createRoute(item!!.latitude, item!!.longitude)
                                    )
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
                                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                        )
                                    }
                                }
                            }

                            if (chatMessages.isEmpty()) {
                                Text(
                                    text = stringResource(R.string.no_messages_yet),
                                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
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
}