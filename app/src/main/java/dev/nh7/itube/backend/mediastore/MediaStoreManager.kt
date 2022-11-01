package dev.nh7.itube.backend.mediastore

import android.content.ContentUris
import android.content.ContentValues
import android.content.Context
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import java.io.File
import java.io.InputStream
import java.io.OutputStream

class MediaStoreManager(context: Context) {

    private val contentResolver = context.contentResolver

    fun createFile(
        fileName: String,
        collection: Uri,
        directory: MediaStoreDirectory,
        columnValues: Map<String, String> = emptyMap()
    ): Uri? {

        val contentValues = ContentValues().apply {

            columnValues.entries.forEach { put(it.key, it.value) }

            put(MediaStore.MediaColumns.DISPLAY_NAME, fileName)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                put(MediaStore.MediaColumns.RELATIVE_PATH, directory.relativeDirectoryString)
            } else {
                val directoryFile = directory.fullDirectoryFile
                if (!directoryFile.exists()) {
                    val success = directoryFile.mkdirs()
                    if (!success) throw Exception("ERROR CREATING DIRECTORY ${directoryFile.absolutePath}")
                }
                val absolutePath = File(directoryFile, fileName).absolutePath
                put(MediaStore.MediaColumns.DATA, absolutePath)
            }
        }

        return contentResolver.insert(collection, contentValues)
    }

    fun updateFile(uri: Uri, columnValues: Map<String, String>): Int {
        val contentValues = ContentValues().apply {
            columnValues.entries.forEach { put(it.key, it.value) }
        }
        return contentResolver.update(uri, contentValues, null, null)
    }

    fun deleteFile(uri: Uri): Int {
        return contentResolver.delete(uri, null, null)
    }

    fun openFileOutputStream(uri: Uri, mode: String): OutputStream? {
        return contentResolver.openOutputStream(uri, mode)
    }

    fun openFileInputStream(uri: Uri): InputStream? {
        return contentResolver.openInputStream(uri)
    }

    private fun queryFiles(
        collection: Uri,
        columns: Array<String> = emptyArray(),
        selection: String? = null,
        selectionArgs: Array<String>? = null,
        sortOrder: String? = null,
        uriIsSingleFile: Boolean
    ): List<MediaStoreFile> {

        val projection = arrayOf(
            MediaStore.MediaColumns._ID,
            MediaStore.MediaColumns.DISPLAY_NAME,
            *columns
        )

        val query = contentResolver.query(
            collection,
            projection,
            selection,
            selectionArgs,
            sortOrder
        )

        val fileList = mutableListOf<MediaStoreFile>()
        query?.use { cursor ->

            val idColumn = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns._ID)
            val fileNameColumn = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DISPLAY_NAME)

            val columnIndexes = columns.associateWith { column ->
                cursor.getColumnIndexOrThrow(column)
            }

            while (cursor.moveToNext()) {

                val id = cursor.getLong(idColumn)
                val contentUri =
                    if (uriIsSingleFile) collection else ContentUris.withAppendedId(collection, id)

                val fileName = cursor.getString(fileNameColumn)

                val columnValues = columns.associateWith { column ->
                    val columnIndex = columnIndexes[column]!!
                    cursor.getString(columnIndex)
                }

                fileList.add(MediaStoreFile(contentUri, fileName, columnValues))
            }

        }
        return fileList
    }

    fun getFileByUri(
        uri: Uri,
        columns: Array<String> = emptyArray()
    ): MediaStoreFile {
        val files = queryFiles(uri, columns, uriIsSingleFile = true)
        if (files.size != 1) throw Exception("files.size != 1")
        return files[0]
    }

    fun getFilesFromDirectory(
        collection: Uri,
        directory: MediaStoreDirectory,
        fileExtension: String = "",
        columns: Array<String> = emptyArray()
    ): List<MediaStoreFile> {

        val selection = "${MediaStore.MediaColumns.DATA} LIKE ?"
        val selectionArgs = arrayOf("%/${directory.relativeDirectoryString}/%$fileExtension")

        val sortOrder = "${MediaStore.MediaColumns.DATE_MODIFIED} ASC"

        return queryFiles(
            collection,
            columns,
            selection,
            selectionArgs,
            sortOrder,
            false
        )
    }

}
