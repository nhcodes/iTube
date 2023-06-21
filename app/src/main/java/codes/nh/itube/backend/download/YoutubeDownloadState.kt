package codes.nh.itube.backend.download

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import codes.nh.itube.backend.utils.LOG


class YoutubeDownloadState {

    init {
        LOG("init YoutubeDownloadState")
    }

    var active by mutableStateOf(false)
    var fileName by mutableStateOf("")
    var progressBytes by mutableStateOf(0L)
    var sizeBytes by mutableStateOf(0L)
    var startTime by mutableStateOf(0L)

    val progressPercent: Float
        get() = if (sizeBytes > 0) (progressBytes * 100L / sizeBytes).toFloat() / 100f else 0f

    val progressMB: Float
        get() = progressBytes / 1000000f

    val sizeMB: Float
        get() = sizeBytes / 1000000f

    val speedMBPS: Float
        get() = (progressBytes / 1000f) / (System.currentTimeMillis() - startTime).toFloat()

    val error: Boolean
        get() = progressBytes == -1L

}