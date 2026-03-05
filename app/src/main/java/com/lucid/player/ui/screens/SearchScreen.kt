package com.lucid.player.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import com.lucid.player.data.models.Song
import com.lucid.player.ui.theme.*
import com.lucid.player.viewmodel.PlayerViewModel

@Composable
fun SearchScreen(
    viewModel: PlayerViewModel,
    onSongClick: (Song) -> Unit
) {
    val searchQuery by viewModel.searchQuery.collectAsState()
    val searchResults by viewModel.searchResults.collectAsState()
    val songs by viewModel.songs.collectAsState()
    val playerState by viewModel.playerState.collectAsState()
    val favorites by viewModel.favorites.collectAsState()
    val focusManager = LocalFocusManager.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Void)
            .statusBarsPadding()
    ) {
        // Header
        Text(
            "Search",
            style = MaterialTheme.typography.headlineLarge,
            color = TextPrimary,
            fontWeight = FontWeight.ExtraBold,
            modifier = Modifier.padding(horizontal = 20.dp, vertical = 20.dp)
        )

        // Search field
        TextField(
            value = searchQuery,
            onValueChange = { viewModel.search(it) },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .clip(RoundedCornerShape(16.dp)),
            placeholder = {
                Text("Songs, artists, albums...", color = TextTertiary)
            },
            leadingIcon = {
                Icon(Icons.Rounded.Search, contentDescription = null, tint = TextSecondary)
            },
            trailingIcon = {
                if (searchQuery.isNotEmpty()) {
                    IconButton(onClick = { viewModel.search("") }) {
                        Icon(Icons.Rounded.Clear, contentDescription = "Clear", tint = TextSecondary)
                    }
                }
            },
            singleLine = true,
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
            keyboardActions = KeyboardActions(onSearch = { focusManager.clearFocus() }),
            colors = TextFieldDefaults.colors(
                focusedContainerColor = Surface2,
                unfocusedContainerColor = Surface1,
                focusedTextColor = TextPrimary,
                unfocusedTextColor = TextPrimary,
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
                cursorColor = NeonPurple
            )
        )

        Spacer(modifier = Modifier.height(16.dp))

        if (searchQuery.isBlank()) {
            // All songs when no query
            LazyColumn {
                item {
                    Text(
                        "All Tracks",
                        style = MaterialTheme.typography.titleMedium,
                        color = TextSecondary,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp)
                    )
                }
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
        } else if (searchResults.isEmpty()) {
            // Empty state
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        Icons.Rounded.SearchOff,
                        contentDescription = null,
                        tint = TextTertiary,
                        modifier = Modifier.size(64.dp)
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text("No results for \"$searchQuery\"", style = MaterialTheme.typography.bodyLarge, color = TextSecondary)
                }
            }
        } else {
            // Search results
            LazyColumn {
                item {
                    Text(
                        "${searchResults.size} results",
                        style = MaterialTheme.typography.titleMedium,
                        color = TextSecondary,
                        modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp)
                    )
                }
                items(searchResults) { song ->
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
