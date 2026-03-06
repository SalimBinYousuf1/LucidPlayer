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
    fun getAllSongs(sort: String = "${MediaStore.Audio.Media.TITLE} ASC"): Flow<List<Song>> =
        flow { emit(querySongs(sortOrder = sort)) }.flowOn(Dispatchers.IO)

    fun getAllAlbums(): Flow<List<Album>> =
        flow { emit(queryAlbums()) }.flowOn(Dispatchers.IO)

    fun getAllArtists(): Flow<List<Artist>> =
        flow { emit(queryArtists()) }.flowOn(Dispatchers.IO)

    fun getRecentSongs(limit: Int = 20): Flow<List<Song>> =
        flow { emit(querySongs(sortOrder = "${MediaStore.Audio.Media.DATE_ADDED} DESC").take(limit)) }
            .flowOn(Dispatchers.IO)

    fun searchSongs(q: String): Flow<List<Song>> = flow {
        emit(querySongs(
            selection = "${MediaStore.Audio.Media.TITLE} LIKE ? OR ${MediaStore.Audio.Media.ARTIST} LIKE ? OR ${MediaStore.Audio.Media.ALBUM} LIKE ?",
            selectionArgs = arrayOf("%$q%", "%$q%", "%$q%")
        ))
    }.flowOn(Dispatchers.IO)

    private fun querySongs(
        selection: String? = null,
        selectionArgs: Array<String>? = null,
        sortOrder: String = "${MediaStore.Audio.Media.TITLE} ASC"
    ): List<Song> {
        val songs = mutableListOf<Song>()
        val base = "${MediaStore.Audio.Media.IS_MUSIC}!=0 AND ${MediaStore.Audio.Media.DURATION}>30000"
        val finalSel = if (selection != null) "$base AND $selection" else base
        val proj = arrayOf(
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
        )
        context.contentResolver.query(
            MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, proj, finalSel, selectionArgs, sortOrder
        )?.use { c ->
            val iId   = c.getColumnIndexOrThrow(MediaStore.Audio.Media._ID)
            val iTitle= c.getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE)
            val iArt  = c.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST)
            val iAlb  = c.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM)
            val iAId  = c.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM_ID)
            val iDur  = c.getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION)
            val iTrk  = c.getColumnIndexOrThrow(MediaStore.Audio.Media.TRACK)
            val iYr   = c.getColumnIndexOrThrow(MediaStore.Audio.Media.YEAR)
            val iDate = c.getColumnIndexOrThrow(MediaStore.Audio.Media.DATE_ADDED)
            val iSize = c.getColumnIndexOrThrow(MediaStore.Audio.Media.SIZE)
            while (c.moveToNext()) {
                val id    = c.getLong(iId)
                val albId = c.getLong(iAId)
                songs += Song(
                    id          = id,
                    title       = c.getString(iTitle)  ?: "Unknown",
                    artist      = c.getString(iArt)    ?: "Unknown Artist",
                    album       = c.getString(iAlb)    ?: "Unknown Album",
                    albumId     = albId,
                    duration    = c.getLong(iDur),
                    uri         = ContentUris.withAppendedId(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, id),
                    artworkUri  = Uri.parse("content://media/external/audio/albumart/$albId"),
                    trackNumber = c.getInt(iTrk),
                    year        = c.getInt(iYr),
                    dateAdded   = c.getLong(iDate),
                    size        = c.getLong(iSize),
                )
            }
        }
        return songs
    }

    private fun queryAlbums(): List<Album> {
        val list = mutableListOf<Album>()
        val proj = arrayOf(MediaStore.Audio.Albums._ID, MediaStore.Audio.Albums.ALBUM,
            MediaStore.Audio.Albums.ARTIST, MediaStore.Audio.Albums.NUMBER_OF_SONGS,
            MediaStore.Audio.Albums.FIRST_YEAR)
        context.contentResolver.query(
            MediaStore.Audio.Albums.EXTERNAL_CONTENT_URI, proj, null, null,
            "${MediaStore.Audio.Albums.ALBUM} ASC"
        )?.use { c ->
            val iId = c.getColumnIndexOrThrow(MediaStore.Audio.Albums._ID)
            val iAlb= c.getColumnIndexOrThrow(MediaStore.Audio.Albums.ALBUM)
            val iArt= c.getColumnIndexOrThrow(MediaStore.Audio.Albums.ARTIST)
            val iCnt= c.getColumnIndexOrThrow(MediaStore.Audio.Albums.NUMBER_OF_SONGS)
            val iYr = c.getColumnIndexOrThrow(MediaStore.Audio.Albums.FIRST_YEAR)
            while (c.moveToNext()) {
                val id = c.getLong(iId)
                list += Album(id, c.getString(iAlb) ?: "Unknown",
                    c.getString(iArt) ?: "Unknown", c.getInt(iCnt),
                    Uri.parse("content://media/external/audio/albumart/$id"), c.getInt(iYr))
            }
        }
        return list
    }

    private fun queryArtists(): List<Artist> {
        val list = mutableListOf<Artist>()
        val proj = arrayOf(MediaStore.Audio.Artists._ID, MediaStore.Audio.Artists.ARTIST,
            MediaStore.Audio.Artists.NUMBER_OF_ALBUMS, MediaStore.Audio.Artists.NUMBER_OF_TRACKS)
        context.contentResolver.query(
            MediaStore.Audio.Artists.EXTERNAL_CONTENT_URI, proj, null, null,
            "${MediaStore.Audio.Artists.ARTIST} ASC"
        )?.use { c ->
            val iId = c.getColumnIndexOrThrow(MediaStore.Audio.Artists._ID)
            val iNm = c.getColumnIndexOrThrow(MediaStore.Audio.Artists.ARTIST)
            val iAl = c.getColumnIndexOrThrow(MediaStore.Audio.Artists.NUMBER_OF_ALBUMS)
            val iTr = c.getColumnIndexOrThrow(MediaStore.Audio.Artists.NUMBER_OF_TRACKS)
            while (c.moveToNext())
                list += Artist(c.getLong(iId), c.getString(iNm) ?: "Unknown", c.getInt(iAl), c.getInt(iTr))
        }
        return list
    }
}
