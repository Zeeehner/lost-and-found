package de.syntax_institut.androidabschlussprojekt.di

import androidx.room.Room
import de.syntax_institut.androidabschlussprojekt.data.local.AppDatabase
import de.syntax_institut.androidabschlussprojekt.repository.AuthRepository
import de.syntax_institut.androidabschlussprojekt.repository.ItemCreateRepository
import de.syntax_institut.androidabschlussprojekt.repository.LocationRepository
import de.syntax_institut.androidabschlussprojekt.repository.LostItemRepository
import de.syntax_institut.androidabschlussprojekt.repository.PreferencesRepository
import de.syntax_institut.androidabschlussprojekt.repository.SettingsRepository
import de.syntax_institut.androidabschlussprojekt.ui.viewmodel.AuthViewModel
import de.syntax_institut.androidabschlussprojekt.ui.viewmodel.CreateViewModel
import de.syntax_institut.androidabschlussprojekt.ui.viewmodel.LostItemViewModel
import de.syntax_institut.androidabschlussprojekt.ui.viewmodel.OnboardingViewModel
import de.syntax_institut.androidabschlussprojekt.ui.viewmodel.SettingsViewModel
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module
import org.koin.androidx.viewmodel.dsl.viewModel

val appModule = module {

    // LoginScreen
    viewModel { AuthViewModel(get()) }
    single { AuthRepository() }

    // OnboardingScreen
    viewModel { OnboardingViewModel() }

    // ListScreen
    viewModel { LostItemViewModel(get()) }
    single { LostItemRepository() }

    // CreateScreen
    viewModel { CreateViewModel(get()) }
    single { ItemCreateRepository() }

    // SettingsScreen
    viewModel { SettingsViewModel(get(), get(), get()) }
    single { SettingsRepository() }
    single { LocationRepository() }
    single { PreferencesRepository(get()) }

    // Room
    single { Room.databaseBuilder(androidContext(), AppDatabase::class.java, "app_db").build() }
    single { get<AppDatabase>().settingsDao() }


}
