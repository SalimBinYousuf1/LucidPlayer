package com.lucid.player.ui.screens

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.lucid.player.data.models.Song
import com.lucid.player.ui.components.GradientDivider
import com.lucid.player.ui.theme.LucidColors
import com.lucid.player.viewmodel.PlayerViewModel

// Deterministic color per artist name
private fun artistGradient(name: String): Pair<Color, Color> {
    val colors = listOf(
        LucidColors.Indigo to LucidColors.Cosmic,
        LucidColors.Cosmic to LucidColors.Aurora,
        LucidColors.Aurora to LucidColors.Indigo,
        LucidColors.Ember to LucidColors.Indigo,
        LucidColors.Jade to LucidColors.Cosmic,
    )
    return colors[name.length % colors.size]
}

@Composable
fun ArtistsScreen(vm: PlayerViewModel, onSongClick: (Song) -> Unit) {
    val artists by vm.artists.collectAsState()

    Column(Modifier.fillMaxSize().background(LucidColors.Void).statusBarsPadding()) {
        Text("Artists", style = MaterialTheme.typography.headlineLarge,
            color = LucidColors.Text100, fontWeight = FontWeight.Black,
            modifier = Modifier.padding(horizontal = 22.dp, vertical = 20.dp))

        LazyColumn {
            items(artists) { artist ->
                val (c1, c2) = remember(artist.name) { artistGradient(artist.name) }
                Row(
                    modifier = Modifier.fillMaxWidth().clickable { }
                        .padding(horizontal = 20.dp, vertical = 13.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Gradient initial avatar
                    Box(
                        modifier = Modifier.size(58.dp).clip(CircleShape)
                            .background(Brush.radialGradient(colors = listOf(c1, c2))),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            artist.name.take(1).uppercase(),
                            style = MaterialTheme.typography.titleLarge,
                            color = Color.White, fontWeight = FontWeight.ExtraBold
                        )
                    }
                    Spacer(Modifier.width(16.dp))
                    Column(Modifier.weight(1f)) {
                        Text(artist.name, style = MaterialTheme.typography.bodyLarge,
                            color = LucidColors.Text100, fontWeight = FontWeight.SemiBold)
                        Text("${artist.albumCount} album${if (artist.albumCount != 1) "s" else ""} · ${artist.songCount} songs",
                            style = MaterialTheme.typography.bodySmall, color = LucidColors.Text50)
                    }
                    Icon(Icons.Rounded.ChevronRight, null, tint = LucidColors.Text30, modifier = Modifier.size(20.dp))
                }
                GradientDivider(Modifier.padding(start = 94.dp))
            }
        }
    }
}
