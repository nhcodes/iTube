package dev.nh7.itube.backend.permission

import android.content.IntentSender
import androidx.activity.result.IntentSenderRequest

class FilePermissionRequest(intentSender: IntentSender, val onResult: (Boolean) -> Unit) {

    val request = IntentSenderRequest.Builder(intentSender).build()

}