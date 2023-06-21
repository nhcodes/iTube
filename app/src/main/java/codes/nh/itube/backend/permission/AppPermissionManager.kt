package codes.nh.itube.backend.permission

import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.core.content.ContextCompat

class AppPermissionManager(private val context: Context) {

    private val neededPermissions = loadNeededPermissions()

    private var missingPermissionState by mutableStateOf(getFirstMissingPermission())

    fun getMissingPermission(): AppPermission? {
        return missingPermissionState
    }

    fun updateMissingPermission() {
        missingPermissionState = getFirstMissingPermission()
    }

    //

    private fun loadNeededPermissions(): List<AppPermission> {
        val deviceVersion = Build.VERSION.SDK_INT
        val permissions = AppPermission.values().filter {
            (it.minVersion == null || deviceVersion >= it.minVersion)
                    && (it.maxVersion == null || deviceVersion <= it.maxVersion)
        }
        return permissions
    }

    private fun getFirstMissingPermission(): AppPermission? {
        return neededPermissions.firstOrNull { !isPermissionGranted(it.manifestName) }
    }

    private fun isPermissionGranted(permission: String): Boolean {
        val status = ContextCompat.checkSelfPermission(context, permission)
        return status == PackageManager.PERMISSION_GRANTED
    }

}