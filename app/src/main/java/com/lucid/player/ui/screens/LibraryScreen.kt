package com.lucid.player.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.lucid.player.data.models.Album
import com.lucid.player.data.models.Song
import com.lucid.player.ui.theme.*
import com.lucid.player.viewmodel.PlayerViewModel

@Composable
fun LibraryScreen(
    viewModel: PlayerViewModel,
    onSongClick: (Song) -> Unit
) {
    val albums by viewModel.albums.collectAsState()
    var selectedTab by remember { mutableIntStateOf(0) }
    val tabs = listOf("Albums", "Songs")

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Void)
            .statusBarsPadding()
    ) {
        // Header
        Text(
            "Library",
            style = MaterialTheme.typography.headlineLarge,
            color = TextPrimary,
            fontWeight = FontWeight.ExtraBold,
            modifier = Modifier.padding(horizontal = 20.dp, vertical = 20.dp)
        )

        // Tab row
        Row(
            modifier = Modifier
                .padding(horizontal = 20.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(Surface1)
                .padding(4.dp)
        ) {
            tabs.forEachIndexed { index, tab ->
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(10.dp))
                        .background(if (selectedTab == index) NeonPurple else Color.Transparent)
                        .clickable { selectedTab = index }
                        .padding(vertical = 10.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        tab,
                        style = MaterialTheme.typography.labelLarge,
                        color = if (selectedTab == index) Color.White else TextSecondary,
                        fontWeight = if (selectedTab == index) FontWeight.SemiBold else FontWeight.Normal
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (selectedTab == 0) {
            // Albums grid
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(albums) { album ->
                    AlbumGridItem(album = album, onClick = { /* navigate to album */ })
                }
            }
        } else {
            // Songs list
            val songs by viewModel.songs.collectAsState()
            val favorites by viewModel.favorites.collectAsState()
            val playerState by viewModel.playerState.collectAsState()

            androidx.compose.foundation.lazy.LazyColumn {
                items(songs.size) { index ->
                    val song = songs[index]
                    SongListItem(
                        song = song,
                        isPlaying = playerState.currentSong?.id == song.id && playerState.isPlaying,
                        isFavorite = song.id in favorites,
                        onSongClick = { onSongClick(song) },
                        onFavoriteClick = { viewModel.toggleFavorite(song.id) }
                    )
                }
            }
        }
    }
}

@Composable
fun AlbumGridItem(album: Album, onClick: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(1f)
                .clip(RoundedCornerShape(14.dp))
                .background(Surface2)
        ) {
            if (album.artworkUri != null) {
                AsyncImage(
                    model = album.artworkUri,
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            } else {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.radialGradient(
                                colors = listOf(NeonPurple.copy(0.4f), CelestialBlue.copy(0.2f))
                            )
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Rounded.Album, null, tint = NeonPurple, modifier = Modifier.size(48.dp))
                }
            }
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(album.name, style = MaterialTheme.typography.labelLarge, color = TextPrimary, maxLines = 1, overflow = TextOverflow.Ellipsis, fontWeight = FontWeight.SemiBold)
        Text("${album.artist} • ${album.songCount} songs", style = MaterialTheme.typography.labelSmall, color = TextSecondary, maxLines = 1, overflow = TextOverflow.Ellipsis)
    }
}
