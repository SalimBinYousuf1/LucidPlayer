package com.lucid.player.ui.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.*
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.*
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.lucid.player.data.models.PlayerState
import com.lucid.player.data.models.RepeatMode
import com.lucid.player.ui.components.*
import com.lucid.player.ui.theme.LucidColors
import com.lucid.player.viewmodel.PlayerViewModel

@Composable
fun NowPlayingScreen(
    vm: PlayerViewModel,
    onBack: () -> Unit
) {
    val state by vm.state.collectAsState()
    val favIds by vm.favIds.collectAsState()
    val song = state.currentSong
    var showQueue by remember { mutableStateOf(false) }
    var showSleepTimer by remember { mutableStateOf(false) }
    var showSpeedSheet by remember { mutableStateOf(false) }

    // Vinyl spin
    val infiniteRotation = rememberInfiniteTransition(label = "vinyl")
    val rotation by infiniteRotation.animateFloat(
        0f, 360f,
        infiniteRepeatable(tween(10000, easing = LinearEasing)),
        label = "rot"
    )

    Box(Modifier.fillMaxSize().background(LucidColors.Void)) {
        // Ambient glow blobs
        AmbienceBackground(isPlaying = state.isPlaying)

        if (showQueue) {
            QueueSheet(state = state, vm = vm, onDismiss = { showQueue = false })
            return@Box
        }

        Column(
            modifier = Modifier.fillMaxSize().statusBarsPadding().navigationBarsPadding()
        ) {
            // ── Top bar ───────────────────────────────────────────────────────
            Row(
                Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 14.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                GlowIconButton(Icons.Rounded.KeyboardArrowDown, "Back", onBack, size = 44.dp, iconSize = 26.dp)
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("NOW PLAYING", style = MaterialTheme.typography.labelSmall,
                        color = LucidColors.Text50, letterSpacing = 2.5.sp)
                    if (song != null)
                        Text(song.album, style = MaterialTheme.typography.labelMedium,
                            color = LucidColors.Text80, maxLines = 1, overflow = TextOverflow.Ellipsis)
                }
                GlowIconButton(Icons.Rounded.QueueMusic, "Queue", { showQueue = true }, size = 44.dp, iconSize = 22.dp)
            }

            Spacer(Modifier.height(12.dp))

            // ── Vinyl record ─────────────────────────────────────────────────
            Box(
                Modifier.fillMaxWidth().padding(horizontal = 44.dp),
                contentAlignment = Alignment.Center
            ) {
                VinylRecord(
                    artworkUri = song?.artworkUri,
                    isPlaying = state.isPlaying,
                    rotation = rotation
                )
            }

            Spacer(Modifier.height(32.dp))

            // ── Song info + fav ───────────────────────────────────────────────
            Row(
                Modifier.fillMaxWidth().padding(horizontal = 28.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(Modifier.weight(1f).padding(end = 12.dp)) {
                    Text(
                        song?.title ?: "Nothing Playing",
                        style = MaterialTheme.typography.headlineSmall,
                        color = LucidColors.Text100,
                        fontWeight = FontWeight.ExtraBold,
                        maxLines = 1, overflow = TextOverflow.Ellipsis
                    )
                    Spacer(Modifier.height(4.dp))
                    Text(
                        song?.artist ?: "—",
                        style = MaterialTheme.typography.bodyLarge,
                        color = LucidColors.Text50,
                        maxLines = 1, overflow = TextOverflow.Ellipsis
                    )
                }
                val isFav = song?.id?.let { it in favIds } == true
                GlowIconButton(
                    icon = if (isFav) Icons.Rounded.Favorite else Icons.Rounded.FavoriteBorder,
                    contentDescription = "Favorite",
                    onClick = { song?.id?.let { vm.toggleFav(it) } },
                    size = 46.dp, iconSize = 22.dp,
                    active = isFav, activeColor = LucidColors.Ember
                )
            }

            Spacer(Modifier.height(28.dp))

            // ── Seek bar ─────────────────────────────────────────────────────
            SeekBar(state = state, onSeek = vm::seekTo)

            Spacer(Modifier.height(20.dp))

            // ── Main controls ─────────────────────────────────────────────────
            Row(
                Modifier.fillMaxWidth().padding(horizontal = 24.dp),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                GlowIconButton(
                    icon = Icons.Rounded.Shuffle, "Shuffle", vm::toggleShuffle,
                    size = 48.dp, iconSize = 22.dp,
                    active = state.isShuffled, activeColor = LucidColors.Indigo
                )
                GlowIconButton(Icons.Rounded.SkipPrevious, "Previous", vm::skipPrev, size = 58.dp, iconSize = 30.dp)
                PrimaryPlayButton(isPlaying = state.isPlaying, onClick = vm::togglePlayPause, size = 76.dp)
                GlowIconButton(Icons.Rounded.SkipNext, "Next", vm::skipNext, size = 58.dp, iconSize = 30.dp)
                val (repIcon, repActive) = when (state.repeatMode) {
                    RepeatMode.OFF -> Icons.Rounded.Repeat    to false
                    RepeatMode.ALL -> Icons.Rounded.Repeat    to true
                    RepeatMode.ONE -> Icons.Rounded.RepeatOne to true
                }
                GlowIconButton(repIcon, "Repeat", vm::toggleRepeat, size = 48.dp, iconSize = 22.dp,
                    active = repActive, activeColor = LucidColors.Aurora)
            }

            Spacer(Modifier.height(28.dp))

            // ── Extra action chips ────────────────────────────────────────────
            Row(
                Modifier.fillMaxWidth().padding(horizontal = 20.dp),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                ActionChip(icon = Icons.Rounded.Timer, label = if (state.sleepTimerMinutes > 0) "${state.sleepTimerMinutes}m" else "Sleep",
                    active = state.sleepTimerMinutes > 0) { showSleepTimer = true }
                ActionChip(icon = Icons.Rounded.Speed, label = "${state.playbackSpeed}x",
                    active = state.playbackSpeed != 1f) { showSpeedSheet = true }
                ActionChip(icon = Icons.Rounded.PlaylistAdd, label = "Queue") { showQueue = true }
                ActionChip(icon = Icons.Rounded.Share, label = "Share") { }
            }
        }

        // ── Sheets ────────────────────────────────────────────────────────────
        if (showSleepTimer) SleepTimerSheet(currentMinutes = state.sleepTimerMinutes,
            onSet = { vm.setSleepTimer(it); showSleepTimer = false },
            onCancel = { vm.cancelSleepTimer(); showSleepTimer = false },
            onDismiss = { showSleepTimer = false })

        if (showSpeedSheet) SpeedSheet(current = state.playbackSpeed,
            onSelect = { vm.setPlaybackSpeed(it); showSpeedSheet = false },
            onDismiss = { showSpeedSheet = false })
    }
}

/* ─── Vinyl record component ─────────────────────────────────────────────────── */
@Composable
private fun VinylRecord(artworkUri: Any?, isPlaying: Boolean, rotation: Float) {
    val actualRotation = if (isPlaying) rotation else rotation // freeze if not playing by not using animated value when stopped
    Box(contentAlignment = Alignment.Center) {
        // Outer platter shadow/glow
        if (isPlaying) {
            Box(
                modifier = Modifier.size(300.dp).clip(CircleShape)
                    .background(Brush.radialGradient(colors = listOf(LucidColors.Indigo.copy(0.2f), Color.Transparent)))
                    .blur(24.dp)
            )
        }
        // Vinyl body
        Box(
            modifier = Modifier.size(270.dp).rotate(if (isPlaying) rotation else 0f).clip(CircleShape)
                .background(Brush.radialGradient(
                    colors = listOf(Color(0xFF2A2A30), Color(0xFF111115), Color(0xFF1C1C22), Color(0xFF080808))
                )).border(1.dp, LucidColors.GlassBorder, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            // Groove rings
            listOf(240, 200, 165, 130).forEach { size ->
                Box(Modifier.size(size.dp).clip(CircleShape).border(0.5.dp, Color.White.copy(0.04f), CircleShape))
            }
            // Center label artwork
            Box(
                modifier = Modifier.size(110.dp).clip(CircleShape)
                    .background(LucidColors.Surface).border(1.5.dp, LucidColors.GlassBorder, CircleShape)
            ) {
                if (artworkUri != null) {
                    AsyncImage(model = artworkUri, contentDescription = null,
                        modifier = Modifier.fillMaxSize(), contentScale = ContentScale.Crop)
                } else {
                    Box(Modifier.fillMaxSize()
                        .background(Brush.radialGradient(colors = listOf(LucidColors.Indigo, LucidColors.Depth))),
                        contentAlignment = Alignment.Center) {
                        Icon(Icons.Rounded.MusicNote, null, tint = Color.White.copy(0.6f), modifier = Modifier.size(40.dp))
                    }
                }
                // Spindle hole
                Box(Modifier.size(10.dp).clip(CircleShape).background(LucidColors.Void)
                    .border(1.dp, LucidColors.GlassBorder, CircleShape).align(Alignment.Center))
            }
        }
        // Animated rainbow ring when playing
        if (isPlaying) {
            Box(
                modifier = Modifier.size(278.dp).clip(CircleShape).border(
                    width = 1.5.dp,
                    brush = Brush.sweepGradient(listOf(LucidColors.Indigo, LucidColors.Aurora, LucidColors.CosmicLight, LucidColors.Indigo)),
                    shape = CircleShape
                )
            )
        }
    }
}

/* ─── Ambience background glows ──────────────────────────────────────────────── */
@Composable
private fun AmbienceBackground(isPlaying: Boolean) {
    val alpha by animateFloatAsState(if (isPlaying) 1f else 0.4f, tween(1500), label = "amb")
    Box(Modifier.fillMaxSize()) {
        Box(Modifier.size(350.dp).offset((-80).dp, (-60).dp).clip(CircleShape)
            .alpha(alpha * 0.28f)
            .background(Brush.radialGradient(colors = listOf(LucidColors.Indigo, Color.Transparent)))
            .blur(80.dp))
        Box(Modifier.size(300.dp).align(Alignment.BottomEnd).offset(80.dp, 80.dp).clip(CircleShape)
            .alpha(alpha * 0.20f)
            .background(Brush.radialGradient(colors = listOf(LucidColors.Cosmic, Color.Transparent)))
            .blur(80.dp))
    }
}

/* ─── Seek bar ────────────────────────────────────────────────────────────────── */
@Composable
private fun SeekBar(state: PlayerState, onSeek: (Float) -> Unit) {
    Column(Modifier.padding(horizontal = 26.dp)) {
        Box(Modifier.fillMaxWidth().height(3.dp).clip(RoundedCornerShape(2.dp)).background(LucidColors.Card)) {
            Box(Modifier.fillMaxWidth(state.progress).fillMaxHeight().clip(RoundedCornerShape(2.dp))
                .background(Brush.horizontalGradient(colors = listOf(LucidColors.Indigo, LucidColors.Aurora))))
        }
        Slider(
            value = state.progress, onValueChange = onSeek,
            modifier = Modifier.fillMaxWidth().height(28.dp).offset(y = (-18).dp),
            colors = SliderDefaults.colors(
                thumbColor = Color.White, activeTrackColor = Color.Transparent, inactiveTrackColor = Color.Transparent
            )
        )
        Row(Modifier.fillMaxWidth().offset(y = (-14).dp), Arrangement.SpaceBetween) {
            Text(formatMs(state.currentPosition), style = MaterialTheme.typography.labelSmall, color = LucidColors.Text50)
            Text(state.currentSong?.durationFormatted ?: "0:00", style = MaterialTheme.typography.labelSmall, color = LucidColors.Text50)
        }
    }
}

/* ─── Action chip ─────────────────────────────────────────────────────────────── */
@Composable
private fun ActionChip(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    active: Boolean = false,
    onClick: () -> Unit
) {
    Column(Modifier.clickable(onClick = onClick), horizontalAlignment = Alignment.CenterHorizontally) {
        Box(
            Modifier.size(50.dp).clip(RoundedCornerShape(14.dp))
                .background(if (active) LucidColors.Indigo.copy(0.2f) else LucidColors.Glass10)
                .border(0.5.dp, if (active) LucidColors.Indigo.copy(0.5f) else LucidColors.GlassBorder, RoundedCornerShape(14.dp)),
            contentAlignment = Alignment.Center
        ) {
            Icon(icon, label, tint = if (active) LucidColors.IndigoLight else LucidColors.Text50, modifier = Modifier.size(22.dp))
        }
        Spacer(Modifier.height(4.dp))
        Text(label, style = MaterialTheme.typography.labelSmall, color = LucidColors.Text30, maxLines = 1)
    }
}

/* ─── Queue sheet ─────────────────────────────────────────────────────────────── */
@Composable
private fun QueueSheet(state: PlayerState, vm: PlayerViewModel, onDismiss: () -> Unit) {
    Box(Modifier.fillMaxSize().background(LucidColors.Void)) {
        Column(Modifier.fillMaxSize().statusBarsPadding()) {
            Row(Modifier.fillMaxWidth().padding(16.dp), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                GlowIconButton(Icons.Rounded.ArrowBack, "Back", onDismiss)
                Text("Up Next", style = MaterialTheme.typography.titleLarge, color = LucidColors.Text100, fontWeight = FontWeight.Bold)
                Text("${state.queue.size} songs", style = MaterialTheme.typography.labelMedium, color = LucidColors.Text50)
            }
            GradientDivider()
            LazyColumn {
                itemsIndexed(state.queue) { index, song ->
                    SongRow(
                        song = song,
                        isPlaying = index == state.currentIndex,
                        isFav = false,
                        onPlay = { vm.playSong(song, state.queue) },
                        onFav = {}
                    )
                }
            }
        }
    }
}

/* ─── Sleep timer sheet ─────────────────────────────────────────────────────────── */
@Composable
private fun SleepTimerSheet(currentMinutes: Int, onSet: (Int) -> Unit, onCancel: () -> Unit, onDismiss: () -> Unit) {
    val options = listOf(5, 10, 15, 30, 45, 60)
    Box(
        modifier = Modifier.fillMaxSize().background(Color.Black.copy(0.7f)).clickable(onClick = onDismiss),
        contentAlignment = Alignment.BottomCenter
    ) {
        Column(
            modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp))
                .background(LucidColors.SurfaceHigh).padding(24.dp).clickable {}
        ) {
            Text("Sleep Timer", style = MaterialTheme.typography.headlineSmall, color = LucidColors.Text100, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(20.dp))
            options.chunked(3).forEach { row ->
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    row.forEach { mins ->
                        val active = currentMinutes == mins
                        Box(
                            modifier = Modifier.weight(1f).height(52.dp).clip(RoundedCornerShape(14.dp))
                                .background(if (active) LucidColors.Indigo else LucidColors.Card)
                                .border(1.dp, if (active) LucidColors.IndigoLight.copy(0.5f) else LucidColors.GlassBorder, RoundedCornerShape(14.dp))
                                .clickable { onSet(mins) },
                            contentAlignment = Alignment.Center
                        ) { Text("${mins}m", color = if (active) Color.White else LucidColors.Text80, fontWeight = FontWeight.SemiBold) }
                    }
                    if (row.size < 3) repeat(3 - row.size) { Spacer(Modifier.weight(1f)) }
                }
                Spacer(Modifier.height(10.dp))
            }
            if (currentMinutes > 0) {
                Spacer(Modifier.height(8.dp))
                Button(
                    onClick = onCancel,
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = LucidColors.Ember.copy(0.2f))
                ) { Text("Cancel Timer (${currentMinutes}m left)", color = LucidColors.Ember) }
            }
            Spacer(Modifier.height(16.dp))
        }
    }
}

/* ─── Speed sheet ─────────────────────────────────────────────────────────────── */
@Composable
private fun SpeedSheet(current: Float, onSelect: (Float) -> Unit, onDismiss: () -> Unit) {
    val speeds = listOf(0.5f, 0.75f, 1.0f, 1.25f, 1.5f, 2.0f)
    Box(
        modifier = Modifier.fillMaxSize().background(Color.Black.copy(0.7f)).clickable(onClick = onDismiss),
        contentAlignment = Alignment.BottomCenter
    ) {
        Column(
            modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp))
                .background(LucidColors.SurfaceHigh).padding(24.dp).clickable {}
        ) {
            Text("Playback Speed", style = MaterialTheme.typography.headlineSmall, color = LucidColors.Text100, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(20.dp))
            speeds.chunked(3).forEach { row ->
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    row.forEach { speed ->
                        val active = current == speed
                        Box(
                            modifier = Modifier.weight(1f).height(52.dp).clip(RoundedCornerShape(14.dp))
                                .background(if (active) LucidColors.Indigo else LucidColors.Card)
                                .border(1.dp, if (active) LucidColors.IndigoLight.copy(0.5f) else LucidColors.GlassBorder, RoundedCornerShape(14.dp))
                                .clickable { onSelect(speed) },
                            contentAlignment = Alignment.Center
                        ) { Text("${speed}x", color = if (active) Color.White else LucidColors.Text80, fontWeight = FontWeight.SemiBold) }
                    }
                    if (row.size < 3) repeat(3 - row.size) { Spacer(Modifier.weight(1f)) }
                }
                Spacer(Modifier.height(10.dp))
            }
            Spacer(Modifier.height(16.dp))
        }
    }
}

private fun formatMs(ms: Long): String {
    val s = ms / 1000; return "%d:%02d".format(s / 60, s % 60)
}
