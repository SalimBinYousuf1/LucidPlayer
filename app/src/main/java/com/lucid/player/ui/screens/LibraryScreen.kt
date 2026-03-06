package com.lucid.player.ui.screens

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.*
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.lucid.player.data.models.Album
import com.lucid.player.data.models.Song
import com.lucid.player.ui.components.*
import com.lucid.player.ui.theme.LucidColors
import com.lucid.player.viewmodel.PlayerViewModel

@Composable
fun LibraryScreen(vm: PlayerViewModel, onSongClick: (Song) -> Unit) {
    val albums  by vm.albums.collectAsState()
    val songs   by vm.songs.collectAsState()
    val state   by vm.state.collectAsState()
    val favIds  by vm.favIds.collectAsState()
    var tab     by remember { mutableIntStateOf(0) }
    val tabs    = listOf("Albums", "Songs", "Playlists")

    Column(Modifier.fillMaxSize().background(LucidColors.Void).statusBarsPadding()) {
        // Header
        Text("Library", style = MaterialTheme.typography.headlineLarge,
            color = LucidColors.Text100, fontWeight = FontWeight.Black,
            modifier = Modifier.padding(horizontal = 22.dp, vertical = 20.dp))

        // Pill tabs
        Row(Modifier.padding(horizontal = 20.dp).clip(RoundedCornerShape(14.dp))
            .background(LucidColors.Surface).padding(4.dp)) {
            tabs.forEachIndexed { i, label ->
                Box(
                    Modifier.weight(1f).clip(RoundedCornerShape(11.dp))
                        .background(if (tab == i) LucidColors.Indigo else Color.Transparent)
                        .clickable { tab = i }
                        .padding(vertical = 10.dp),
                    Alignment.Center
                ) {
                    Text(label, style = MaterialTheme.typography.labelLarge,
                        color = if (tab == i) Color.White else LucidColors.Text50,
                        fontWeight = if (tab == i) FontWeight.SemiBold else FontWeight.Normal)
                }
            }
        }
        Spacer(Modifier.height(16.dp))

        when (tab) {
            0 -> AlbumsGrid(albums = albums)
            1 -> SongsTab(songs = songs, state = state, favIds = favIds, vm = vm, onSongClick = onSongClick)
            2 -> PlaylistsPlaceholder()
        }
    }
}

@Composable
private fun AlbumsGrid(albums: List<Album>) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        contentPadding = PaddingValues(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(14.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        items(albums) { album ->
            Column(Modifier.clickable { }) {
                ArtworkImage(uri = album.artworkUri, modifier = Modifier.fillMaxWidth().aspectRatio(1f), cornerRadius = 16.dp)
                Spacer(Modifier.height(8.dp))
                Text(album.name, style = MaterialTheme.typography.labelLarge, color = LucidColors.Text100,
                    fontWeight = FontWeight.SemiBold, maxLines = 1, overflow = TextOverflow.Ellipsis)
                Text("${album.artist} · ${album.songCount}",
                    style = MaterialTheme.typography.labelSmall, color = LucidColors.Text50,
                    maxLines = 1, overflow = TextOverflow.Ellipsis)
            }
        }
    }
}

@Composable
private fun SongsTab(
    songs: List<Song>,
    state: com.lucid.player.data.models.PlayerState,
    favIds: Set<Long>,
    vm: PlayerViewModel,
    onSongClick: (Song) -> Unit
) {
    LazyColumn {
        items(songs) { song ->
            SongRow(
                song = song,
                isPlaying = state.currentSong?.id == song.id && state.isPlaying,
                isFav = song.id in favIds,
                onPlay = { onSongClick(song) },
                onFav = { vm.toggleFav(song.id) }
            )
            GradientDivider(Modifier.padding(start = 84.dp))
        }
    }
}

@Composable
private fun PlaylistsPlaceholder() {
    Box(Modifier.fillMaxSize(), Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(Icons.Rounded.PlaylistPlay, null, tint = LucidColors.Text30, modifier = Modifier.size(72.dp))
            Spacer(Modifier.height(16.dp))
            Text("Playlists coming soon", style = MaterialTheme.typography.titleMedium, color = LucidColors.Text50)
            Text("Create and manage your playlists", style = MaterialTheme.typography.bodySmall, color = LucidColors.Text30)
        }
    }
}
