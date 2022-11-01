package dev.nh7.itube.frontend.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import dev.nh7.itube.R
import dev.nh7.itube.backend.library.Song


@ExperimentalMaterial3Api
@Composable
fun EditSongDialogComponent(
    song: Song,
    onClickEdit: (artist: String, title: String) -> Unit,
    onClickDelete: () -> Unit,
    onDismiss: () -> Unit
) {

    var artist by remember { mutableStateOf(song.artist ?: "") }
    var title by remember { mutableStateOf(song.title) }

    DialogComponent(
        titleId = R.string.dialog_edit_song_title,
        iconId = R.drawable.icon_edit,
        buttons = listOf(
            DialogButton(
                textId = R.string.dialog_edit_song_button_delete,
                onClick = {
                    onClickDelete()
                }
            ),
            DialogButton(
                textId = R.string.dialog_edit_song_button_save,
                onClick = {
                    onClickEdit(artist, title)
                }
            )
        ),
        onDismiss = onDismiss
    ) {

        Column {

            OutlinedTextField(
                value = artist,
                onValueChange = { value -> artist = value.replace(" - ", "") },
                label = { Text(text = stringResource(id = R.string.dialog_edit_song_label_artist)) },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.padding(8.dp))

            OutlinedTextField(
                value = title,
                onValueChange = { value -> title = value },
                label = { Text(text = stringResource(id = R.string.dialog_edit_song_label_title)) },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

        }

    }

}