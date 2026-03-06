package com.lucid.player.ui.components

import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.lucid.player.data.models.PlayerState
import com.lucid.player.ui.theme.LucidColors

@Composable
fun MiniPlayer(
    state: PlayerState,
    onTogglePlay: () -> Unit,
    onSkipNext: () -> Unit,
    onSkipPrev: () -> Unit,
    onClick: () -> Unit,
) {
    val song = state.currentSong ?: return
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 10.dp, vertical = 4.dp)
            .clip(RoundedCornerShape(20.dp))
            .background(
                Brush.horizontalGradient(
                    colors = listOf(LucidColors.Card, LucidColors.SurfaceHigh)
                )
            )
            .border(0.5.dp, LucidColors.GlassBorder, RoundedCornerShape(20.dp))
            .clickable(onClick = onClick)
    ) {
        // Progress line at very top
        Box(
            modifier = Modifier
                .fillMaxWidth(state.progress)
                .height(2.dp)
                .align(Alignment.TopStart)
                .clip(RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp))
                .background(
                    Brush.horizontalGradient(colors = listOf(LucidColors.Indigo, LucidColors.Aurora))
                )
        )
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 11.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // Artwork
            ArtworkImage(
                uri = song.artworkUri,
                modifier = Modifier.size(46.dp),
                cornerRadius = 10.dp
            )
            // Song info
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    song.title,
                    style = MaterialTheme.typography.labelLarge,
                    color = LucidColors.Text100,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    song.artist,
                    style = MaterialTheme.typography.labelSmall,
                    color = LucidColors.Text50,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
            // Controls
            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                GlowIconButton(
                    icon = Icons.Rounded.SkipPrevious,
                    contentDescription = "Previous",
                    onClick = onSkipPrev,
                    size = 36.dp, iconSize = 18.dp,
                    tint = LucidColors.Text80
                )
                GlowIconButton(
                    icon = if (state.isPlaying) Icons.Rounded.Pause else Icons.Rounded.PlayArrow,
                    contentDescription = "Play/Pause",
                    onClick = onTogglePlay,
                    size = 40.dp, iconSize = 22.dp,
                    active = true, activeColor = LucidColors.Indigo
                )
                GlowIconButton(
                    icon = Icons.Rounded.SkipNext,
                    contentDescription = "Next",
                    onClick = onSkipNext,
                    size = 36.dp, iconSize = 18.dp,
                    tint = LucidColors.Text80
                )
            }
        }
    }
}
