package de.syntax_institut.androidabschlussprojekt.di

import de.syntax_institut.androidabschlussprojekt.repository.AuthRepository
import de.syntax_institut.androidabschlussprojekt.ui.viewmodel.AuthViewModel
import de.syntax_institut.androidabschlussprojekt.ui.viewmodel.OnboardingViewModel
import de.syntax_institut.androidabschlussprojekt.ui.viewmodel.SettingsViewModel
import org.koin.dsl.module
import org.koin.androidx.viewmodel.dsl.viewModel

val appModule = module {

    // LoginScreen
    viewModel { AuthViewModel(get()) }
    single { AuthRepository() }

    // OnboardingScreen
    viewModel { OnboardingViewModel() }

    // SettingsScreen
    viewModel { SettingsViewModel(get()) }


}
