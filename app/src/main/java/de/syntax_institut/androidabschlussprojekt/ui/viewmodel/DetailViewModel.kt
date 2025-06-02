package de.syntax_institut.androidabschlussprojekt.ui.viewmodel

import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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

/**
 * ViewModel für die Detailansicht eines Items.
 *
 * Verantwortlich für das Laden des Items, der zugehörigen Chatnachrichten
 * und der Telefonnummer des Erstellers.
 *
 * @property repository Repository zur Datenbeschaffung.
 */
class DetailViewModel(
    private val repository: DetailRepository
) : ViewModel() {

    // Firebase Firestore Instanz
    private val firestore = FirebaseFirestore.getInstance()

    /** Telefonnummer des Erstellers */
    var creatorPhoneNumber by mutableStateOf<String?>(null)
        private set

    private val _item = MutableStateFlow<Item?>(null)
    /** Aktuelles Item als StateFlow */
    val item: StateFlow<Item?> = _item

    private val _chatMessages = MutableStateFlow<List<ChatMessage>>(emptyList())
    /** Chatnachrichten zum Item als StateFlow */
    val chatMessages: StateFlow<List<ChatMessage>> = _chatMessages

    /** Aktuelle Nutzer-ID */
    var currentUserId by mutableStateOf<String?>(null)
        private set

    /** Aktueller Nutzername */
    var currentUserName by mutableStateOf<String?>(null)
        private set

    init {
        val user = FirebaseAuth.getInstance().currentUser
        loadCurrentUser(user?.uid ?: "", user?.displayName ?: "Unknown")
    }

    /**
     * Setzt die aktuelle Nutzer-ID und den Namen.
     */
    fun loadCurrentUser(userId: String, userName: String) {
        currentUserId = userId
        currentUserName = userName
    }

    /**
     * Lädt das Item mit zugehörigen Chatnachrichten.
     *
     * @param itemId Die ID des Items.
     */
    fun loadItem(itemId: String) {
        repository.getLostItemById(itemId) {
            _item.value = it
        }
        repository.observeChatMessages(itemId) {
            _chatMessages.value = it
        }
        loadCreatorPhoneNumber(itemId)
    }

    /**
     * Lädt die Telefonnummer des Erstellers aus Firestore.
     *
     * @param userId ID des Erstellers.
     */
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

    /**
     * Erstellt einen Share-Intent zum Teilen des Items.
     *
     * @param context Kontext zum Starten der Activity.
     * @param item Das zu teilende Item.
     */
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

    /**
     * Sendet eine Chatnachricht zum Item.
     *
     * @param itemId Die ID des Items.
     * @param userName Name des Nutzers, der die Nachricht sendet.
     * @param text Inhalt der Nachricht.
     */
    fun sendMessage(itemId: String, userName: String, text: String) {
        val chat = ChatMessage(userName = userName, message = text)
        repository.sendChatMessage(itemId, chat)
    }
}