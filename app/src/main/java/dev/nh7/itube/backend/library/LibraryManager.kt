package dev.nh7.itube.backend.library

import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import dev.nh7.itube.backend.mediastore.MediaStoreManager
import dev.nh7.itube.backend.permission.FilePermissionManager
import dev.nh7.itube.backend.permission.FilePermissionRequest
import dev.nh7.itube.backend.utils.async

class LibraryManager(context: Context) {

    private val mediaStoreManager = MediaStoreManager(context)

    //songs ==================================================================

    val songManager = SongManager(context, mediaStoreManager)

    //library state

    private val songsStateList = mutableStateListOf<Song>()

    fun getSongs(): List<Song> {
        return songsStateList
    }

    fun addToSongs(song: Song) {
        songsStateList.add(song)
    }

    //edit state

    private var editSongState by mutableStateOf(null as Song?)

    fun openEditSongDialog(song: Song) {
        editSongState = song
    }

    fun closeEditSongDialog() {
        editSongState = null
    }

    fun getEditSong(): Song? {
        return editSongState
    }

    //load/delete/rename

    fun loadLibrary() {
        async {
            val songs = songManager.getAllSongs()
            songsStateList.clear()
            songsStateList.addAll(songs)
        }
    }

    fun deleteSong(song: Song) {
        async {
            filePermissionManager.handleSecurityException(
                f = {

                    val rowsDeleted = songManager.deleteSong(song.contentUri)
                    if (rowsDeleted == 1) {
                        songsStateList.remove(song)
                    }

                },
                onSecurityException = {
                    filePermissionManager.setRequest(
                        FilePermissionRequest(it) { success ->
                            if (success) deleteSong(song)
                        }
                    )
                }
            )
        }
    }

    fun renameSong(song: Song, newFileName: String) {
        async {
            filePermissionManager.handleSecurityException(
                f = {

                    val rowsUpdated = songManager.renameSong(song.contentUri, newFileName)
                    if (rowsUpdated == 1) {
                        val updatedSong = songManager.getSingleSong(song.contentUri)
                        songsStateList[songsStateList.indexOf(song)] = updatedSong
                    }

                },
                onSecurityException = {
                    filePermissionManager.setRequest(
                        FilePermissionRequest(it) { success ->
                            if (success) renameSong(song, newFileName)
                        }
                    )
                }
            )
        }
    }

    //playlists ==================================================================

    private val playlistManager = PlaylistManager(context, mediaStoreManager)

    //library state

    private val playlistsStateList = mutableStateListOf<Playlist>()

    fun getPlaylists(): List<Playlist> {
        return playlistsStateList
    }

    //create state

    private var createPlaylistDialogOpenState by mutableStateOf(false)

    fun openCreatePlaylistDialog() {
        createPlaylistDialogOpenState = true
    }

    fun closeCreatePlaylistDialog() {
        createPlaylistDialogOpenState = false
    }

    fun isCreatePlaylistDialogOpen(): Boolean {
        return createPlaylistDialogOpenState
    }

    //edit playlist

    private var editPlaylistState by mutableStateOf(null as Playlist?)

    fun openEditPlaylistDialog(playlist: Playlist) {
        loadPlaylistSongs(playlist) { songs ->
            playlist.songs = songs
            editPlaylistState = playlist
        }
    }

    fun closeEditPlaylistDialog() {
        editPlaylistState = null
    }

    fun getEditPlaylist(): Playlist? {
        return editPlaylistState
    }

    //load/create/delete playlists

    fun loadPlaylists() {
        async {
            val playlists = playlistManager.getAllPlaylists()
            playlistsStateList.clear()
            playlistsStateList.addAll(playlists)
        }
    }

    fun loadPlaylistSongs(playlist: Playlist, onLoad: (songs: List<Song>?) -> Unit) {
        async {
            val playlistSongUris = playlistManager.readPlaylist(playlist.uri)
            if (playlistSongUris == null) {
                onLoad(null)
                return@async
            }
            val songs =
                songsStateList.filter { playlistSongUris.contains(it.contentUri.toString()) }
            onLoad(songs)
        }
    }

    fun createPlaylist(name: String, songUris: List<String>) {
        async {
            val uri = playlistManager.createPlaylist(name, songUris) ?: return@async
            val playlist = playlistManager.getSinglePlaylist(uri)
            playlistsStateList.add(playlist)
        }
    }

    fun deletePlaylist(playlistFile: Playlist) {
        async {
            filePermissionManager.handleSecurityException(
                f = {

                    val rowsDeleted = playlistManager.deletePlaylist(playlistFile.uri)
                    if (rowsDeleted == 1) {
                        playlistsStateList.remove(playlistFile)
                    }

                },
                onSecurityException = {
                    filePermissionManager.setRequest(
                        FilePermissionRequest(it) { success ->
                            if (success) deletePlaylist(playlistFile)
                        }
                    )
                }
            )
        }
    }

    //database ==================================================================

    val databaseManager = DatabaseManager(context, mediaStoreManager)

    //file permission ==================================================================

    val filePermissionManager = FilePermissionManager()

}