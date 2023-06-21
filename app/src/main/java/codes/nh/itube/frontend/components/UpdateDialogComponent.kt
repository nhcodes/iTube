package codes.nh.itube.frontend.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import codes.nh.itube.R

@Composable
fun UpdateDialogComponent(
    title: String,
    version: String,
    description: String,
    onCancel: () -> Unit,
    onDownload: () -> Unit,
    onOpen: () -> Unit
) {

    DialogComponent(
        titleId = R.string.dialog_update_title,
        iconId = R.drawable.icon_update,
        buttons = listOf(
            DialogButton(
                textId = R.string.dialog_update_button_cancel,
                onClick = onCancel
            ),
            DialogButton(
                textId = R.string.dialog_update_button_download,
                onClick = onDownload
            )
        )
    ) {

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
        ) {
            Text(
                text = title,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                style = MaterialTheme.typography.titleMedium
            )
            Text(
                text = version,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            Text(text = description)
            TextButton(onClick = onOpen) {
                Text(text = stringResource(id = R.string.dialog_update_button_open))
            }
        }

    }

}