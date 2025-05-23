package de.syntax_institut.androidabschlussprojekt.ui.component.settings

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Phone
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewModelScope
import de.syntax_institut.androidabschlussprojekt.ui.viewmodel.AuthViewModel
import de.syntax_institut.androidabschlussprojekt.R
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

@Composable
fun PhoneNumberSection(authViewModel: AuthViewModel) {
    val phoneNumber by authViewModel.phoneNumber.collectAsState()
    var isEditing by remember { mutableStateOf(false) }
    var inputNumber by remember { mutableStateOf(phoneNumber ?: "") }
    val isInputValid = remember(inputNumber) {
        inputNumber.isBlank() || Regex("""^(\+\d{1,3}[- ]?)?\d{10,14}$""").matches(inputNumber.replace("\\s".toRegex(), ""))
    }

    Column(modifier = Modifier.fillMaxWidth()) {
        SettingsItem(
            icon = Icons.Outlined.Phone,
            title = stringResource(R.string.phone_number),
            subtitle = if (phoneNumber.isNullOrEmpty())
                stringResource(R.string.no_phone_number)
            else phoneNumber,
            onClick = { isEditing = !isEditing }
        )

        AnimatedVisibility(
            visible = isEditing,
            enter = fadeIn() + expandVertically(),
            exit = fadeOut() + shrinkVertically()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                OutlinedTextField(
                    value = inputNumber,
                    onValueChange = { inputNumber = it },
                    label = { Text(stringResource(R.string.enter_phone_number)) },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Phone,
                        imeAction = ImeAction.Done
                    ),
                    modifier = Modifier.fillMaxWidth(),
                    isError = !isInputValid && inputNumber.isNotBlank()
                )

                Spacer(modifier = Modifier.height(12.dp))

                Button(
                    onClick = {
                        if (isInputValid) {
                            authViewModel.viewModelScope.launch {
                                authViewModel.currentUser?.uid?.let { uid ->
                                    val firestore = com.google.firebase.firestore.FirebaseFirestore.getInstance()
                                    firestore.collection("users").document(uid)
                                        .set(mapOf("phoneNumber" to inputNumber.takeIf { it.isNotBlank() }),
                                            com.google.firebase.firestore.SetOptions.merge()
                                        ).await()
                                    authViewModel.loadPhoneNumber()
                                    isEditing = false
                                }
                            }
                        }
                    },
                    enabled = isInputValid,
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(stringResource(R.string.save))
                }
            }
        }
    }
}
