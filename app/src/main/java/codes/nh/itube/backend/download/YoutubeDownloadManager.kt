package codes.nh.itube.backend.download

import android.net.Uri
import codes.nh.itube.backend.library.Song
import codes.nh.itube.backend.library.SongManager
import codes.nh.itube.backend.utils.LOG
import codes.nh.itube.backend.utils.async
import java.util.concurrent.Future

class YoutubeDownloadManager(private val songManager: SongManager) {

    private val downloadState = YoutubeDownloadState()

    fun getDownloadState(): YoutubeDownloadState {
        return downloadState
    }

    //

    private var downloadThread: Future<*>? = null

    private var lastFileUri: Uri? = null

    fun start(
        downloadInfo: YoutubeDownloadInfo,
        bufferBytes: Long,
        onSuccess: (song: Song) -> Unit
    ) {
        val download = YoutubeDownload(downloadInfo)

        downloadState.active = true
        downloadState.fileName = download.fileName
        downloadState.progressBytes = 0L
        downloadState.sizeBytes = download.contentLength
        downloadState.startTime = System.currentTimeMillis()

        downloadThread = async {

            val uri = songManager.createSong(download.fileName)
            if (uri == null) {
                LOG("createFile error")
                downloadState.progressBytes = -1
                return@async
            }

            lastFileUri = uri

            val outputStream = songManager.openSongForWriting(uri)
            if (outputStream == null) {
                LOG("openFileForWriting error")
                downloadState.progressBytes = -1
                songManager.deleteSong(uri)
                return@async
            }

            download.start(
                outputStream = outputStream,
                buffer = bufferBytes,
                onProgress = { progress ->
                    downloadState.progressBytes = progress
                }
            )

            downloadState.active = false

            val song = songManager.getSingleSong(uri)
            onSuccess(song)

        }
    }

    fun cancel() {
        val canceled = downloadThread?.cancel(true) ?: false
        downloadState.active = false
        if (canceled) {
            val uri = lastFileUri ?: return
            async {
                songManager.deleteSong(uri)
            }
        }
    }
}