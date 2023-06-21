package codes.nh.itube.backend.library

import android.content.Context
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import codes.nh.itube.R
import codes.nh.itube.backend.mediastore.MediaStoreDirectory
import codes.nh.itube.backend.mediastore.MediaStoreManager
import codes.nh.itube.backend.utils.LOG


class DatabaseManager(context: Context, private val mediaStoreManager: MediaStoreManager) {

    private val databaseQueryFile = "database%.txt" //database(1).txt for example
    private val databaseFileName = "database.txt"

    private val databaseDirectory = MediaStoreDirectory(
        Environment.DIRECTORY_DOCUMENTS,
        context.getString(R.string.app_name).plus("/databases")
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

    private var database: Uri? = null

    private fun getDatabase(): Uri? {
        if (database == null) {
            val files = mediaStoreManager.getFilesFromDirectory(
                filesCollection,
                databaseDirectory,
                databaseQueryFile
            )
            LOG("database files: ${files.size} (${files.joinToString { it.name }})")
            database = if (files.isNotEmpty()) {
                files.last().uri
            } else {
                createDatabase()
            }
        }
        return database
    }

    private fun createDatabase(): Uri? {
        val uri =
            mediaStoreManager.createFile(databaseFileName, filesCollection, databaseDirectory)
                ?: return null
        val outputStream = mediaStoreManager.openFileOutputStream(uri, "wa") ?: return null
        outputStream.write("#${System.currentTimeMillis()}\n".toByteArray())
        return uri
    }

    fun writeLine(line: String): Boolean {
        val databaseFileUri = getDatabase() ?: return false
        val outputStream =
            mediaStoreManager.openFileOutputStream(databaseFileUri, "wa") ?: return false
        val data = line.plus('\n').toByteArray()
        outputStream.use { out ->
            out.write(data)
        }
        return true
    }

    fun readLines(): List<String>? {
        val databaseFileUri = getDatabase() ?: return null
        val inputStream = mediaStoreManager.openFileInputStream(databaseFileUri) ?: return null
        inputStream.bufferedReader().use { reader ->
            return reader.readLines().filter { !it.startsWith('#') }
        }
    }

}