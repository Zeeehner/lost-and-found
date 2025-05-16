package de.syntax_institut.androidabschlussprojekt.ui.component.login

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.*
import androidx.compose.ui.unit.dp
import de.syntax_institut.androidabschlussprojekt.ui.util.spacing
import de.syntax_institut.androidabschlussprojekt.R
import de.syntax_institut.androidabschlussprojekt.ui.viewmodel.AuthViewModel

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
            AnimatedVisibility(visible = isRegistrationMode) {
                Column {
                    OutlinedTextField(
                        value = authViewModel.nameInput,
                        onValueChange = { authViewModel.nameInput = it },
                        label = { Text(stringResource(R.string.name)) },
                        leadingIcon = { Icon(Icons.Default.Person, contentDescription = null) },
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Text,
                            imeAction = ImeAction.Next
                        ),
                        keyboardActions = KeyboardActions(onNext = { emailFocusRequester.requestFocus() }),
                        modifier = Modifier
                            .fillMaxWidth()
                            .focusRequester(nameFocusRequester),
                        singleLine = true
                    )
                    Spacer(modifier = Modifier.height(MaterialTheme.spacing.medium))
                }
            }

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

            OutlinedTextField(
                value = authViewModel.passwordInput,
                onValueChange = {
                    authViewModel.passwordInput = it
                    isPasswordError = false
                },
                label = { Text(stringResource(R.string.password)) },
                leadingIcon = { Icon(Icons.Default.PlayArrow, contentDescription = null) },
                trailingIcon = {
                    IconButton(onClick = { passwordVisible = !passwordVisible }) {
                        Icon(
                            imageVector = if (passwordVisible) Icons.Default.Close else Icons.Default.Lock,
                            contentDescription = null
                        )
                    }
                },
                visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                isError = isPasswordError,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password, imeAction = ImeAction.Done),
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
