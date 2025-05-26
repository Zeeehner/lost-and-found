package de.syntax_institut.androidabschlussprojekt.ui.screen

import android.widget.Toast
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import de.syntax_institut.androidabschlussprojekt.AdMobBanner
import de.syntax_institut.androidabschlussprojekt.R
import de.syntax_institut.androidabschlussprojekt.ui.component.edit.EditScreenContent
import de.syntax_institut.androidabschlussprojekt.ui.viewmodel.EditViewModel
import org.koin.androidx.compose.koinViewModel

@Composable
fun EditScreen(
    itemId: String,
    navController: NavController,
    viewModel: EditViewModel = koinViewModel()
) {
    val context = LocalContext.current
    val item by viewModel.item.collectAsState()

    var title by remember { mutableStateOf(TextFieldValue("")) }
    var description by remember { mutableStateOf(TextFieldValue("")) }

    LaunchedEffect(itemId) {
        viewModel.loadItem(itemId)
    }

    LaunchedEffect(item) {
        item?.let {
            title = TextFieldValue(it.title)
            description = TextFieldValue(it.description)
        }
    }

    AdMobBanner(modifier = Modifier.fillMaxWidth().height(50.dp))

    EditScreenContent(
        title = title,
        onTitleChange = { title = it },
        description = description,
        onDescriptionChange = { description = it },
        onSave = {
            viewModel.updateItem(
                title = title.text,
                description = description.text,
                onSuccess = {
                    Toast.makeText(
                        context,
                        context.getString(R.string.entry_updated),
                        Toast.LENGTH_SHORT
                    ).show()
                    navController.popBackStack()
                },
                onFailure = {
                    Toast.makeText(
                        context,
                        context.getString(R.string.entry_update_failed),
                        Toast.LENGTH_SHORT
                    ).show()
                }
            )
        },
        onBack = { navController.popBackStack() }
    )
}