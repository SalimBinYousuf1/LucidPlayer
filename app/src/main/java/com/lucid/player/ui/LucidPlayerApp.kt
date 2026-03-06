package com.lucid.player.ui

import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.*
import com.lucid.player.ui.components.MiniPlayer
import com.lucid.player.ui.screens.*
import com.lucid.player.ui.theme.LucidColors
import com.lucid.player.viewmodel.PlayerViewModel

sealed class Dest(val route: String, val icon: ImageVector, val label: String) {
    object Home    : Dest("home",    Icons.Rounded.Home,        "Home")
    object Library : Dest("library", Icons.Rounded.LibraryMusic,"Library")
    object Artists : Dest("artists", Icons.Rounded.Person,      "Artists")
    object Search  : Dest("search",  Icons.Rounded.Search,      "Search")
    object NowPlay : Dest("player",  Icons.Rounded.Home,        "Player")
}

@Composable
fun LucidPlayerApp() {
    val nav  = rememberNavController()
    val vm: PlayerViewModel = hiltViewModel()
    val state by vm.state.collectAsState()
    val current by nav.currentBackStackEntryAsState()
    val route = current?.destination?.route
    val showBar = route != Dest.NowPlay.route
    val tabs = listOf(Dest.Home, Dest.Library, Dest.Artists, Dest.Search)

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = LucidColors.Void,
        bottomBar = {
            if (showBar) {
                Column {
                    AnimatedVisibility(
                        visible = state.currentSong != null,
                        enter = slideInVertically { it } + fadeIn(),
                        exit  = slideOutVertically { it } + fadeOut()
                    ) {
                        MiniPlayer(
                            state = state,
                            onTogglePlay = vm::togglePlayPause,
                            onSkipNext   = vm::skipNext,
                            onSkipPrev   = vm::skipPrev,
                            onClick      = { nav.navigate(Dest.NowPlay.route) }
                        )
                    }
                    NavigationBar(
                        containerColor = LucidColors.Surface,
                        tonalElevation = 0.dp,
                        modifier = Modifier.height(62.dp)
                    ) {
                        tabs.forEach { dest ->
                            val sel = current?.destination?.hierarchy?.any { it.route == dest.route } == true
                            NavigationBarItem(
                                selected = sel,
                                onClick = {
                                    nav.navigate(dest.route) {
                                        popUpTo(nav.graph.findStartDestination().id) { saveState = true }
                                        launchSingleTop = true
                                        restoreState = true
                                    }
                                },
                                icon = { Icon(dest.icon, dest.label, modifier = Modifier.size(22.dp)) },
                                label = { Text(dest.label) },
                                colors = NavigationBarItemDefaults.colors(
                                    selectedIconColor   = LucidColors.Indigo,
                                    selectedTextColor   = LucidColors.Indigo,
                                    unselectedIconColor = LucidColors.Text30,
                                    unselectedTextColor = LucidColors.Text30,
                                    indicatorColor      = LucidColors.Indigo.copy(alpha = 0.15f)
                                )
                            )
                        }
                    }
                }
            }
        }
    ) { pv ->
        NavHost(
            navController = nav,
            startDestination = Dest.Home.route,
            modifier = Modifier.padding(pv),
            enterTransition  = { fadeIn(tween(220)) + slideInHorizontally { 60 } },
            exitTransition   = { fadeOut(tween(180)) },
            popEnterTransition  = { fadeIn(tween(220)) + slideInHorizontally { -60 } },
            popExitTransition   = { fadeOut(tween(180)) }
        ) {
            composable(Dest.Home.route) {
                HomeScreen(vm, onSongClick = { s -> vm.playSong(s); nav.navigate(Dest.NowPlay.route) },
                    onNowPlaying = { nav.navigate(Dest.NowPlay.route) })
            }
            composable(Dest.Library.route) {
                LibraryScreen(vm, onSongClick = { s -> vm.playSong(s); nav.navigate(Dest.NowPlay.route) })
            }
            composable(Dest.Artists.route) {
                ArtistsScreen(vm, onSongClick = { s -> vm.playSong(s); nav.navigate(Dest.NowPlay.route) })
            }
            composable(Dest.Search.route) {
                SearchScreen(vm, onSongClick = { s -> vm.playSong(s); nav.navigate(Dest.NowPlay.route) })
            }
            composable(
                Dest.NowPlay.route,
                enterTransition = { slideInVertically { it } + fadeIn(tween(300)) },
                exitTransition  = { slideOutVertically { it } + fadeOut(tween(250)) },
                popEnterTransition = { slideInVertically { it } + fadeIn(tween(300)) },
                popExitTransition  = { slideOutVertically { it } + fadeOut(tween(250)) },
            ) {
                NowPlayingScreen(vm, onBack = { nav.popBackStack() })
            }
        }
    }
}
