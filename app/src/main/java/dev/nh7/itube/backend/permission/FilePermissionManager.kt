package dev.nh7.itube.backend.permission

import android.app.RecoverableSecurityException
import android.content.IntentSender
import android.os.Build
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

class FilePermissionManager {

    private var requestState by mutableStateOf(null as FilePermissionRequest?)

    fun getRequest(): FilePermissionRequest? {
        return requestState
    }

    fun setRequest(request: FilePermissionRequest) {
        requestState = request
    }

    fun clearRequest() {
        requestState = null
    }

    //

    //https://developer.android.com/training/data-storage/shared/media#update-other-apps-files
    fun handleSecurityException(f: () -> Unit, onSecurityException: (IntentSender) -> Unit) {
        try {
            f()
        } catch (securityException: SecurityException) {

            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
                throw RuntimeException(securityException.message, securityException)
            }

            val recoverableSecurityException = securityException as? RecoverableSecurityException
                ?: throw RuntimeException(securityException.message, securityException)

            val intentSender = recoverableSecurityException.userAction.actionIntent.intentSender
            onSecurityException(intentSender)
        }
    }
}