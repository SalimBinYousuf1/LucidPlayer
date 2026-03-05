package com.lucid.player.viewmodel

import android.content.ComponentName
import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.session.MediaController
import androidx.media3.session.SessionToken
import com.google.common.util.concurrent.ListenableFuture
import com.google.common.util.concurrent.MoreExecutors
import com.lucid.player.data.models.Album
import com.lucid.player.data.models.Artist
import com.lucid.player.data.models.PlayerState
import com.lucid.player.data.models.RepeatMode
import com.lucid.player.data.models.Song
import com.lucid.player.data.repository.MusicRepository
import com.lucid.player.service.MusicService
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PlayerViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val repository: MusicRepository
) : ViewModel() {

    private val _playerState = MutableStateFlow(PlayerState())
    val playerState: StateFlow<PlayerState> = _playerState.asStateFlow()

    private val _songs = MutableStateFlow<List<Song>>(emptyList())
    val songs: StateFlow<List<Song>> = _songs.asStateFlow()

    private val _albums = MutableStateFlow<List<Album>>(emptyList())
    val albums: StateFlow<List<Album>> = _albums.asStateFlow()

    private val _artists = MutableStateFlow<List<Artist>>(emptyList())
    val artists: StateFlow<List<Artist>> = _artists.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _searchResults = MutableStateFlow<List<Song>>(emptyList())
    val searchResults: StateFlow<List<Song>> = _searchResults.asStateFlow()

    private val _favorites = MutableStateFlow<Set<Long>>(emptySet())
    val favorites: StateFlow<Set<Long>> = _favorites.asStateFlow()

    private var controllerFuture: ListenableFuture<MediaController>? = null
    private var controller: MediaController? = null

    init {
        loadLibrary()
        connectPlayer()
        startProgressTracking()
    }

    private fun connectPlayer() {
        val sessionToken = SessionToken(
            context,
            ComponentName(context, MusicService::class.java)
        )
        controllerFuture = MediaController.Builder(context, sessionToken).buildAsync()
        controllerFuture?.addListener({
            controller = controllerFuture?.get()
            controller?.addListener(playerListener)
        }, MoreExecutors.directExecutor())
    }

    private val playerListener = object : Player.Listener {
        override fun onIsPlayingChanged(isPlaying: Boolean) {
            _playerState.update { it.copy(isPlaying = isPlaying) }
        }

        override fun onMediaItemTransition(mediaItem: MediaItem?, reason: Int) {
            val index = controller?.currentMediaItemIndex ?: -1
            if (index >= 0 && index < _playerState.value.queue.size) {
                _playerState.update {
                    it.copy(
                        currentSong = it.queue[index],
                        currentIndex = index
                    )
                }
            }
        }

        override fun onRepeatModeChanged(repeatMode: Int) {
            val mode = when (repeatMode) {
                Player.REPEAT_MODE_ONE -> RepeatMode.ONE
                Player.REPEAT_MODE_ALL -> RepeatMode.ALL
                else -> RepeatMode.OFF
            }
            _playerState.update { it.copy(repeatMode = mode) }
        }

        override fun onShuffleModeEnabledChanged(shuffleModeEnabled: Boolean) {
            _playerState.update { it.copy(isShuffled = shuffleModeEnabled) }
        }
    }

    private fun startProgressTracking() {
        viewModelScope.launch {
            while (true) {
                val ctrl = controller
                if (ctrl != null && ctrl.isPlaying) {
                    val position = ctrl.currentPosition
                    val duration = ctrl.duration.takeIf { it > 0 } ?: 1L
                    val progress = (position.toFloat() / duration.toFloat()).coerceIn(0f, 1f)
                    _playerState.update {
                        it.copy(progress = progress, currentPosition = position)
                    }
                }
                delay(500)
            }
        }
    }

    private fun loadLibrary() {
        viewModelScope.launch {
            repository.getAllSongs().collect { songList ->
                _songs.value = songList
            }
        }
        viewModelScope.launch {
            repository.getAllAlbums().collect { albumList ->
                _albums.value = albumList
            }
        }
        viewModelScope.launch {
            repository.getAllArtists().collect { artistList ->
                _artists.value = artistList
            }
        }
    }

    fun playSong(song: Song, queue: List<Song> = _songs.value) {
        val ctrl = controller ?: return
        val songIndex = queue.indexOf(song)
        val mediaItems = queue.map { s ->
            MediaItem.Builder()
                .setMediaId(s.id.toString())
                .setUri(s.uri)
                .build()
        }

        ctrl.setMediaItems(mediaItems, songIndex, 0L)
        ctrl.prepare()
        ctrl.play()

        _playerState.update {
            it.copy(
                currentSong = song,
                queue = queue,
                currentIndex = songIndex,
                isPlaying = true
            )
        }
    }

    fun togglePlayPause() {
        val ctrl = controller ?: return
        if (ctrl.isPlaying) ctrl.pause() else ctrl.play()
    }

    fun skipNext() {
        controller?.seekToNextMediaItem()
    }

    fun skipPrevious() {
        val ctrl = controller ?: return
        if (ctrl.currentPosition > 3000) {
            ctrl.seekTo(0)
        } else {
            ctrl.seekToPreviousMediaItem()
        }
    }

    fun seekTo(fraction: Float) {
        val ctrl = controller ?: return
        val duration = ctrl.duration.takeIf { it > 0 } ?: return
        ctrl.seekTo((duration * fraction).toLong())
    }

    fun toggleRepeat() {
        val ctrl = controller ?: return
        val newMode = when (_playerState.value.repeatMode) {
            RepeatMode.OFF -> {
                ctrl.repeatMode = Player.REPEAT_MODE_ALL
                RepeatMode.ALL
            }
            RepeatMode.ALL -> {
                ctrl.repeatMode = Player.REPEAT_MODE_ONE
                RepeatMode.ONE
            }
            RepeatMode.ONE -> {
                ctrl.repeatMode = Player.REPEAT_MODE_OFF
                RepeatMode.OFF
            }
        }
        _playerState.update { it.copy(repeatMode = newMode) }
    }

    fun toggleShuffle() {
        val ctrl = controller ?: return
        val newShuffle = !_playerState.value.isShuffled
        ctrl.shuffleModeEnabled = newShuffle
        _playerState.update { it.copy(isShuffled = newShuffle) }
    }

    fun toggleFavorite(songId: Long) {
        _favorites.update { current ->
            if (songId in current) current - songId else current + songId
        }
    }

    fun search(query: String) {
        _searchQuery.value = query
        viewModelScope.launch {
            if (query.isBlank()) {
                _searchResults.value = emptyList()
            } else {
                repository.searchSongs(query).collect {
                    _searchResults.value = it
                }
            }
        }
    }

    override fun onCleared() {
        MediaController.releaseFuture(controllerFuture ?: return)
        super.onCleared()
    }
}
