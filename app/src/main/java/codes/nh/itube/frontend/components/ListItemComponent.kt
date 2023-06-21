package codes.nh.itube.frontend.components

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@ExperimentalFoundationApi
@Composable
fun ClickableListItemComponent(
    modifier: Modifier = Modifier,
    imageContent: (@Composable () -> Unit)? = null,
    text: String,
    maxLines: Int = Integer.MAX_VALUE,
    onClick: () -> Unit,
    onLongClick: () -> Unit,
    height: Dp? = null
) {
    Surface(
        shape = CardDefaults.shape,
        modifier = modifier
            .fillMaxWidth()
            .then(if (height != null) Modifier.height(height) else Modifier.height(IntrinsicSize.Min))
    ) {
        ListItemContent(
            modifier = Modifier.combinedClickable(onClick = onClick, onLongClick = onLongClick),
            imageContent = imageContent,
            text = text,
            maxLines = maxLines
        )
    }
}

@ExperimentalMaterial3Api
@Composable
fun CheckableListItemComponent(
    modifier: Modifier = Modifier,
    imageContent: (@Composable () -> Unit)? = null,
    text: String,
    maxLines: Int = Integer.MAX_VALUE,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    height: Dp? = null
) {
    val elevation = if (checked) 64.dp else 0.dp
    Surface(
        shape = CardDefaults.shape,
        onCheckedChange = onCheckedChange,
        checked = checked,
        tonalElevation = elevation,
        modifier = modifier
            .fillMaxWidth()
            .then(if (height != null) Modifier.height(height) else Modifier.height(IntrinsicSize.Min))
    ) {
        ListItemContent(
            imageContent = imageContent,
            text = text,
            maxLines = maxLines
        )
    }
}

@Composable
private fun ListItemContent(
    modifier: Modifier = Modifier,
    imageContent: (@Composable () -> Unit)? = null,
    text: String,
    maxLines: Int = Integer.MAX_VALUE
) {
    Row(verticalAlignment = Alignment.CenterVertically, modifier = modifier) {

        if (imageContent != null) {
            Box(
                modifier = Modifier
                    .aspectRatio(1f)
                    .fillMaxHeight(),
                contentAlignment = Alignment.Center
            ) {
                imageContent()
            }
        }

        Text(
            text = text,
            maxLines = maxLines,
            style = MaterialTheme.typography.bodyLarge,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier
                .weight(1f)
                .padding(8.dp)
        )

    }
}