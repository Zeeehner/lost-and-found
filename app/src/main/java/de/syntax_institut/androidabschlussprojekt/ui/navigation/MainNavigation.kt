package de.syntax_institut.androidabschlussprojekt.ui.navigation

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.MailOutline
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.*
import de.syntax_institut.androidabschlussprojekt.R
import de.syntax_institut.androidabschlussprojekt.ui.screen.ListScreen
import de.syntax_institut.androidabschlussprojekt.ui.screen.PrivateChatListScreen
import de.syntax_institut.androidabschlussprojekt.ui.screen.SettingsScreen
import de.syntax_institut.androidabschlussprojekt.ui.viewmodel.AuthViewModel
import de.syntax_institut.androidabschlussprojekt.ui.viewmodel.PrivateChatViewModel
import org.koin.androidx.compose.koinViewModel

sealed class BottomNavScreen(val route: String, val icon: ImageVector) {
    object Home : BottomNavScreen("home", Icons.Default.Home)
    object Chat : BottomNavScreen("chat", Icons.Default.MailOutline)
    object Settings : BottomNavScreen("settings", Icons.Default.Settings)
}

@Composable
fun getLabelRes(screen: BottomNavScreen): Int = when (screen) {
    BottomNavScreen.Home -> R.string.nav_home
    BottomNavScreen.Chat -> R.string.nav_chat
    BottomNavScreen.Settings -> R.string.nav_settings
}

@Composable
fun MainNavigation(rootNavController: NavHostController, modifier: Modifier = Modifier) {
    val bottomNavController = rememberNavController()
    val items = listOf(BottomNavScreen.Home, BottomNavScreen.Chat, BottomNavScreen.Settings)
    val navBackStackEntry by bottomNavController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val authViewModel: AuthViewModel = koinViewModel()
    val currentUserId = authViewModel.currentUser?.uid ?: ""

    val chatViewModel: PrivateChatViewModel = koinViewModel()
    val unreadCount by chatViewModel.unreadCount.collectAsState()

    LaunchedEffect(currentUserId) {
        if (currentUserId.isNotEmpty()) {
            chatViewModel.loadChatPartners(currentUserId)
        }
    }

    Box(modifier = modifier.fillMaxSize()) {
        Column(modifier = Modifier.fillMaxSize()) {
            Box(modifier = Modifier.weight(1f)) {
                NavHost(
                    navController = bottomNavController,
                    startDestination = BottomNavScreen.Home.route
                ) {
                    composable(BottomNavScreen.Home.route) {
                        ListScreen(rootNavController)
                    }
                    composable(BottomNavScreen.Chat.route) {
                        PrivateChatListScreen(
                            currentUserId = currentUserId,
                            onChatSelected = { partner ->
                                chatViewModel.resetUnread(currentUserId, partner.userId)
                                rootNavController.navigate(
                                    Screen.PrivateChat.createRoute(
                                        partnerName = partner.userName,
                                        partnerId = partner.userId
                                    )
                                )
                            },
                            navController = rootNavController,
                            viewModel = chatViewModel
                        )
                    }
                    composable(BottomNavScreen.Settings.route) {
                        SettingsScreen(rootNavController)
                    }
                }
            }

            NavigationBar {
                items.forEach { screen ->
                    NavigationBarItem(
                        icon = {
                            if (screen == BottomNavScreen.Chat && unreadCount > 0) {
                                BadgedBox(
                                    badge = {
                                        Badge {
                                            Text(unreadCount.toString())
                                        }
                                    }
                                ) {
                                    Icon(screen.icon, contentDescription = stringResource(getLabelRes(screen)))
                                }
                            } else {
                                Icon(screen.icon, contentDescription = stringResource(getLabelRes(screen)))
                            }
                        },
                        label = { Text(stringResource(getLabelRes(screen))) },
                        selected = currentRoute == screen.route,
                        onClick = {
                            bottomNavController.navigate(screen.route) {
                                popUpTo(bottomNavController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    )
                }
            }
        }
    }
}
