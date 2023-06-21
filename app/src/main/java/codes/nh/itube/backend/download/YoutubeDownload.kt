package codes.nh.itube.backend.download

import codes.nh.itube.backend.utils.LOG
import java.io.InputStream
import java.io.OutputStream
import java.net.URL


class YoutubeDownload(downloadInfo: YoutubeDownloadInfo) {

    val fileName = downloadInfo.videoTitle

    private val downloadUrl = downloadInfo.downloadUrl.toString()
    private val range = downloadInfo.downloadUrl.getQueryParameter("range")!!
    val contentLength = downloadInfo.downloadUrl.getQueryParameter("clen")!!.toLong()

    fun start(
        outputStream: OutputStream,
        buffer: Long = 1000000L,
        onProgress: (progress: Long) -> Unit
    ) {
        val startTime = System.currentTimeMillis()
        LOG("STARTED DOWNLOAD $fileName")

        outputStream.use { output ->
            download(output, buffer, onProgress)
        }

        val time = System.currentTimeMillis() - startTime
        LOG("ENDED DOWNLOAD ($time ms)")
    }

    private fun download(output: OutputStream, buffer: Long, onProgress: (progress: Long) -> Unit) {
        var totalProgress = 0L
        var start = 0L
        while (start < contentLength) {
            val end = start + buffer - 1L //todo maybe math.max
            downloadPart(output, start, end) { progress ->
                totalProgress += progress
                onProgress(totalProgress)
            }
            start += buffer
        }
    }

    private fun downloadPart(
        output: OutputStream,
        startRange: Long,
        endRange: Long,
        onProgress: (progress: Int) -> Unit
    ) {
        val url = downloadUrl.replace(range, "$startRange-$endRange")
        val inputStream = URL(url).openStream()
        inputStream.use { input ->
            copy(input, output, onProgress)
        }
    }

    //see InputStream.copyTo
    private fun copy(
        inputStream: InputStream,
        outputStream: OutputStream,
        onProgress: (progress: Int) -> Unit,
        bufferSize: Int = DEFAULT_BUFFER_SIZE
    ) {
        val buffer = ByteArray(bufferSize)
        var bytes = inputStream.read(buffer)
        while (bytes >= 0) {
            outputStream.write(buffer, 0, bytes)
            onProgress(bytes)
            bytes = inputStream.read(buffer)
        }
    }
}