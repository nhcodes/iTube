package dev.nh7.itube.frontend.components

import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import dev.nh7.itube.R
import dev.nh7.itube.backend.permission.AppPermission
import dev.nh7.itube.backend.permission.FilePermissionRequest


/*
https://developer.android.com/training/permissions/requesting
*/
@Composable
fun AppPermissionHandler(
    missingPermission: AppPermission,
    onResult: (success: Boolean) -> Unit,
    onDeny: () -> Unit
) {

    val appPermissionRequestLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = onResult
    )

    val permissionName = remember(missingPermission) {
        missingPermission.manifestName.replace("android.permission.", "")
    }

    val permissionDescription =
        stringResource(id = missingPermission.descriptionStringId, permissionName)

    PermissionsDialogComponent(
        message = permissionDescription,
        onClickDeny = onDeny,
        onClickAllow = {
            appPermissionRequestLauncher.launch(missingPermission.manifestName)
        }
    )

}

/*
need to request permission to edit files that we didn't create ourselves
after app reinstall, we don't have the permission to edit previously downloaded files anymore
https://developer.android.com/training/data-storage/shared/media#update-other-apps-files
*/
@Composable
fun FilePermissionHandler(
    filePermissionRequest: FilePermissionRequest,
    onResult: (success: Boolean) -> Unit,
    onDeny: () -> Unit
) {

    val filePermissionRequestLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartIntentSenderForResult(),
        onResult = {
            val success = it.resultCode == ComponentActivity.RESULT_OK
            onResult(success)
        }
    )

    PermissionsDialogComponent(
        message = stringResource(id = R.string.dialog_file_permission_request),
        onClickDeny = onDeny,
        onClickAllow = {
            filePermissionRequestLauncher.launch(filePermissionRequest.request)
        }
    )

}

@Composable
private fun PermissionsDialogComponent(
    message: String,
    onClickAllow: () -> Unit,
    onClickDeny: () -> Unit
) {

    DialogComponent(
        titleId = R.string.dialog_permissions_title,
        iconId = R.drawable.icon_permissions,
        buttons = listOf(
            DialogButton(
                textId = R.string.dialog_permissions_button_deny,
                onClick = onClickDeny
            ),
            DialogButton(
                textId = R.string.dialog_permissions_button_allow,
                onClick = onClickAllow
            )
        )
    ) {

        Text(
            text = message,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )

    }
}