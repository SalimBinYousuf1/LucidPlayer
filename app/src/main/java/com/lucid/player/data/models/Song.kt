package com.lucid.player.data.models

import android.net.Uri

data class Song(
    val id: Long,
    val title: String,
    val artist: String,
    val album: String,
    val albumId: Long,
    val duration: Long, // milliseconds
    val uri: Uri,
    val artworkUri: Uri?,
    val trackNumber: Int = 0,
    val year: Int = 0,
    val dateAdded: Long = 0L,
    val size: Long = 0L,
    val isFavorite: Boolean = false
) {
    val durationFormatted: String
        get() {
            val totalSeconds = duration / 1000
            val minutes = totalSeconds / 60
            val seconds = totalSeconds % 60
            return "%d:%02d".format(minutes, seconds)
        }
}

data class Album(
    val id: Long,
    val name: String,
    val artist: String,
    val songCount: Int,
    val artworkUri: Uri?,
    val year: Int = 0
)

data class Artist(
    val id: Long,
    val name: String,
    val albumCount: Int,
    val songCount: Int
)

data class Playlist(
    val id: Long,
    val name: String,
    val songs: List<Song> = emptyList(),
    val createdAt: Long = System.currentTimeMillis(),
    val artworkUri: Uri? = null
) {
    val songCount: Int get() = songs.size
    val totalDuration: Long get() = songs.sumOf { it.duration }
}

enum class RepeatMode {
    OFF, ONE, ALL
}

data class PlayerState(
    val currentSong: Song? = null,
    val isPlaying: Boolean = false,
    val progress: Float = 0f,
    val currentPosition: Long = 0L,
    val repeatMode: RepeatMode = RepeatMode.OFF,
    val isShuffled: Boolean = false,
    val queue: List<Song> = emptyList(),
    val currentIndex: Int = -1,
    val dominantColor: Int = 0xFF1A1A2E.toInt(),
    val accentColor: Int = 0xFF6C63FF.toInt()
)
