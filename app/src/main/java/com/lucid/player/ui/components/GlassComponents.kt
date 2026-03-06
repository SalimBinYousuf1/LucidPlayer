package com.lucid.player.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.*
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.lucid.player.data.models.Song
import com.lucid.player.ui.theme.LucidColors

/* ─── Glass card container ──────────────────────────────────────────────────── */
@Composable
fun GlassCard(
    modifier: Modifier = Modifier,
    cornerRadius: Dp = 20.dp,
    onClick: (() -> Unit)? = null,
    content: @Composable BoxScope.() -> Unit
) {
    val mod = modifier
        .clip(RoundedCornerShape(cornerRadius))
        .background(LucidColors.Glass10)
        .border(0.5.dp, LucidColors.GlassBorder, RoundedCornerShape(cornerRadius))
        .then(if (onClick != null) Modifier.clickable(onClick = onClick) else Modifier)
    Box(modifier = mod, content = content)
}

/* ─── Glow icon button ──────────────────────────────────────────────────────── */
@Composable
fun GlowIconButton(
    icon: ImageVector,
    contentDescription: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    size: Dp = 48.dp,
    iconSize: Dp = 22.dp,
    tint: Color = LucidColors.Text100,
    active: Boolean = false,
    activeColor: Color = LucidColors.Indigo,
) {
    Box(
        modifier = modifier
            .size(size)
            .clip(CircleShape)
            .background(if (active) activeColor.copy(alpha = 0.2f) else LucidColors.Glass10)
            .border(0.5.dp, if (active) activeColor.copy(alpha = 0.5f) else LucidColors.GlassBorder, CircleShape)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Icon(icon, contentDescription, tint = if (active) activeColor else tint, modifier = Modifier.size(iconSize))
    }
}

/* ─── Primary play button with pulsing glow ────────────────────────────────── */
@Composable
fun PrimaryPlayButton(
    isPlaying: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    size: Dp = 72.dp
) {
    val pulse by rememberInfiniteTransition(label = "pulse").animateFloat(
        initialValue = 0.6f, targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(1200, easing = FastOutSlowInEasing), RepeatMode.Reverse),
        label = "p"
    )
    Box(modifier = modifier, contentAlignment = Alignment.Center) {
        if (isPlaying) {
            Box(
                modifier = Modifier
                    .size(size * 1.35f)
                    .alpha(pulse * 0.25f)
                    .clip(CircleShape)
                    .background(LucidColors.Indigo)
                    .blur(16.dp)
            )
        }
        Box(
            modifier = Modifier
                .size(size)
                .clip(CircleShape)
                .background(Brush.radialGradient(colors = listOf(LucidColors.IndigoLight, LucidColors.Indigo, LucidColors.IndigoDim)))
                .clickable(onClick = onClick),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = if (isPlaying) androidx.compose.material.icons.Icons.Rounded.Pause
                              else androidx.compose.material.icons.Icons.Rounded.PlayArrow,
                contentDescription = if (isPlaying) "Pause" else "Play",
                tint = Color.White,
                modifier = Modifier.size(size * 0.46f)
            )
        }
    }
}

/* ─── Album artwork with fallback gradient ─────────────────────────────────── */
@Composable
fun ArtworkImage(
    uri: Any?,
    modifier: Modifier = Modifier,
    cornerRadius: Dp = 14.dp,
    contentScale: ContentScale = ContentScale.Crop
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(cornerRadius))
            .background(LucidColors.Card),
        contentAlignment = Alignment.Center
    ) {
        if (uri != null) {
            AsyncImage(model = uri, contentDescription = null, modifier = Modifier.fillMaxSize(), contentScale = contentScale)
        } else {
            Box(
                modifier = Modifier.fillMaxSize()
                    .background(Brush.radialGradient(colors = listOf(LucidColors.IndigoDim, LucidColors.Depth))),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    androidx.compose.material.icons.Icons.Rounded.MusicNote,
                    null, tint = LucidColors.Indigo.copy(alpha = 0.7f),
                    modifier = Modifier.fillMaxSize(0.4f)
                )
            }
        }
    }
}

/* ─── Song list row ────────────────────────────────────────────────────────── */
@Composable
fun SongRow(
    song: Song,
    isPlaying: Boolean,
    isFav: Boolean,
    onPlay: () -> Unit,
    onFav: () -> Unit,
    modifier: Modifier = Modifier,
    showTrackNumber: Boolean = false,
) {
    val bgColor = if (isPlaying) LucidColors.Indigo.copy(alpha = 0.10f) else Color.Transparent
    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(bgColor)
            .clickable(onClick = onPlay)
            .padding(horizontal = 20.dp, vertical = 11.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Artwork / playing indicator
        Box(modifier = Modifier.size(50.dp)) {
            ArtworkImage(uri = song.artworkUri, modifier = Modifier.fillMaxSize(), cornerRadius = 10.dp)
            if (isPlaying) {
                Box(
                    modifier = Modifier.fillMaxSize().clip(RoundedCornerShape(10.dp))
                        .background(LucidColors.Indigo.copy(alpha = 0.55f)),
                    contentAlignment = Alignment.Center
                ) {
                    PlayingBarsIndicator()
                }
            }
        }
        Spacer(Modifier.width(14.dp))
        Column(Modifier.weight(1f)) {
            Text(
                song.title,
                style = MaterialTheme.typography.bodyMedium,
                color = if (isPlaying) LucidColors.IndigoLight else LucidColors.Text100,
                fontWeight = if (isPlaying) FontWeight.SemiBold else FontWeight.Normal,
                maxLines = 1, overflow = TextOverflow.Ellipsis
            )
            Spacer(Modifier.height(2.dp))
            Text(
                "${song.artist} · ${song.durationFormatted}",
                style = MaterialTheme.typography.bodySmall,
                color = LucidColors.Text50,
                maxLines = 1, overflow = TextOverflow.Ellipsis
            )
        }
        Spacer(Modifier.width(8.dp))
        Box(
            modifier = Modifier.size(36.dp)
                .clip(CircleShape)
                .clickable(onClick = onFav),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                if (isFav) androidx.compose.material.icons.Icons.Rounded.Favorite
                else androidx.compose.material.icons.Icons.Rounded.FavoriteBorder,
                contentDescription = "Favorite",
                tint = if (isFav) LucidColors.Ember else LucidColors.Text30,
                modifier = Modifier.size(18.dp)
            )
        }
    }
}

/* ─── Animated playing bars (3 bars bouncing) ──────────────────────────────── */
@Composable
fun PlayingBarsIndicator(modifier: Modifier = Modifier) {
    val transition = rememberInfiniteTransition(label = "bars")
    val delays = listOf(0, 160, 320)
    val heights = delays.map { delay ->
        transition.animateFloat(
            initialValue = 0.3f, targetValue = 1f,
            animationSpec = infiniteRepeatable(
                tween(500, delayMillis = delay, easing = FastOutSlowInEasing), RepeatMode.Reverse
            ), label = "bar$delay"
        )
    }
    Row(modifier = modifier.size(20.dp), horizontalArrangement = Arrangement.spacedBy(2.dp), verticalAlignment = Alignment.Bottom) {
        heights.forEach { h ->
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight(h.value)
                    .clip(RoundedCornerShape(1.dp))
                    .background(Color.White)
            )
        }
    }
}

/* ─── Gradient divider ──────────────────────────────────────────────────────── */
@Composable
fun GradientDivider(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(0.5.dp)
            .background(
                Brush.horizontalGradient(
                    colors = listOf(Color.Transparent, LucidColors.GlassBorder, Color.Transparent)
                )
            )
    )
}

/* ─── Section header ────────────────────────────────────────────────────────── */
@Composable
fun SectionHeader(title: String, subtitle: String? = null, actionLabel: String? = null, onAction: (() -> Unit)? = null) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.Bottom
    ) {
        Column {
            Text(title, style = MaterialTheme.typography.titleLarge, color = LucidColors.Text100, fontWeight = FontWeight.Bold)
            if (subtitle != null)
                Text(subtitle, style = MaterialTheme.typography.labelSmall, color = LucidColors.Text50)
        }
        if (actionLabel != null && onAction != null) {
            Text(actionLabel, style = MaterialTheme.typography.labelMedium,
                color = LucidColors.Indigo, modifier = Modifier.clickable(onClick = onAction))
        }
    }
}
