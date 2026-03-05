package com.lucid.player.data.repository

import android.content.ContentUris
import android.content.Context
import android.net.Uri
import android.provider.MediaStore
import com.lucid.player.data.models.Album
import com.lucid.player.data.models.Artist
import com.lucid.player.data.models.Song
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MusicRepository @Inject constructor(
    @ApplicationContext private val context: Context
) {

    fun getAllSongs(): Flow<List<Song>> = flow {
        val songs = querySongs()
        emit(songs)
    }.flowOn(Dispatchers.IO)

    fun getAllAlbums(): Flow<List<Album>> = flow {
        val albums = queryAlbums()
        emit(albums)
    }.flowOn(Dispatchers.IO)

    fun getAllArtists(): Flow<List<Artist>> = flow {
        val artists = queryArtists()
        emit(artists)
    }.flowOn(Dispatchers.IO)

    fun getSongsByAlbum(albumId: Long): Flow<List<Song>> = flow {
        val songs = querySongs(
            selection = "${MediaStore.Audio.Media.ALBUM_ID} = ?",
            selectionArgs = arrayOf(albumId.toString())
        )
        emit(songs)
    }.flowOn(Dispatchers.IO)

    fun getSongsByArtist(artistName: String): Flow<List<Song>> = flow {
        val songs = querySongs(
            selection = "${MediaStore.Audio.Media.ARTIST} = ?",
            selectionArgs = arrayOf(artistName)
        )
        emit(songs)
    }.flowOn(Dispatchers.IO)

    fun searchSongs(query: String): Flow<List<Song>> = flow {
        val songs = querySongs(
            selection = "${MediaStore.Audio.Media.TITLE} LIKE ? OR ${MediaStore.Audio.Media.ARTIST} LIKE ? OR ${MediaStore.Audio.Media.ALBUM} LIKE ?",
            selectionArgs = arrayOf("%$query%", "%$query%", "%$query%")
        )
        emit(songs)
    }.flowOn(Dispatchers.IO)

    private fun querySongs(
        selection: String? = null,
        selectionArgs: Array<String>? = null,
        sortOrder: String = "${MediaStore.Audio.Media.TITLE} ASC"
    ): List<Song> {
        val songs = mutableListOf<Song>()
        val projection = arrayOf(
            MediaStore.Audio.Media._ID,
            MediaStore.Audio.Media.TITLE,
            MediaStore.Audio.Media.ARTIST,
            MediaStore.Audio.Media.ALBUM,
            MediaStore.Audio.Media.ALBUM_ID,
            MediaStore.Audio.Media.DURATION,
            MediaStore.Audio.Media.TRACK,
            MediaStore.Audio.Media.YEAR,
            MediaStore.Audio.Media.DATE_ADDED,
            MediaStore.Audio.Media.SIZE,
            MediaStore.Audio.Media.IS_MUSIC
        )

        val baseSelection = "${MediaStore.Audio.Media.IS_MUSIC} != 0 AND ${MediaStore.Audio.Media.DURATION} > 30000"
        val finalSelection = if (selection != null) "$baseSelection AND $selection" else baseSelection

        context.contentResolver.query(
            MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
            projection,
            finalSelection,
            selectionArgs,
            sortOrder
        )?.use { cursor ->
            val idCol = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media._ID)
            val titleCol = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE)
            val artistCol = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST)
            val albumCol = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM)
            val albumIdCol = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM_ID)
            val durationCol = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION)
            val trackCol = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.TRACK)
            val yearCol = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.YEAR)
            val dateAddedCol = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATE_ADDED)
            val sizeCol = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.SIZE)

            while (cursor.moveToNext()) {
                val id = cursor.getLong(idCol)
                val albumId = cursor.getLong(albumIdCol)
                val contentUri = ContentUris.withAppendedId(
                    MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, id
                )
                val artworkUri = Uri.parse("content://media/external/audio/albumart/$albumId")

                songs.add(
                    Song(
                        id = id,
                        title = cursor.getString(titleCol) ?: "Unknown",
                        artist = cursor.getString(artistCol) ?: "Unknown Artist",
                        album = cursor.getString(albumCol) ?: "Unknown Album",
                        albumId = albumId,
                        duration = cursor.getLong(durationCol),
                        uri = contentUri,
                        artworkUri = artworkUri,
                        trackNumber = cursor.getInt(trackCol),
                        year = cursor.getInt(yearCol),
                        dateAdded = cursor.getLong(dateAddedCol),
                        size = cursor.getLong(sizeCol)
                    )
                )
            }
        }
        return songs
    }

    private fun queryAlbums(): List<Album> {
        val albums = mutableListOf<Album>()
        val projection = arrayOf(
            MediaStore.Audio.Albums._ID,
            MediaStore.Audio.Albums.ALBUM,
            MediaStore.Audio.Albums.ARTIST,
            MediaStore.Audio.Albums.NUMBER_OF_SONGS,
            MediaStore.Audio.Albums.FIRST_YEAR
        )

        context.contentResolver.query(
            MediaStore.Audio.Albums.EXTERNAL_CONTENT_URI,
            projection,
            null, null,
            "${MediaStore.Audio.Albums.ALBUM} ASC"
        )?.use { cursor ->
            val idCol = cursor.getColumnIndexOrThrow(MediaStore.Audio.Albums._ID)
            val albumCol = cursor.getColumnIndexOrThrow(MediaStore.Audio.Albums.ALBUM)
            val artistCol = cursor.getColumnIndexOrThrow(MediaStore.Audio.Albums.ARTIST)
            val songCountCol = cursor.getColumnIndexOrThrow(MediaStore.Audio.Albums.NUMBER_OF_SONGS)
            val yearCol = cursor.getColumnIndexOrThrow(MediaStore.Audio.Albums.FIRST_YEAR)

            while (cursor.moveToNext()) {
                val id = cursor.getLong(idCol)
                albums.add(
                    Album(
                        id = id,
                        name = cursor.getString(albumCol) ?: "Unknown Album",
                        artist = cursor.getString(artistCol) ?: "Unknown Artist",
                        songCount = cursor.getInt(songCountCol),
                        artworkUri = Uri.parse("content://media/external/audio/albumart/$id"),
                        year = cursor.getInt(yearCol)
                    )
                )
            }
        }
        return albums
    }

    private fun queryArtists(): List<Artist> {
        val artists = mutableListOf<Artist>()
        val projection = arrayOf(
            MediaStore.Audio.Artists._ID,
            MediaStore.Audio.Artists.ARTIST,
            MediaStore.Audio.Artists.NUMBER_OF_ALBUMS,
            MediaStore.Audio.Artists.NUMBER_OF_TRACKS
        )

        context.contentResolver.query(
            MediaStore.Audio.Artists.EXTERNAL_CONTENT_URI,
            projection,
            null, null,
            "${MediaStore.Audio.Artists.ARTIST} ASC"
        )?.use { cursor ->
            val idCol = cursor.getColumnIndexOrThrow(MediaStore.Audio.Artists._ID)
            val artistCol = cursor.getColumnIndexOrThrow(MediaStore.Audio.Artists.ARTIST)
            val albumCountCol = cursor.getColumnIndexOrThrow(MediaStore.Audio.Artists.NUMBER_OF_ALBUMS)
            val songCountCol = cursor.getColumnIndexOrThrow(MediaStore.Audio.Artists.NUMBER_OF_TRACKS)

            while (cursor.moveToNext()) {
                artists.add(
                    Artist(
                        id = cursor.getLong(idCol),
                        name = cursor.getString(artistCol) ?: "Unknown Artist",
                        albumCount = cursor.getInt(albumCountCol),
                        songCount = cursor.getInt(songCountCol)
                    )
                )
            }
        }
        return artists
    }
}
