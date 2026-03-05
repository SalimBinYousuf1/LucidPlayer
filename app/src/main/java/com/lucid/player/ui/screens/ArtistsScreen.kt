package com.lucid.player.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.lucid.player.data.models.Song
import com.lucid.player.ui.theme.*
import com.lucid.player.viewmodel.PlayerViewModel

@Composable
fun ArtistsScreen(
    viewModel: PlayerViewModel,
    onSongClick: (Song) -> Unit
) {
    val artists by viewModel.artists.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Void)
            .statusBarsPadding()
    ) {
        Text(
            "Artists",
            style = MaterialTheme.typography.headlineLarge,
            color = TextPrimary,
            fontWeight = FontWeight.ExtraBold,
            modifier = Modifier.padding(horizontal = 20.dp, vertical = 20.dp)
        )

        LazyColumn {
            items(artists) { artist ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { }
                        .padding(horizontal = 20.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Artist avatar
                    Box(
                        modifier = Modifier
                            .size(56.dp)
                            .clip(CircleShape)
                            .background(
                                Brush.radialGradient(
                                    colors = listOf(NeonPurple.copy(0.5f), CelestialBlue.copy(0.3f))
                                )
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            artist.name.take(1).uppercase(),
                            style = MaterialTheme.typography.titleLarge,
                            color = TextPrimary,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Spacer(modifier = Modifier.width(16.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(artist.name, style = MaterialTheme.typography.bodyLarge, color = TextPrimary, fontWeight = FontWeight.SemiBold)
                        Text(
                            "${artist.albumCount} albums • ${artist.songCount} songs",
                            style = MaterialTheme.typography.bodySmall,
                            color = TextSecondary
                        )
                    }

                    Icon(Icons.Rounded.ChevronRight, contentDescription = null, tint = TextTertiary, modifier = Modifier.size(20.dp))
                }
                HorizontalDivider(color = Surface1, thickness = 0.5.dp, modifier = Modifier.padding(start = 92.dp))
            }
        }
    }
}
