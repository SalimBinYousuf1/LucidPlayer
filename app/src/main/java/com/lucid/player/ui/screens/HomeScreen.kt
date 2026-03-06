package com.lucid.player.ui.screens

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
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
import com.lucid.player.ui.components.*
import com.lucid.player.ui.theme.LucidColors
import com.lucid.player.viewmodel.PlayerViewModel
import java.util.Calendar

@Composable
fun HomeScreen(
    vm: PlayerViewModel,
    onSongClick: (Song) -> Unit,
    onNowPlaying: () -> Unit
) {
    val state      by vm.state.collectAsState()
    val songs      by vm.songs.collectAsState()
    val favSongs   by vm.favSongs.collectAsState()
    val recentSongs by vm.recentSongs.collectAsState()
    val favIds     by vm.favIds.collectAsState()

    val hour = remember { Calendar.getInstance().get(Calendar.HOUR_OF_DAY) }
    val greeting = when { hour < 12 -> "Good Morning" ; hour < 17 -> "Good Afternoon" ; else -> "Good Evening" }

    LazyColumn(
        modifier = Modifier.fillMaxSize().background(LucidColors.Void),
        contentPadding = PaddingValues(bottom = 20.dp)
    ) {
        // ── Hero header ─────────────────────────────────────────────────────
        item {
            Box(
                modifier = Modifier.fillMaxWidth()
                    .background(Brush.verticalGradient(
                        colors = listOf(LucidColors.Indigo.copy(0.22f), LucidColors.Void)
                    ))
                    .statusBarsPadding()
                    .padding(horizontal = 22.dp, vertical = 22.dp)
            ) {
                Row(Modifier.fillMaxWidth(), Arrangement.SpaceBetween, Alignment.CenterVertically) {
                    Column {
                        Text(greeting, style = MaterialTheme.typography.labelLarge, color = LucidColors.Text50)
                        Spacer(Modifier.height(2.dp))
                        Text("Lucid", style = MaterialTheme.typography.displayMedium,
                            color = LucidColors.Text100, fontWeight = FontWeight.Black, letterSpacing = (-1).sp)
                    }
                    // Stats badge
                    GlassCard(modifier = Modifier.padding(4.dp)) {
                        Column(
                            Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text("${songs.size}", style = MaterialTheme.typography.titleLarge,
                                color = LucidColors.IndigoLight, fontWeight = FontWeight.Black)
                            Text("Tracks", style = MaterialTheme.typography.labelSmall, color = LucidColors.Text50)
                        }
                    }
                }
                Spacer(Modifier.height(16.dp))
                // Quick actions row
                Row(Modifier.padding(top = 70.dp), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    QuickActionButton(Icons.Rounded.PlayArrow, "Play All",
                        Modifier.weight(1f)) { vm.playAll(); onNowPlaying() }
                    QuickActionButton(Icons.Rounded.Shuffle, "Shuffle",
                        Modifier.weight(1f), filled = false) { vm.shuffleAll(); onNowPlaying() }
                }
            }
        }

        // ── Currently playing banner ─────────────────────────────────────────
        if (state.currentSong != null) {
            item {
                NowPlayingBanner(state = state, onTogglePlay = vm::togglePlayPause, onClick = onNowPlaying)
                Spacer(Modifier.height(4.dp))
            }
        }

        // ── Recently added ────────────────────────────────────────────────────
        if (recentSongs.isNotEmpty()) {
            item {
                SectionHeader("Recently Added", "${recentSongs.size} tracks", "See All")
                LazyRow(
                    contentPadding = PaddingValues(horizontal = 20.dp),
                    horizontalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    items(recentSongs.take(12)) { song ->
                        SongCard(song = song, isPlaying = state.currentSong?.id == song.id && state.isPlaying,
                            onClick = { onSongClick(song) })
                    }
                }
                Spacer(Modifier.height(8.dp))
            }
        }

        // ── Favorites ─────────────────────────────────────────────────────────
        if (favSongs.isNotEmpty()) {
            item {
                SectionHeader("Favourites", "${favSongs.size} saved")
                LazyRow(
                    contentPadding = PaddingValues(horizontal = 20.dp),
                    horizontalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    items(favSongs.take(10)) { song ->
                        SongCard(song = song, isPlaying = state.currentSong?.id == song.id && state.isPlaying,
                            onClick = { onSongClick(song) })
                    }
                }
                Spacer(Modifier.height(8.dp))
            }
        }

        // ── All songs ─────────────────────────────────────────────────────────
        item { SectionHeader("All Songs", "${songs.size} tracks") }

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
fun QuickActionButton(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    modifier: Modifier = Modifier,
    filled: Boolean = true,
    onClick: () -> Unit
) {
    Box(
        modifier = modifier.height(46.dp)
            .clip(RoundedCornerShape(14.dp))
            .background(if (filled) LucidColors.Indigo else LucidColors.Glass10)
            .border(if (filled) 0.dp else 0.5.dp, LucidColors.GlassBorder, RoundedCornerShape(14.dp))
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Icon(icon, null, tint = if (filled) Color.White else LucidColors.Text80, modifier = Modifier.size(18.dp))
            Text(label, style = MaterialTheme.typography.labelLarge,
                color = if (filled) Color.White else LucidColors.Text80, fontWeight = FontWeight.SemiBold)
        }
    }
}

@Composable
fun NowPlayingBanner(
    state: com.lucid.player.data.models.PlayerState,
    onTogglePlay: () -> Unit,
    onClick: () -> Unit
) {
    val song = state.currentSong ?: return
    Box(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 18.dp, vertical = 4.dp)
            .clip(RoundedCornerShape(18.dp))
            .background(Brush.horizontalGradient(colors = listOf(
                LucidColors.Indigo.copy(0.25f), LucidColors.Cosmic.copy(0.15f)
            )))
            .border(0.5.dp, LucidColors.GlassBorder, RoundedCornerShape(18.dp))
            .clickable(onClick = onClick)
    ) {
        // Progress bar at top
        Box(Modifier.fillMaxWidth(state.progress).height(2.dp).align(Alignment.TopStart)
            .clip(RoundedCornerShape(topStart = 18.dp, topEnd = 18.dp))
            .background(Brush.horizontalGradient(colors = listOf(LucidColors.Indigo, LucidColors.Aurora))))
        Row(Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            ArtworkImage(uri = song.artworkUri, modifier = Modifier.size(52.dp), cornerRadius = 12.dp)
            Column(Modifier.weight(1f)) {
                Text(song.title, style = MaterialTheme.typography.labelLarge, color = LucidColors.Text100,
                    fontWeight = FontWeight.SemiBold, maxLines = 1, overflow = TextOverflow.Ellipsis)
                Text(song.artist, style = MaterialTheme.typography.labelSmall, color = LucidColors.Text50,
                    maxLines = 1, overflow = TextOverflow.Ellipsis)
            }
            GlowIconButton(
                icon = if (state.isPlaying) Icons.Rounded.Pause else Icons.Rounded.PlayArrow,
                contentDescription = "Play/Pause", onClick = onTogglePlay,
                size = 42.dp, iconSize = 22.dp, active = true, activeColor = LucidColors.Indigo
            )
        }
    }
}

@Composable
fun SongCard(song: Song, isPlaying: Boolean, onClick: () -> Unit) {
    Column(Modifier.width(140.dp).clickable(onClick = onClick)) {
        Box(Modifier.size(140.dp)) {
            ArtworkImage(uri = song.artworkUri, modifier = Modifier.fillMaxSize(), cornerRadius = 16.dp)
            if (isPlaying) {
                Box(Modifier.fillMaxSize().clip(RoundedCornerShape(16.dp)).background(LucidColors.Indigo.copy(0.5f)),
                    contentAlignment = Alignment.Center) {
                    PlayingBarsIndicator(Modifier.size(28.dp))
                }
            }
            // Playing badge
            if (isPlaying) {
                Box(
                    Modifier.align(Alignment.TopEnd).padding(8.dp)
                        .clip(CircleShape).background(LucidColors.Indigo).padding(5.dp)
                ) {
                    Icon(Icons.Rounded.VolumeUp, null, tint = Color.White, modifier = Modifier.size(12.dp))
                }
            }
        }
        Spacer(Modifier.height(8.dp))
        Text(song.title, style = MaterialTheme.typography.labelLarge,
            color = if (isPlaying) LucidColors.IndigoLight else LucidColors.Text100,
            fontWeight = FontWeight.SemiBold, maxLines = 1, overflow = TextOverflow.Ellipsis)
        Text(song.artist, style = MaterialTheme.typography.labelSmall, color = LucidColors.Text50,
            maxLines = 1, overflow = TextOverflow.Ellipsis)
    }
}
