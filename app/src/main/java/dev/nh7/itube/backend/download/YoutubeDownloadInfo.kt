package dev.nh7.itube.backend.download

import android.net.Uri


class YoutubeDownloadInfo(
    val videoTitle: String,
    val videoUrl: Uri,
    val downloadUrl: Uri
)