package com.lucid.player.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Album
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.lucid.player.ui.components.MiniPlayer
import com.lucid.player.ui.screens.*
import com.lucid.player.ui.theme.NeonPurple
import com.lucid.player.ui.theme.Surface1
import com.lucid.player.ui.theme.TextSecondary
import com.lucid.player.ui.theme.Void
import com.lucid.player.viewmodel.PlayerViewModel

sealed class Screen(val route: String, val icon: androidx.compose.ui.graphics.vector.ImageVector, val label: String) {
    object Home : Screen("home", Icons.Default.Home, "Home")
    object Library : Screen("library", Icons.Default.Album, "Library")
    object Artists : Screen("artists", Icons.Default.Person, "Artists")
    object Search : Screen("search", Icons.Default.Search, "Search")
    object NowPlaying : Screen("now_playing", Icons.Default.Home, "Now Playing")
}

@Composable
fun LucidPlayerApp() {
    val navController = rememberNavController()
    val viewModel: PlayerViewModel = hiltViewModel()
    val playerState by viewModel.playerState.collectAsState()

    val bottomNavItems = listOf(Screen.Home, Screen.Library, Screen.Artists, Screen.Search)
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination
    val showBottomBar = currentDestination?.route != Screen.NowPlaying.route

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = Void,
        bottomBar = {
            if (showBottomBar) {
                Column {
                    // Mini Player above bottom nav
                    if (playerState.currentSong != null) {
                        MiniPlayer(
                            playerState = playerState,
                            onTogglePlay = viewModel::togglePlayPause,
                            onSkipNext = viewModel::skipNext,
                            onClick = { navController.navigate(Screen.NowPlaying.route) }
                        )
                    }
                    NavigationBar(
                        containerColor = Surface1,
                        tonalElevation = 0.dp
                    ) {
                        bottomNavItems.forEach { screen ->
                            val selected = currentDestination?.hierarchy?.any { it.route == screen.route } == true
                            NavigationBarItem(
                                selected = selected,
                                onClick = {
                                    navController.navigate(screen.route) {
                                        popUpTo(navController.graph.findStartDestination().id) {
                                            saveState = true
                                        }
                                        launchSingleTop = true
                                        restoreState = true
                                    }
                                },
                                icon = {
                                    Icon(
                                        imageVector = screen.icon,
                                        contentDescription = screen.label
                                    )
                                },
                                label = {
                                    Text(
                                        text = screen.label,
                                        style = MaterialTheme.typography.labelSmall
                                    )
                                },
                                colors = NavigationBarItemDefaults.colors(
                                    selectedIconColor = NeonPurple,
                                    selectedTextColor = NeonPurple,
                                    unselectedIconColor = TextSecondary,
                                    unselectedTextColor = TextSecondary,
                                    indicatorColor = NeonPurple.copy(alpha = 0.15f)
                                )
                            )
                        }
                    }
                }
            }
        }
    ) { paddingValues ->
        NavHost(
            navController = navController,
            startDestination = Screen.Home.route,
            modifier = Modifier.padding(paddingValues)
        ) {
            composable(Screen.Home.route) {
                HomeScreen(
                    viewModel = viewModel,
                    onSongClick = { song ->
                        viewModel.playSong(song)
                        navController.navigate(Screen.NowPlaying.route)
                    },
                    onNavigateToNowPlaying = { navController.navigate(Screen.NowPlaying.route) }
                )
            }
            composable(Screen.Library.route) {
                LibraryScreen(
                    viewModel = viewModel,
                    onSongClick = { song ->
                        viewModel.playSong(song)
                        navController.navigate(Screen.NowPlaying.route)
                    }
                )
            }
            composable(Screen.Artists.route) {
                ArtistsScreen(
                    viewModel = viewModel,
                    onSongClick = { song ->
                        viewModel.playSong(song)
                        navController.navigate(Screen.NowPlaying.route)
                    }
                )
            }
            composable(Screen.Search.route) {
                SearchScreen(
                    viewModel = viewModel,
                    onSongClick = { song ->
                        viewModel.playSong(song)
                        navController.navigate(Screen.NowPlaying.route)
                    }
                )
            }
            composable(Screen.NowPlaying.route) {
                NowPlayingScreen(
                    viewModel = viewModel,
                    onNavigateBack = { navController.popBackStack() }
                )
            }
        }
    }
}

@Composable
fun Column(content: @Composable () -> Unit) {
    androidx.compose.foundation.layout.Column { content() }
}
