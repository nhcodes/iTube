package codes.nh.itube.frontend.components

import androidx.compose.animation.*
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import codes.nh.itube.R
import codes.nh.itube.frontend.modifiers.DraggableState
import codes.nh.itube.frontend.modifiers.draggableAfterLongClick

@Composable
fun DownloadButtonComponent(
    visible: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {

    val draggableState = remember { DraggableState() }
    val downloadText = stringResource(id = R.string.button_download)

    AnimatedVisibility(
        visible = visible,
        modifier = modifier.draggableAfterLongClick(state = draggableState),
        enter = fadeIn() + slideInVertically(initialOffsetY = { it / 2 }),
        exit = fadeOut() + slideOutVertically(targetOffsetY = { it / 2 })
    ) {

        ExtendedFloatingActionButton(
            icon = {
                Icon(
                    painter = painterResource(id = R.drawable.icon_download),
                    contentDescription = downloadText
                )
            },
            text = { Text(text = downloadText) },
            onClick = onClick
        )

    }

}