package de.syntax_institut.androidabschlussprojekt.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import de.syntax_institut.androidabschlussprojekt.AdMobBanner
import de.syntax_institut.androidabschlussprojekt.R
import de.syntax_institut.androidabschlussprojekt.data.local.model.ChatPartner
import de.syntax_institut.androidabschlussprojekt.data.local.model.PrivateChatMessage
import de.syntax_institut.androidabschlussprojekt.ui.component.chat.PrivateMessageBubble
import de.syntax_institut.androidabschlussprojekt.ui.viewmodel.PrivateChatViewModel
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel

/**
 * Detailansicht eines privaten Chats zwischen zwei Benutzern.
 *
 * Zeigt die Nachrichten im Chatverlauf an, einen Eingabebereich zum Schreiben neuer Nachrichten
 * sowie Informationen zum Chatpartner (Name, letzter Online-Status).
 *
 * @param currentUserId Die ID des aktuell angemeldeten Benutzers.
 * @param currentUserName Der Anzeigename des aktuell angemeldeten Benutzers.
 * @param chatPartner Der Chatpartner mit Informationen zu Nutzer-ID und Namen.
 * @param onBackPressed Callback, wenn die Zurück-Schaltfläche gedrückt wird.
 * @param viewModel Das ViewModel zur Verwaltung des Chatverlaufs und der Nachrichten (Standard: Koin Injection).
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PrivateChatDetailScreen(
    currentUserId: String,
    currentUserName: String,
    chatPartner: ChatPartner,
    onBackPressed: () -> Unit,
    viewModel: PrivateChatViewModel = koinViewModel()
) {
    val messages by viewModel.messages.collectAsState()
    var input by remember { mutableStateOf("") }
    val listState = rememberLazyListState()
    val lastSeen by viewModel.partnerLastSeen.collectAsState()

    // Debounce Mechanismus für den Zurück-Button
    val coroutineScope = rememberCoroutineScope()
    var backPressedOnce by remember { mutableStateOf(false) }

    // Formatierung des Zeitpunkts "zuletzt online"
    val lastSeenFormatted = remember(lastSeen) {
        android.text.format.DateUtils.getRelativeTimeSpanString(lastSeen)
    }

    // Aktualisiere "last seen" des aktuellen Benutzers alle 10 Sekunden
    LaunchedEffect(currentUserId) {
        viewModel.updateMyLastSeen(currentUserId)
        kotlinx.coroutines.delay(10_000)
    }

    // Lade Nachrichten und beobachte "last seen" des Partners, wenn sich der Partner ändert
    LaunchedEffect(chatPartner.userId) {
        viewModel.loadMessages(currentUserId, chatPartner.userId)
        viewModel.observePartnerLastSeen(currentUserId, chatPartner.userId)
    }

    // Scroll automatisch zum neuesten Nachrichteneintrag
    LaunchedEffect(messages.size) {
        if (messages.isNotEmpty()) {
            listState.animateScrollToItem(messages.size - 1)
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = 50.dp)
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            MaterialTheme.colorScheme.surface,
                            MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
                        )
                    )
                )
        ) {
            // Header mit Zurück-Button und Partnerinfo
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = MaterialTheme.colorScheme.primaryContainer,
                shadowElevation = 4.dp
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Debounce Mechanismus
                    IconButton(
                        onClick = {
                            if (!backPressedOnce) {
                                backPressedOnce = true
                                onBackPressed()
                                coroutineScope.launch {
                                    kotlinx.coroutines.delay(1000)
                                    backPressedOnce = false
                                }
                            }
                        },
                        modifier = Modifier.size(40.dp)
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(id = R.string.back),
                            tint = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.primary),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = chatPartner.userName.take(1).uppercase(),
                            color = MaterialTheme.colorScheme.onPrimary,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Column {
                        Text(
                            text = chatPartner.userName,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                        Text(
                            text = "Last seen: $lastSeenFormatted",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f)
                        )
                    }
                }
            }

            // Nachrichtenliste mit Sicherheitshinweis
            LazyColumn(
                state = listState,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                contentPadding = PaddingValues(vertical = 16.dp)
            ) {
                // Sicherheitshinweis oben im Chat
                item {
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 12.dp, vertical = 8.dp),
                        shape = RoundedCornerShape(12.dp),
                        color = MaterialTheme.colorScheme.errorContainer,
                        tonalElevation = 2.dp,
                        shadowElevation = 4.dp
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Warning,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onErrorContainer
                            )
                            Text(
                                text = stringResource(id = R.string.chat_security_notice),
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onErrorContainer
                            )
                        }
                    }
                }

                // Chat-Nachrichten darstellen
                items(messages) { msg ->
                    PrivateMessageBubble(
                        message = msg,
                        isMe = msg.senderId == currentUserId
                    )
                }
            }

            // Eingabebereich zum Verfassen neuer Nachrichten
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = MaterialTheme.colorScheme.surface,
                shadowElevation = 8.dp
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.Bottom,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedTextField(
                        value = input,
                        onValueChange = { input = it },
                        modifier = Modifier.weight(1f),
                        placeholder = {
                            Text(
                                stringResource(id = R.string.write_message),
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                            )
                        },
                        shape = RoundedCornerShape(24.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = MaterialTheme.colorScheme.primary,
                            unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)
                        ),
                        maxLines = 4
                    )

                    FloatingActionButton(
                        onClick = {
                            if (input.isNotBlank()) {
                                viewModel.sendMessage(
                                    message = PrivateChatMessage(
                                        senderId = currentUserId,
                                        receiverId = chatPartner.userId,
                                        message = input.trim()
                                    ),
                                    senderName = currentUserName,
                                    receiverName = chatPartner.userName
                                )
                                input = ""
                            }
                        },
                        modifier = Modifier.size(48.dp),
                        containerColor = if (input.isNotBlank())
                            MaterialTheme.colorScheme.primary
                        else
                            MaterialTheme.colorScheme.surfaceVariant,
                        contentColor = if (input.isNotBlank())
                            MaterialTheme.colorScheme.onPrimary
                        else
                            MaterialTheme.colorScheme.onSurfaceVariant
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.Send,
                            contentDescription = stringResource(id = R.string.send),
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }
        }

        // Werbebanner am unteren Bildschirmrand
        AdMobBanner(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .height(50.dp)
        )
    }
}