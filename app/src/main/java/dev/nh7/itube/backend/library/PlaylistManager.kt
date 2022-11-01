package dev.nh7.itube.backend.library

import android.content.Context
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import dev.nh7.itube.R
import dev.nh7.itube.backend.mediastore.MediaStoreDirectory
import dev.nh7.itube.backend.mediastore.MediaStoreFile
import dev.nh7.itube.backend.mediastore.MediaStoreManager


class PlaylistManager(context: Context, private val mediaStoreManager: MediaStoreManager) {

    private val playlistFileExtension = ".txt"

    private val playlistsDirectory = MediaStoreDirectory(
        Environment.DIRECTORY_DOCUMENTS,
        context.getString(R.string.app_name).plus("/playlists")
    )

    private val filesCollection = getFilesCollection()

    private fun getFilesCollection(): Uri {
        val collection =
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                MediaStore.Files.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY)
            } else {
                MediaStore.Files.getContentUri("external") //MediaStore.VOLUME_EXTERNAL
            }
        return collection
    }

    fun getSinglePlaylist(uri: Uri): Playlist {
        val file = mediaStoreManager.getFileByUri(uri)
        return getPlaylistFromFile(file)
    }

    fun getAllPlaylists(): List<Playlist> {
        val files = mediaStoreManager.getFilesFromDirectory(
            filesCollection,
            playlistsDirectory,
            playlistFileExtension
        )
        return files.map { file -> getPlaylistFromFile(file) }
    }

    private fun getPlaylistFromFile(file: MediaStoreFile): Playlist {
        return Playlist(file.uri, file.name.replace(playlistFileExtension, ""))
    }

    fun createPlaylist(name: String, files: List<String>): Uri? {
        val fileName = name.plus(playlistFileExtension)
        val uri = mediaStoreManager.createFile(fileName, filesCollection, playlistsDirectory)
            ?: return null
        val outputStream = mediaStoreManager.openFileOutputStream(uri, "w") ?: return null
        val data = files.joinToString("\n").toByteArray()
        outputStream.use { out ->
            out.write(data)
        }
        return uri
    }

    fun readPlaylist(uri: Uri): List<String>? {
        val inputStream = mediaStoreManager.openFileInputStream(uri) ?: return null
        inputStream.bufferedReader().use { reader ->
            return reader.readLines()
        }
    }

    fun deletePlaylist(uri: Uri): Int {
        return mediaStoreManager.deleteFile(uri)
    }

}