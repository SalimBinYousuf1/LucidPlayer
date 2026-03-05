package com.lucid.player.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.*
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.*
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.lucid.player.data.models.PlayerState
import com.lucid.player.data.models.RepeatMode
import com.lucid.player.ui.theme.*
import com.lucid.player.viewmodel.PlayerViewModel
import kotlin.math.abs

@Composable
fun NowPlayingScreen(
    viewModel: PlayerViewModel,
    onNavigateBack: () -> Unit
) {
    val playerState by viewModel.playerState.collectAsState()
    val favorites by viewModel.favorites.collectAsState()
    val song = playerState.currentSong

    // Animated rotation for vinyl record
    val infiniteTransition = rememberInfiniteTransition(label = "vinyl")
    val rotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(8000, easing = LinearEasing),
            repeatMode = androidx.compose.animation.core.RepeatMode.Restart
        ),
        label = "rotation"
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Void)
    ) {
        // Animated gradient background
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.radialGradient(
                        colors = listOf(
                            NeonPurple.copy(alpha = 0.3f),
                            CelestialBlue.copy(alpha = 0.15f),
                            Void
                        ),
                        center = Offset(0.5f, 0.2f),
                        radius = 800f
                    )
                )
        )

        // Floating orbs for atmosphere
        Box(
            modifier = Modifier
                .size(300.dp)
                .offset((-80).dp, (-40).dp)
                .clip(CircleShape)
                .background(
                    Brush.radialGradient(
                        colors = listOf(
                            NeonPurple.copy(alpha = 0.25f),
                            Color.Transparent
                        )
                    )
                )
                .blur(60.dp)
        )
        Box(
            modifier = Modifier
                .size(250.dp)
                .align(Alignment.BottomEnd)
                .offset(60.dp, 60.dp)
                .clip(CircleShape)
                .background(
                    Brush.radialGradient(
                        colors = listOf(
                            CelestialBlue.copy(alpha = 0.2f),
                            Color.Transparent
                        )
                    )
                )
                .blur(60.dp)
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding()
        ) {
            // Top bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = onNavigateBack,
                    modifier = Modifier
                        .size(44.dp)
                        .clip(CircleShape)
                        .background(GlassWhite)
                ) {
                    Icon(
                        Icons.Rounded.KeyboardArrowDown,
                        contentDescription = "Minimize",
                        tint = TextPrimary,
                        modifier = Modifier.size(24.dp)
                    )
                }

                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        "NOW PLAYING",
                        style = MaterialTheme.typography.labelSmall,
                        color = TextSecondary,
                        letterSpacing = 3.sp
                    )
                }

                IconButton(
                    onClick = { /* queue */ },
                    modifier = Modifier
                        .size(44.dp)
                        .clip(CircleShape)
                        .background(GlassWhite)
                ) {
                    Icon(
                        Icons.Rounded.QueueMusic,
                        contentDescription = "Queue",
                        tint = TextPrimary,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Vinyl / Artwork
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 40.dp),
                contentAlignment = Alignment.Center
            ) {
                // Outer vinyl ring
                Box(
                    modifier = Modifier
                        .size(280.dp)
                        .rotate(if (playerState.isPlaying) rotation else rotation)
                        .clip(CircleShape)
                        .background(
                            Brush.radialGradient(
                                colors = listOf(
                                    Color(0xFF2A2A2A),
                                    Color(0xFF111111),
                                    Color(0xFF1E1E1E),
                                    Color(0xFF0A0A0A)
                                )
                            )
                        )
                        .border(1.dp, GlassBorder, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    // Vinyl grooves (decorative rings)
                    repeat(5) { i ->
                        Box(
                            modifier = Modifier
                                .size((220 - i * 28).dp)
                                .clip(CircleShape)
                                .border(0.5.dp, Color.White.copy(alpha = 0.05f + i * 0.01f), CircleShape)
                        )
                    }

                    // Center artwork
                    Box(
                        modifier = Modifier
                            .size(140.dp)
                            .clip(CircleShape)
                            .background(Surface2)
                            .border(2.dp, GlassBorder, CircleShape)
                    ) {
                        if (song?.artworkUri != null) {
                            AsyncImage(
                                model = song.artworkUri,
                                contentDescription = "Album art",
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop
                            )
                        } else {
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .background(
                                        Brush.radialGradient(
                                            colors = listOf(NeonPurple, CelestialBlue)
                                        )
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    Icons.Rounded.MusicNote,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(48.dp)
                                )
                            }
                        }

                        // Center pin hole
                        Box(
                            modifier = Modifier
                                .size(12.dp)
                                .clip(CircleShape)
                                .background(Void)
                                .border(1.dp, GlassBorder, CircleShape)
                                .align(Alignment.Center)
                        )
                    }
                }

                // Playing indicator glow
                if (playerState.isPlaying) {
                    Box(
                        modifier = Modifier
                            .size(290.dp)
                            .clip(CircleShape)
                            .border(
                                width = 1.5.dp,
                                brush = Brush.sweepGradient(
                                    colors = listOf(
                                        NeonPurple.copy(alpha = 0.8f),
                                        CelestialBlue.copy(alpha = 0.4f),
                                        Aurora.copy(alpha = 0.6f),
                                        NeonPurple.copy(alpha = 0.8f)
                                    )
                                ),
                                shape = CircleShape
                            )
                    )
                }
            }

            Spacer(modifier = Modifier.height(36.dp))

            // Song info
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 28.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = song?.title ?: "No Song Selected",
                        style = MaterialTheme.typography.headlineSmall,
                        color = TextPrimary,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = song?.artist ?: "Unknown Artist",
                        style = MaterialTheme.typography.bodyLarge,
                        color = TextSecondary,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                // Favorite button
                val isFav = song?.id?.let { it in favorites } == true
                IconButton(
                    onClick = { song?.id?.let { viewModel.toggleFavorite(it) } },
                    modifier = Modifier
                        .size(44.dp)
                        .clip(CircleShape)
                        .background(if (isFav) NeonPink.copy(alpha = 0.15f) else GlassWhite)
                ) {
                    Icon(
                        if (isFav) Icons.Rounded.Favorite else Icons.Rounded.FavoriteBorder,
                        contentDescription = "Favorite",
                        tint = if (isFav) NeonPink else TextSecondary,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Progress bar
            Column(
                modifier = Modifier.padding(horizontal = 28.dp)
            ) {
                // Glowing progress slider
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(4.dp)
                        .clip(RoundedCornerShape(2.dp))
                        .background(Surface3)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(playerState.progress)
                            .fillMaxHeight()
                            .clip(RoundedCornerShape(2.dp))
                            .background(
                                Brush.horizontalGradient(
                                    colors = listOf(CelestialBlue, NeonPurple, Aurora)
                                )
                            )
                    )

                    // Glow on thumb
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(playerState.progress)
                            .fillMaxHeight()
                            .clip(RoundedCornerShape(2.dp))
                            .background(
                                Brush.horizontalGradient(
                                    colors = listOf(
                                        Color.Transparent,
                                        NeonPurple.copy(alpha = 0.3f)
                                    )
                                )
                            )
                    )
                }

                Slider(
                    value = playerState.progress,
                    onValueChange = { viewModel.seekTo(it) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(24.dp)
                        .offset(y = (-14).dp),
                    colors = SliderDefaults.colors(
                        thumbColor = Color.White,
                        activeTrackColor = Color.Transparent,
                        inactiveTrackColor = Color.Transparent
                    )
                )

                Row(
                    modifier = Modifier.fillMaxWidth().offset(y = (-8).dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        formatDuration(playerState.currentPosition),
                        style = MaterialTheme.typography.labelMedium,
                        color = TextSecondary
                    )
                    Text(
                        song?.durationFormatted ?: "0:00",
                        style = MaterialTheme.typography.labelMedium,
                        color = TextSecondary
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Main Controls
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Shuffle
                IconButton(
                    onClick = viewModel::toggleShuffle,
                    modifier = Modifier.size(48.dp)
                ) {
                    Icon(
                        Icons.Rounded.Shuffle,
                        contentDescription = "Shuffle",
                        tint = if (playerState.isShuffled) NeonPurple else TextSecondary,
                        modifier = Modifier.size(22.dp)
                    )
                }

                // Skip Previous
                IconButton(
                    onClick = viewModel::skipPrevious,
                    modifier = Modifier
                        .size(56.dp)
                        .clip(CircleShape)
                        .background(GlassWhite)
                ) {
                    Icon(
                        Icons.Rounded.SkipPrevious,
                        contentDescription = "Previous",
                        tint = TextPrimary,
                        modifier = Modifier.size(28.dp)
                    )
                }

                // Play/Pause - Main button
                Box(
                    modifier = Modifier
                        .size(72.dp)
                        .clip(CircleShape)
                        .background(
                            Brush.radialGradient(
                                colors = listOf(NeonPurple, ElectricViolet)
                            )
                        )
                        .clickable { viewModel.togglePlayPause() }
                        .shadow(
                            elevation = 20.dp,
                            shape = CircleShape,
                            ambientColor = NeonPurple.copy(alpha = 0.5f),
                            spotColor = NeonPurple.copy(alpha = 0.8f)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        if (playerState.isPlaying) Icons.Rounded.Pause else Icons.Rounded.PlayArrow,
                        contentDescription = if (playerState.isPlaying) "Pause" else "Play",
                        tint = Color.White,
                        modifier = Modifier.size(36.dp)
                    )
                }

                // Skip Next
                IconButton(
                    onClick = viewModel::skipNext,
                    modifier = Modifier
                        .size(56.dp)
                        .clip(CircleShape)
                        .background(GlassWhite)
                ) {
                    Icon(
                        Icons.Rounded.SkipNext,
                        contentDescription = "Next",
                        tint = TextPrimary,
                        modifier = Modifier.size(28.dp)
                    )
                }

                // Repeat
                IconButton(
                    onClick = viewModel::toggleRepeat,
                    modifier = Modifier.size(48.dp)
                ) {
                    val (icon, tint) = when (playerState.repeatMode) {
                        RepeatMode.OFF -> Icons.Rounded.Repeat to TextSecondary
                        RepeatMode.ALL -> Icons.Rounded.Repeat to NeonPurple
                        RepeatMode.ONE -> Icons.Rounded.RepeatOne to Aurora
                    }
                    Icon(
                        icon,
                        contentDescription = "Repeat",
                        tint = tint,
                        modifier = Modifier.size(22.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Extra actions row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 28.dp),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                ActionChip(Icons.Rounded.PlaylistAdd, "Add to Playlist")
                ActionChip(Icons.Rounded.Share, "Share")
                ActionChip(Icons.Rounded.Equalizer, "Equalizer")
                ActionChip(Icons.Rounded.MoreHoriz, "More")
            }
        }
    }
}

@Composable
private fun ActionChip(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.clickable { }
    ) {
        Box(
            modifier = Modifier
                .size(44.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(GlassWhite)
                .border(1.dp, GlassBorder, RoundedCornerShape(12.dp)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                icon,
                contentDescription = label,
                tint = TextSecondary,
                modifier = Modifier.size(20.dp)
            )
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            label,
            style = MaterialTheme.typography.labelSmall,
            color = TextTertiary,
            maxLines = 1
        )
    }
}

private fun formatDuration(ms: Long): String {
    val totalSeconds = ms / 1000
    val minutes = totalSeconds / 60
    val seconds = totalSeconds % 60
    return "%d:%02d".format(minutes, seconds)
}
