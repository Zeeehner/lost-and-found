package de.syntax_institut.androidabschlussprojekt.ui.viewmodel

import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import de.syntax_institut.androidabschlussprojekt.R
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import de.syntax_institut.androidabschlussprojekt.data.local.model.ChatMessage
import de.syntax_institut.androidabschlussprojekt.data.local.model.Item
import de.syntax_institut.androidabschlussprojekt.repository.DetailRepository
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import de.syntax_institut.androidabschlussprojekt.ui.navigation.Screen

class DetailViewModel(
    private val repository: DetailRepository
) : ViewModel() {

    // Hole aus FB die Daten des Users unter der Tabelle users -> phoneNumber
    private val firestore = FirebaseFirestore.getInstance()

    // tried and not working, phonenumber are always null
    var creatorPhoneNumber by mutableStateOf<String?>(null)
        private set

    private val _item = MutableStateFlow<Item?>(null)
    val item: StateFlow<Item?> = _item

    private val _chatMessages = MutableStateFlow<List<ChatMessage>>(emptyList())
    val chatMessages: StateFlow<List<ChatMessage>> = _chatMessages

    var currentUserId by mutableStateOf<String?>(null)
        private set

    var currentUserName by mutableStateOf<String?>(null)
        private set

    init {
        val user = FirebaseAuth.getInstance().currentUser
        loadCurrentUser(user?.uid ?: "", user?.displayName ?: "Unknown")
    }

    fun loadCurrentUser(userId: String, userName: String) {
        currentUserId = userId
        currentUserName = userName
    }

    fun loadItem(itemId: String) {
        repository.getLostItemById(itemId) {
            _item.value = it
        }
        repository.observeChatMessages(itemId) {
            _chatMessages.value = it
        }
//        loadCreatorPhoneNumber(itemId)
    }

    fun loadCreatorPhoneNumber(userId: String) {
        viewModelScope.launch {
            try {
                val snapshot = firestore.collection("users").document(userId).get().await()
                val phone = snapshot.getString("phoneNumber")
                Log.d("PhoneCheck", "Dokument existiert: ${snapshot.exists()}")
                Log.d("PhoneCheck", "Daten: ${snapshot.data}")
                Log.d("PhoneCheck", "Geladene userId: $userId")
                creatorPhoneNumber = phone
            } catch (e: Exception) {
                Log.e("PhoneCheck", "Fehler beim Laden der Telefonnummer", e)
                creatorPhoneNumber = null
            }
        }
    }

    fun onShareClick(context: Context, item: Item?) {
        item?.let {
            val shareIntent = Intent().apply {
                action = Intent.ACTION_SEND
                putExtra(
                    Intent.EXTRA_TEXT,
                    context.getString(
                        R.string.share_item_format,
                        it.title,
                        it.description,
                        it.locationName ?: ""
                    )
                )
                type = "text/plain"
            }
            context.startActivity(Intent.createChooser(shareIntent, context.getString(R.string.share_via)))
        }
    }

    fun onMapClick(navController: NavController, item: Item) {
        navController.navigate("map/${item.id}")
    }

    fun onEditClick(navController: NavController, itemId: String) {
        navController.navigate(Screen.edit(itemId))
    }

    fun sendMessage(itemId: String, userName: String, text: String) {
        val chat = ChatMessage(userName = userName, message = text)
        repository.sendChatMessage(itemId, chat)
    }
}