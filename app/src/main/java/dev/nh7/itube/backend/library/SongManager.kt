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
import java.io.OutputStream


class SongManager(context: Context, private val mediaStoreManager: MediaStoreManager) {

    //actually .webm, but can't save audio/webm files into music directory
    private val songFileExtension = ".mp3"

    private val songDirectory = MediaStoreDirectory(
        Environment.DIRECTORY_MUSIC,
        context.getString(R.string.app_name)
    )

    private val audioCollection = getAudioCollection()

    private fun getAudioCollection(): Uri {
        val collection =
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                MediaStore.Audio.Media.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY)
            } else {
                MediaStore.Audio.Media.getContentUri("external") //MediaStore.VOLUME_EXTERNAL
            }
        return collection
    }

    fun getSingleSong(uri: Uri): Song {
        val file = mediaStoreManager.getFileByUri(uri)
        return getSongFromFile(file)
    }

    fun getAllSongs(): List<Song> {
        val files = mediaStoreManager.getFilesFromDirectory(
            audioCollection,
            songDirectory,
            songFileExtension
        )
        return files.map { file -> getSongFromFile(file) }
    }

    private fun getSongFromFile(file: MediaStoreFile): Song {
        return Song(file.uri, file.name.replace(songFileExtension, ""))
    }

    fun createSong(fileName: String): Uri? {
        val fileNameWithExtension = fileName.plus(songFileExtension)
        return mediaStoreManager.createFile(fileNameWithExtension, audioCollection, songDirectory)
    }

    fun openSongForWriting(uri: Uri): OutputStream? {
        return mediaStoreManager.openFileOutputStream(uri, "w")
    }

    fun renameSong(uri: Uri, newFileName: String): Int {
        val columnValues = mapOf(MediaStore.MediaColumns.DISPLAY_NAME to newFileName)
        return mediaStoreManager.updateFile(uri, columnValues)
    }

    fun deleteSong(uri: Uri): Int {
        return mediaStoreManager.deleteFile(uri)
    }

}