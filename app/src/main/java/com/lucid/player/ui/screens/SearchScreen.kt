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
import androidx.compose.ui.*
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import com.lucid.player.data.models.Song
import com.lucid.player.ui.components.*
import com.lucid.player.ui.theme.LucidColors
import com.lucid.player.viewmodel.PlayerViewModel

@Composable
fun SearchScreen(vm: PlayerViewModel, onSongClick: (Song) -> Unit) {
    val query   by vm.query.collectAsState()
    val results by vm.results.collectAsState()
    val songs   by vm.songs.collectAsState()
    val state   by vm.state.collectAsState()
    val favIds  by vm.favIds.collectAsState()
    val focus   = LocalFocusManager.current

    Column(
        Modifier.fillMaxSize().background(LucidColors.Void).statusBarsPadding()
    ) {
        Text("Search", style = MaterialTheme.typography.headlineLarge,
            color = LucidColors.Text100, fontWeight = FontWeight.Black,
            modifier = Modifier.padding(horizontal = 22.dp, vertical = 20.dp))

        // Search field
        TextField(
            value = query,
            onValueChange = { vm.search(it) },
            modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp)
                .clip(RoundedCornerShape(18.dp)),
            placeholder = { Text("Songs, artists, albums…", color = LucidColors.Text30) },
            leadingIcon = { Icon(Icons.Rounded.Search, null, tint = LucidColors.Text50, modifier = Modifier.size(20.dp)) },
            trailingIcon = {
                if (query.isNotEmpty()) IconButton(onClick = { vm.search("") }) {
                    Icon(Icons.Rounded.Clear, null, tint = LucidColors.Text50, modifier = Modifier.size(18.dp))
                }
            },
            singleLine = true,
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
            keyboardActions = KeyboardActions(onSearch = { focus.clearFocus() }),
            colors = TextFieldDefaults.colors(
                focusedContainerColor   = LucidColors.Card,
                unfocusedContainerColor = LucidColors.Surface,
                focusedTextColor   = LucidColors.Text100,
                unfocusedTextColor = LucidColors.Text100,
                focusedIndicatorColor   = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
                cursorColor = LucidColors.Indigo
            )
        )
        Spacer(Modifier.height(16.dp))

        val displayList = if (query.isBlank()) songs else results

        if (query.isNotBlank() && results.isEmpty()) {
            // Empty state
            Box(Modifier.fillMaxSize(), Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(Icons.Rounded.SearchOff, null, tint = LucidColors.Text30, modifier = Modifier.size(64.dp))
                    Spacer(Modifier.height(12.dp))
                    Text("No results for "$query"", style = MaterialTheme.typography.titleMedium, color = LucidColors.Text50)
                }
            }
        } else {
            if (query.isNotBlank()) {
                Text("${results.size} results",
                    style = MaterialTheme.typography.labelMedium,
                    color = LucidColors.Text50,
                    modifier = Modifier.padding(horizontal = 22.dp, vertical = 4.dp))
            }
            LazyColumn {
                items(displayList) { song ->
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
    }
}
