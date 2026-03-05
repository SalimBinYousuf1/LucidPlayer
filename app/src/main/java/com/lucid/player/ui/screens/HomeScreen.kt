package com.lucid.player.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.lucid.player.data.models.Song
import com.lucid.player.ui.theme.*
import com.lucid.player.viewmodel.PlayerViewModel
import java.util.Calendar

@Composable
fun HomeScreen(
    viewModel: PlayerViewModel,
    onSongClick: (Song) -> Unit,
    onNavigateToNowPlaying: () -> Unit
) {
    val songs by viewModel.songs.collectAsState()
    val albums by viewModel.albums.collectAsState()
    val playerState by viewModel.playerState.collectAsState()
    val favorites by viewModel.favorites.collectAsState()

    val greeting = remember {
        val hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
        when {
            hour < 12 -> "Good Morning"
            hour < 17 -> "Good Afternoon"
            else -> "Good Evening"
        }
    }

    val recentSongs = songs.sortedByDescending { it.dateAdded }.take(20)
    val favoriteSongs = songs.filter { it.id in favorites }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(Void),
        contentPadding = PaddingValues(bottom = 16.dp)
    ) {
        // Header
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                NeonPurple.copy(alpha = 0.2f),
                                Void
                            )
                        )
                    )
                    .statusBarsPadding()
                    .padding(horizontal = 20.dp, vertical = 24.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            greeting,
                            style = MaterialTheme.typography.bodyMedium,
                            color = TextSecondary
                        )
                        Text(
                            "Lucid Player",
                            style = MaterialTheme.typography.headlineMedium,
                            color = TextPrimary,
                            fontWeight = FontWeight.ExtraBold
                        )
                    }
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .clip(CircleShape)
                            .background(
                                Brush.radialGradient(
                                    colors = listOf(NeonPurple, CelestialBlue)
                                )
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            Icons.Rounded.Person,
                            contentDescription = "Profile",
                            tint = Color.White,
                            modifier = Modifier.size(22.dp)
                        )
                    }
                }
            }
        }

        // Quick Play Banner (if something was playing)
        if (playerState.currentSong != null) {
            item {
                NowPlayingBanner(
                    playerState = playerState,
                    onTogglePlay = viewModel::togglePlayPause,
                    onClick = onNavigateToNowPlaying
                )
                Spacer(modifier = Modifier.height(8.dp))
            }
        }

        // Recently Added
        if (recentSongs.isNotEmpty()) {
            item {
                SectionHeader(title = "Recently Added", subtitle = "${recentSongs.size} songs")
                LazyRow(
                    contentPadding = PaddingValues(horizontal = 20.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(recentSongs.take(10)) { song ->
                        SongCard(song = song, onClick = { onSongClick(song) })
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
            }
        }

        // Favorites
        if (favoriteSongs.isNotEmpty()) {
            item {
                SectionHeader(title = "❤️ Favorites", subtitle = "${favoriteSongs.size} songs")
                LazyRow(
                    contentPadding = PaddingValues(horizontal = 20.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(favoriteSongs.take(10)) { song ->
                        SongCard(song = song, onClick = { onSongClick(song) })
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
            }
        }

        // All Songs header
        item {
            SectionHeader(title = "All Songs", subtitle = "${songs.size} tracks")
        }

        // Songs list
        items(songs) { song ->
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

@Composable
fun NowPlayingBanner(
    playerState: com.lucid.player.data.models.PlayerState,
    onTogglePlay: () -> Unit,
    onClick: () -> Unit
) {
    val song = playerState.currentSong ?: return
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(
                Brush.horizontalGradient(
                    colors = listOf(NeonPurple.copy(alpha = 0.3f), CelestialBlue.copy(alpha = 0.2f))
                )
            )
            .background(GlassWhite)
            .clickable(onClick = onClick)
            .padding(16.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            // Artwork
            Box(
                modifier = Modifier
                    .size(52.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(Surface2)
            ) {
                if (song.artworkUri != null) {
                    AsyncImage(
                        model = song.artworkUri,
                        contentDescription = null,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Box(
                        modifier = Modifier.fillMaxSize().background(
                            Brush.radialGradient(colors = listOf(NeonPurple, CelestialBlue))
                        ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Rounded.MusicNote, null, tint = Color.White, modifier = Modifier.size(24.dp))
                    }
                }
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(song.title, style = MaterialTheme.typography.titleSmall, color = TextPrimary, maxLines = 1, overflow = TextOverflow.Ellipsis)
                Text(song.artist, style = MaterialTheme.typography.bodySmall, color = TextSecondary, maxLines = 1, overflow = TextOverflow.Ellipsis)
                Spacer(modifier = Modifier.height(6.dp))
                LinearProgressIndicator(
                    progress = { playerState.progress },
                    modifier = Modifier.fillMaxWidth().height(2.dp).clip(RoundedCornerShape(1.dp)),
                    color = NeonPurple,
                    trackColor = Surface3
                )
            }
            Spacer(modifier = Modifier.width(12.dp))
            IconButton(
                onClick = onTogglePlay,
                modifier = Modifier.size(40.dp).clip(CircleShape).background(NeonPurple.copy(alpha = 0.3f))
            ) {
                Icon(
                    if (playerState.isPlaying) Icons.Rounded.Pause else Icons.Rounded.PlayArrow,
                    contentDescription = null, tint = NeonPurple
                )
            }
        }
    }
}

@Composable
fun SectionHeader(title: String, subtitle: String? = null) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text(title, style = MaterialTheme.typography.titleLarge, color = TextPrimary, fontWeight = FontWeight.Bold)
            if (subtitle != null) {
                Text(subtitle, style = MaterialTheme.typography.bodySmall, color = TextSecondary)
            }
        }
        Text("See all", style = MaterialTheme.typography.labelMedium, color = NeonPurple)
    }
}

@Composable
fun SongCard(song: Song, onClick: () -> Unit) {
    Column(
        modifier = Modifier
            .width(130.dp)
            .clickable(onClick = onClick),
        horizontalAlignment = Alignment.Start
    ) {
        Box(
            modifier = Modifier
                .size(130.dp)
                .clip(RoundedCornerShape(14.dp))
                .background(Surface2)
        ) {
            if (song.artworkUri != null) {
                AsyncImage(
                    model = song.artworkUri,
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            } else {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Brush.radialGradient(colors = listOf(NeonPurple.copy(0.5f), CelestialBlue.copy(0.3f)))),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Rounded.MusicNote, null, tint = NeonPurple, modifier = Modifier.size(40.dp))
                }
            }
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(song.title, style = MaterialTheme.typography.labelLarge, color = TextPrimary, maxLines = 1, overflow = TextOverflow.Ellipsis)
        Text(song.artist, style = MaterialTheme.typography.labelSmall, color = TextSecondary, maxLines = 1, overflow = TextOverflow.Ellipsis)
    }
}

@Composable
fun SongListItem(
    song: Song,
    isPlaying: Boolean,
    isFavorite: Boolean,
    onSongClick: () -> Unit,
    onFavoriteClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onSongClick)
            .background(if (isPlaying) NeonPurple.copy(alpha = 0.08f) else Color.Transparent)
            .padding(horizontal = 20.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Artwork
        Box(
            modifier = Modifier
                .size(50.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(Surface2)
        ) {
            if (song.artworkUri != null) {
                AsyncImage(
                    model = song.artworkUri,
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            } else {
                Box(
                    modifier = Modifier.fillMaxSize().background(
                        Brush.radialGradient(colors = listOf(NeonPurple.copy(0.4f), CelestialBlue.copy(0.2f)))
                    ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Rounded.MusicNote, null, tint = NeonPurple, modifier = Modifier.size(22.dp))
                }
            }
            if (isPlaying) {
                Box(
                    modifier = Modifier.fillMaxSize().background(NeonPurple.copy(alpha = 0.5f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Rounded.VolumeUp, null, tint = Color.White, modifier = Modifier.size(20.dp))
                }
            }
        }

        Spacer(modifier = Modifier.width(14.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                song.title,
                style = MaterialTheme.typography.bodyMedium,
                color = if (isPlaying) NeonPurple else TextPrimary,
                fontWeight = if (isPlaying) FontWeight.SemiBold else FontWeight.Normal,
                maxLines = 1, overflow = TextOverflow.Ellipsis
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                "${song.artist} • ${song.durationFormatted}",
                style = MaterialTheme.typography.bodySmall,
                color = TextSecondary,
                maxLines = 1, overflow = TextOverflow.Ellipsis
            )
        }

        IconButton(onClick = onFavoriteClick, modifier = Modifier.size(36.dp)) {
            Icon(
                if (isFavorite) Icons.Rounded.Favorite else Icons.Rounded.FavoriteBorder,
                contentDescription = "Favorite",
                tint = if (isFavorite) NeonPink else TextTertiary,
                modifier = Modifier.size(18.dp)
            )
        }
    }
}
