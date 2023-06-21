package codes.nh.itube.frontend.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import codes.nh.itube.R
import codes.nh.itube.backend.download.YoutubeDownloadState

@Composable
fun DownloadDialogComponent(
    downloadState: YoutubeDownloadState,
    onClickCancel: () -> Unit
) {

    val fileName = downloadState.fileName
    val progressPercent = downloadState.progressPercent
    val progressMB = downloadState.progressMB
    val sizeMB = downloadState.sizeMB
    val speedMBPS = downloadState.speedMBPS
    val error = downloadState.error

    DialogComponent(
        titleId = R.string.dialog_download_title,
        iconId = R.drawable.icon_download,
        buttons = listOf(
            DialogButton(textId = R.string.dialog_download_button_cancel, onClick = onClickCancel)
        )
    ) {

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxWidth()
        ) {

            if (error) {

                Text(text = fileName, maxLines = 1, overflow = TextOverflow.Ellipsis)
                Text(
                    text = stringResource(id = R.string.dialog_download_error),
                    modifier = Modifier.padding(vertical = 8.dp)
                )

            } else {

                Text(text = fileName, maxLines = 1, overflow = TextOverflow.Ellipsis)
                LinearProgressIndicator(
                    progress = progressPercent,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)
                )
                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(text = stringResource(id = R.string.dialog_download_progress, progressMB))
                    Text(text = stringResource(id = R.string.dialog_download_speed, speedMBPS))
                    Text(text = stringResource(id = R.string.dialog_download_progress, sizeMB))
                }

            }

        }

    }

}