package com.lucid.player.data.models

import android.net.Uri

data class Song(
    val id: Long,
    val title: String,
    val artist: String,
    val album: String,
    val albumId: Long,
    val duration: Long,          // ms
    val uri: Uri,
    val artworkUri: Uri?,
    val trackNumber: Int = 0,
    val year: Int = 0,
    val dateAdded: Long = 0L,
    val size: Long = 0L,
    val isFavorite: Boolean = false
) {
    val durationFormatted: String get() {
        val s = duration / 1000
        return "%d:%02d".format(s / 60, s % 60)
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
    val createdAt: Long = System.currentTimeMillis()
) {
    val songCount: Int  get() = songs.size
    val totalDuration: Long get() = songs.sumOf { it.duration }
}

enum class RepeatMode { OFF, ALL, ONE }

enum class PlayerTab { HOME, LIBRARY, ARTISTS, SEARCH }

data class PlayerState(
    val currentSong: Song? = null,
    val isPlaying: Boolean = false,
    val progress: Float = 0f,
    val currentPosition: Long = 0L,
    val duration: Long = 0L,
    val repeatMode: RepeatMode = RepeatMode.OFF,
    val isShuffled: Boolean = false,
    val queue: List<Song> = emptyList(),
    val currentIndex: Int = -1,
    val sleepTimerMinutes: Int = 0,
    val equalizerBands: List<Float> = listOf(0f, 0f, 0f, 0f, 0f),
    val volume: Float = 1f,
    val playbackSpeed: Float = 1f
)
