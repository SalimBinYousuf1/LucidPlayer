package com.lucid.player.viewmodel

import android.content.ComponentName
import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.media3.common.MediaItem
import androidx.media3.common.PlaybackParameters
import androidx.media3.common.Player
import androidx.media3.session.MediaController
import androidx.media3.session.SessionToken
import com.google.common.util.concurrent.MoreExecutors
import com.lucid.player.data.models.*
import com.lucid.player.data.repository.MusicRepository
import com.lucid.player.service.MusicService
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import javax.inject.Inject

@HiltViewModel
class PlayerViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val repo: MusicRepository,
) : ViewModel() {

    /* ── Exposed state ─────────────────────────────────────────────────────── */
    private val _state   = MutableStateFlow(PlayerState())
    val state: StateFlow<PlayerState> = _state.asStateFlow()

    private val _songs   = MutableStateFlow<List<Song>>(emptyList())
    val songs: StateFlow<List<Song>> = _songs.asStateFlow()

    private val _albums  = MutableStateFlow<List<Album>>(emptyList())
    val albums: StateFlow<List<Album>> = _albums.asStateFlow()

    private val _artists = MutableStateFlow<List<Artist>>(emptyList())
    val artists: StateFlow<List<Artist>> = _artists.asStateFlow()

    private val _query   = MutableStateFlow("")
    val query: StateFlow<String> = _query.asStateFlow()

    private val _results = MutableStateFlow<List<Song>>(emptyList())
    val results: StateFlow<List<Song>> = _results.asStateFlow()

    private val _favIds  = MutableStateFlow<Set<Long>>(emptySet())
    val favIds: StateFlow<Set<Long>> = _favIds.asStateFlow()

    val favSongs: StateFlow<List<Song>> = combine(_songs, _favIds) { s, f ->
        s.filter { it.id in f }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val recentSongs: StateFlow<List<Song>> = _songs.map { it.sortedByDescending { s -> s.dateAdded }.take(20) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    /* ── Controller ────────────────────────────────────────────────────────── */
    private var controller: MediaController? = null
    private var sleepJob: Job? = null

    init { loadLibrary(); connectPlayer(); trackProgress() }

    private fun connectPlayer() {
        val token = SessionToken(context, ComponentName(context, MusicService::class.java))
        val future = MediaController.Builder(context, token).buildAsync()
        future.addListener({
            controller = future.get().also { it.addListener(playerListener) }
        }, MoreExecutors.directExecutor())
    }

    private val playerListener = object : Player.Listener {
        override fun onIsPlayingChanged(p: Boolean) = _state.update { it.copy(isPlaying = p) }
        override fun onMediaItemTransition(item: MediaItem?, reason: Int) {
            val idx = controller?.currentMediaItemIndex ?: return
            _state.update { s ->
                s.copy(currentSong = s.queue.getOrNull(idx), currentIndex = idx)
            }
        }
        override fun onRepeatModeChanged(m: Int) = _state.update {
            it.copy(repeatMode = when (m) {
                Player.REPEAT_MODE_ONE -> RepeatMode.ONE
                Player.REPEAT_MODE_ALL -> RepeatMode.ALL
                else                   -> RepeatMode.OFF
            })
        }
        override fun onShuffleModeEnabledChanged(s: Boolean) = _state.update { it.copy(isShuffled = s) }
    }

    private fun trackProgress() = viewModelScope.launch {
        while (true) {
            val ctrl = controller
            if (ctrl != null && ctrl.isPlaying) {
                val pos = ctrl.currentPosition
                val dur = ctrl.duration.takeIf { it > 0 } ?: 1L
                _state.update { it.copy(progress = (pos.toFloat() / dur).coerceIn(0f, 1f),
                    currentPosition = pos, duration = dur) }
            }
            delay(300)
        }
    }

    private fun loadLibrary() {
        viewModelScope.launch { repo.getAllSongs().collect { _songs.value = it } }
        viewModelScope.launch { repo.getAllAlbums().collect { _albums.value = it } }
        viewModelScope.launch { repo.getAllArtists().collect { _artists.value = it } }
    }

    /* ── Playback controls ─────────────────────────────────────────────────── */
    fun playSong(song: Song, queue: List<Song> = _songs.value) {
        val ctrl = controller ?: return
        val idx = queue.indexOf(song).coerceAtLeast(0)
        ctrl.setMediaItems(queue.map { MediaItem.Builder().setMediaId(it.id.toString()).setUri(it.uri).build() }, idx, 0L)
        ctrl.prepare(); ctrl.play()
        _state.update { it.copy(currentSong = song, queue = queue, currentIndex = idx, isPlaying = true) }
    }

    fun playAll()          { if (_songs.value.isNotEmpty()) playSong(_songs.value.first(), _songs.value) }
    fun shuffleAll()       { val s = _songs.value.shuffled(); if (s.isNotEmpty()) { toggleShuffle(true); playSong(s.first(), s) } }
    fun togglePlayPause()  { controller?.let { if (it.isPlaying) it.pause() else it.play() } }
    fun skipNext()         { controller?.seekToNextMediaItem() }
    fun skipPrev()         { controller?.let { if (it.currentPosition > 3000) it.seekTo(0) else it.seekToPreviousMediaItem() } }
    fun seekTo(f: Float)   { controller?.let { it.seekTo(((it.duration.takeIf { d -> d > 0 } ?: 1L) * f).toLong()) } }

    fun toggleRepeat() {
        val ctrl = controller ?: return
        val next = when (_state.value.repeatMode) {
            RepeatMode.OFF -> RepeatMode.ALL.also { ctrl.repeatMode = Player.REPEAT_MODE_ALL }
            RepeatMode.ALL -> RepeatMode.ONE.also { ctrl.repeatMode = Player.REPEAT_MODE_ONE }
            RepeatMode.ONE -> RepeatMode.OFF.also { ctrl.repeatMode = Player.REPEAT_MODE_OFF }
        }
        _state.update { it.copy(repeatMode = next) }
    }

    fun toggleShuffle(force: Boolean? = null) {
        val new = force ?: !_state.value.isShuffled
        controller?.shuffleModeEnabled = new
        _state.update { it.copy(isShuffled = new) }
    }

    fun setPlaybackSpeed(speed: Float) {
        controller?.playbackParameters = PlaybackParameters(speed)
        _state.update { it.copy(playbackSpeed = speed) }
    }

    fun setVolume(v: Float) {
        controller?.volume = v
        _state.update { it.copy(volume = v) }
    }

    /* ── Favorites ─────────────────────────────────────────────────────────── */
    fun toggleFav(id: Long) = _favIds.update { if (id in it) it - id else it + id }
    fun isFav(id: Long) = id in _favIds.value

    /* ── Search ────────────────────────────────────────────────────────────── */
    fun search(q: String) {
        _query.value = q
        viewModelScope.launch {
            if (q.isBlank()) _results.value = emptyList()
            else repo.searchSongs(q).collect { _results.value = it }
        }
    }

    /* ── Sleep timer ───────────────────────────────────────────────────────── */
    fun setSleepTimer(minutes: Int) {
        sleepJob?.cancel()
        _state.update { it.copy(sleepTimerMinutes = minutes) }
        if (minutes > 0) {
            sleepJob = viewModelScope.launch {
                delay(minutes * 60_000L)
                controller?.pause()
                _state.update { it.copy(sleepTimerMinutes = 0) }
            }
        }
    }

    fun cancelSleepTimer() { sleepJob?.cancel(); _state.update { it.copy(sleepTimerMinutes = 0) } }

    /* ── Queue management ──────────────────────────────────────────────────── */
    fun addToQueue(song: Song) {
        val newQ = _state.value.queue + song
        _state.update { it.copy(queue = newQ) }
        controller?.addMediaItem(MediaItem.Builder().setMediaId(song.id.toString()).setUri(song.uri).build())
    }

    fun removeFromQueue(index: Int) {
        val newQ = _state.value.queue.toMutableList().also { it.removeAt(index) }
        _state.update { it.copy(queue = newQ) }
        controller?.removeMediaItem(index)
    }

    override fun onCleared() { controller?.release(); super.onCleared() }
}
