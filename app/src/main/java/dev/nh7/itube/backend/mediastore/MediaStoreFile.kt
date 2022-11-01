package dev.nh7.itube.backend.mediastore

import android.net.Uri

class MediaStoreFile(
    val uri: Uri,
    val name: String,
    val columnValues: Map<String, String>
)