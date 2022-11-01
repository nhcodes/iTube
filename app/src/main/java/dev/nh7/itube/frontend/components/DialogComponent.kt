package dev.nh7.itube.frontend.components

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource

class DialogButton(val textId: Int, val onClick: () -> Unit, val enabled: Boolean = true)

@Composable
fun DialogComponent(
    titleId: Int,
    iconId: Int,
    buttons: List<DialogButton> = emptyList(),
    onDismiss: () -> Unit = {},
    content: @Composable () -> Unit,
) {

    val dialogTitle = stringResource(id = titleId)

    AlertDialog(
        icon = {
            Icon(
                painter = painterResource(id = iconId),
                contentDescription = dialogTitle
            )
        },
        title = {
            Text(text = dialogTitle)
        },
        text = content,
        confirmButton = {

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {

                buttons.forEach { dialogButton ->

                    TextButton(
                        modifier = Modifier.weight(1f),
                        onClick = dialogButton.onClick,
                        enabled = dialogButton.enabled
                    ) {
                        Text(text = stringResource(id = dialogButton.textId))
                    }

                }

            }

        },
        onDismissRequest = onDismiss
    )
}