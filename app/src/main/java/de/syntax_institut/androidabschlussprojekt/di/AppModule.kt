package de.syntax_institut.androidabschlussprojekt.di

import androidx.room.Room
import de.syntax_institut.androidabschlussprojekt.data.local.AppDatabase
import de.syntax_institut.androidabschlussprojekt.repository.*
import de.syntax_institut.androidabschlussprojekt.ui.viewmodel.*
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module
import org.koin.androidx.viewmodel.dsl.viewModel

/**
 * Koin-Modul zur Bereitstellung aller Abhängigkeiten der App, wie ViewModels, Repositories und Datenbankzugriffe.
 */
val appModule = module {

    // --- LoginScreen ---
    viewModel { AuthViewModel(get()) }
    single { AuthRepository() }

    // --- OnboardingScreen ---
    viewModel { OnboardingViewModel() }
    single { UserRepository() }

    // --- ListScreen (z.B. verlorene Gegenstände) ---
    viewModel { LostItemViewModel(get()) }
    single { LostItemRepository() }

    // --- DetailScreen ---
    viewModel { DetailViewModel(get()) }
    single { DetailRepository() }

    // --- MapScreen ---
    viewModel { MapViewModel(get()) }
    single { MapRepository() }

    // --- CreateScreen ---
    viewModel { CreateViewModel(get()) }
    single { ItemCreateRepository() }

    // --- EditScreen ---
    viewModel { EditViewModel(get()) }
    single { EditRepository() }

    // --- ChatScreens ---
    viewModel { ChatViewModel(get()) }
    single { ChatRepository() }

    viewModel { PrivateChatViewModel(get()) }
    single { PrivateChatRepository() }

    // --- SettingsScreen ---
    viewModel { SettingsViewModel(get(), get(), get()) }
    single { SettingsRepository() }
    single { LocationRepository() }
    single { PreferencesRepository(get()) }

    // --- Room-Datenbank ---
    single {
        Room.databaseBuilder(
            androidContext(),
            AppDatabase::class.java,
            "app_db"
        ).build()
    }
    single { get<AppDatabase>().settingsDao() }
}