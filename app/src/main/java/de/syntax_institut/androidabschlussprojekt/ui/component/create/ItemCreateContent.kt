package de.syntax_institut.androidabschlussprojekt.ui.component.create

import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import de.syntax_institut.androidabschlussprojekt.AdMobBanner
import de.syntax_institut.androidabschlussprojekt.R
import de.syntax_institut.androidabschlussprojekt.ui.viewmodel.AuthViewModel
import de.syntax_institut.androidabschlussprojekt.ui.viewmodel.CreateViewModel
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel

/**
 * Bildschirminhalt zum Erstellen eines neuen Items.
 *
 * Zeigt ein Formular zur Eingabe von Titel, Beschreibung, Bild und Standort.
 * Unterstützt automatische Standortermittlung sowie Bildauswahl aus der Galerie.
 *
 * @param navController Zur Navigation zurück nach erfolgreicher Erstellung.
 * @param authViewModel ViewModel für aktuelle Benutzerinfos.
 * @param viewModel ViewModel für die Formularlogik und Datenverarbeitung.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ItemCreateContent(
    navController: NavController,
    authViewModel: AuthViewModel = koinViewModel(),
    viewModel: CreateViewModel = koinViewModel()
) {
    val context = LocalContext.current
    val currentUserName =
        authViewModel.getCurrentUserName() ?: stringResource(R.string.anonymous_user)

    val formState by viewModel.formState.collectAsState()
    val bitmap by viewModel.bitmap.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val success by viewModel.success.collectAsState()

    val scrollState = rememberScrollState()

    // Debounce Mechanismus für den Back-Button
    val coroutineScope = rememberCoroutineScope()
    var backPressedOnce by remember { mutableStateOf(false) }

    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            val bmp: Bitmap? = if (Build.VERSION.SDK_INT < 28) {
                MediaStore.Images.Media.getBitmap(context.contentResolver, it)
            } else {
                val source = ImageDecoder.createSource(context.contentResolver, it)
                ImageDecoder.decodeBitmap(source)
            }
            viewModel.setImage(it, bmp)
        }
    }

    LaunchedEffect(success) {
        success?.let {
            val message = if (it) {
                context.getString(R.string.entry_created)
            } else {
                context.getString(R.string.entry_failed)
            }
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
            if (it) navController.popBackStack()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.create_entry)) },
                navigationIcon = {
                    // Debounce Mechanismus
                    IconButton(onClick = {
                        if (!backPressedOnce) {
                            backPressedOnce = true
                            navController.popBackStack()
                            coroutineScope.launch {
                                kotlinx.coroutines.delay(1000)
                                backPressedOnce = false
                            }
                        }
                    }) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = stringResource(R.string.back)
                        )
                    }
                }
            )
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(bottom = 60.dp)
                    .verticalScroll(scrollState)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                ItemStatusSelector(
                    status = formState.status,
                    onStatusChange = { viewModel.updateField { copy(status = it) } }
                )

                ItemImagePicker(
                    bitmap = bitmap,
                    onClick = { imagePickerLauncher.launch("image/*") }
                )

                LostItemForm(
                    title = formState.title,
                    description = formState.description,
                    onTitleChange = { viewModel.updateField { copy(title = it) } },
                    onDescChange = { viewModel.updateField { copy(description = it) } }
                )

                ItemLocationSection(
                    location = formState.location,
                    latitude = formState.latitude,
                    longitude = formState.longitude,
                    onLocationChange = { viewModel.updateField { copy(location = it) } },
                    onLatChange = { viewModel.updateField { copy(latitude = it) } },
                    onLongChange = { viewModel.updateField { copy(longitude = it) } },
                    showDetails = formState.showLocationDetails,
                    onToggleDetails = { viewModel.toggleLocationDetails() },
                    onAutoLocate = { viewModel.fetchCurrentLocation(context) }
                )

                Button(
                    onClick = {
                        viewModel.submit(
                            userId = authViewModel.getCurrentUserId() ?: "unknown",
                            userName = currentUserName
                        ) {}
                    },
                    enabled = !isLoading,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(stringResource(R.string.save))
                }
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