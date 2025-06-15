package de.syntax_institut.androidabschlussprojekt.ui.component.login

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.*
import androidx.compose.ui.unit.dp
import de.syntax_institut.androidabschlussprojekt.ui.util.spacing
import de.syntax_institut.androidabschlussprojekt.R
import de.syntax_institut.androidabschlussprojekt.ui.viewmodel.AuthViewModel

/**
 * Login- und Registrierungsformular.
 *
 * @param authViewModel Das zugehörige ViewModel für Authentifizierung.
 * @param isLoading Gibt an, ob ein Ladevorgang aktiv ist.
 */
@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun LoginForm(authViewModel: AuthViewModel, isLoading: Boolean) {
    val context = LocalContext.current
    val focusManager = LocalFocusManager.current
    val keyboardController = LocalSoftwareKeyboardController.current

    val emailFocusRequester = remember { FocusRequester() }
    val passwordFocusRequester = remember { FocusRequester() }
    val nameFocusRequester = remember { FocusRequester() }

    val isRegistrationMode by authViewModel.isRegistrationMode.collectAsState()

    var isEmailError by remember { mutableStateOf(false) }
    var isPasswordError by remember { mutableStateOf(false) }
    var passwordVisible by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(modifier = Modifier.padding(MaterialTheme.spacing.medium)) {

            // Namefeld nur im Registrierungsmodus sichtbar
            AnimatedVisibility(visible = isRegistrationMode) {
                Column {
                    OutlinedTextField(
                        value = authViewModel.nameInput,
                        onValueChange = { authViewModel.nameInput = it },
                        label = { Text(stringResource(R.string.name)) },
                        leadingIcon = { Icon(Icons.Default.Person, contentDescription = null) },
                        keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Next),
                        keyboardActions = KeyboardActions(onNext = { emailFocusRequester.requestFocus() }),
                        modifier = Modifier
                            .fillMaxWidth()
                            .focusRequester(nameFocusRequester),
                        singleLine = true
                    )
                    Spacer(modifier = Modifier.height(MaterialTheme.spacing.medium))
                }
            }

            // E-Mail Feld
            OutlinedTextField(
                value = authViewModel.emailInput,
                onValueChange = {
                    authViewModel.emailInput = it
                    isEmailError = false
                },
                label = { Text(stringResource(R.string.email)) },
                leadingIcon = { Icon(Icons.Default.Email, contentDescription = null) },
                isError = isEmailError,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email, imeAction = ImeAction.Next),
                keyboardActions = KeyboardActions(onNext = { passwordFocusRequester.requestFocus() }),
                modifier = Modifier
                    .fillMaxWidth()
                    .focusRequester(emailFocusRequester),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(MaterialTheme.spacing.medium))

            // Passwortfeld mit Toggle für Sichtbarkeit
            OutlinedTextField(
                value = authViewModel.passwordInput,
                onValueChange = {
                    authViewModel.passwordInput = it
                    isPasswordError = false
                },
                label = { Text(stringResource(R.string.password)) },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Lock,
                        contentDescription = null
                    )
                },
                trailingIcon = {
                    IconButton(onClick = { passwordVisible = !passwordVisible }) {
                        Icon(
                            painter = painterResource(
                                id = if (passwordVisible)
                                    R.drawable.eye
                                else
                                    R.drawable.eye_slash
                            ),
                            contentDescription = if (passwordVisible)
                                "Hide Password"
                            else
                                "Show Password"
                        )
                    }
                },
                visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                isError = isPasswordError,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Password,
                    imeAction = ImeAction.Done
                ),
                keyboardActions = KeyboardActions(onDone = {
                    keyboardController?.hide()
                    focusManager.clearFocus()
                    if (authViewModel.validateInputs()) {
                        authViewModel.onLoginClick(context)
                    } else {
                        isEmailError = !authViewModel.isValidEmail()
                        isPasswordError = !authViewModel.isValidPassword()
                    }
                }),
                modifier = Modifier
                    .fillMaxWidth()
                    .focusRequester(passwordFocusRequester),
                singleLine = true
            )


            Spacer(modifier = Modifier.height(MaterialTheme.spacing.large))

            // Login/Register Button mit Ladeanzeige
            Button(
                onClick = {
                    keyboardController?.hide()
                    focusManager.clearFocus()
                    if (authViewModel.validateInputs()) {
                        authViewModel.onLoginClick(context)
                    } else {
                        isEmailError = !authViewModel.isValidEmail()
                        isPasswordError = !authViewModel.isValidPassword()
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                enabled = !isLoading
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                } else {
                    Text(text = stringResource(if (isRegistrationMode) R.string.register else R.string.login))
                }
            }
        }
    }
}