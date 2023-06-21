package codes.nh.itube.backend.permission

import android.Manifest
import android.os.Build
import codes.nh.itube.R

enum class AppPermission(
    val manifestName: String,
    val descriptionStringId: Int,
    val minVersion: Int?,
    val maxVersion: Int?
) {

    //https://developer.android.com/reference/android/Manifest.permission#INTERNET
    INTERNET(
        Manifest.permission.INTERNET,
        R.string.permission_description_internet,
        null,
        null
    ),

    //https://developer.android.com/reference/android/Manifest.permission#WRITE_EXTERNAL_STORAGE
    WRITE_EXTERNAL_STORAGE(
        Manifest.permission.WRITE_EXTERNAL_STORAGE,
        R.string.permission_description_write,
        null,
        Build.VERSION_CODES.P
    ),

    //https://developer.android.com/reference/android/Manifest.permission#READ_EXTERNAL_STORAGE
    READ_EXTERNAL_STORAGE(
        Manifest.permission.READ_EXTERNAL_STORAGE,
        R.string.permission_description_read,
        null,
        Build.VERSION_CODES.S_V2
    ),

    //https://developer.android.com/reference/android/Manifest.permission#READ_MEDIA_AUDIO
    READ_MEDIA_AUDIO(
        Manifest.permission.READ_MEDIA_AUDIO,
        R.string.permission_description_read,
        Build.VERSION_CODES.TIRAMISU,
        null
    ),

    //https://developer.android.com/reference/android/Manifest.permission#FOREGROUND_SERVICE
    FOREGROUND_SERVICE(
        Manifest.permission.FOREGROUND_SERVICE,
        R.string.permission_description_foreground,
        Build.VERSION_CODES.P,
        null
    ),

}