package codes.nh.itube.frontend.components

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.*
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import codes.nh.itube.R
import codes.nh.itube.backend.library.Playlist
import codes.nh.itube.backend.library.Song


@ExperimentalMaterial3Api
@ExperimentalFoundationApi
@Composable
fun CreatePlaylistDialogComponent(
    songs: List<Song>,
    onClickCreate: (name: String, playlist: List<String>) -> Unit,
    onDismiss: () -> Unit
) {

    val playlistName = remember { mutableStateOf("") }
    val selectedSongUris = remember { mutableStateListOf<String>() }

    DialogComponent(
        titleId = R.string.dialog_create_playlist_title,
        iconId = R.drawable.icon_create,
        buttons = listOf(
            DialogButton(
                textId = R.string.dialog_create_playlist_button_cancel,
                onClick = onDismiss
            ),
            DialogButton(
                textId = R.string.dialog_create_playlist_button_create,
                onClick = {
                    onClickCreate(playlistName.value, selectedSongUris.toList())
                },
                enabled = selectedSongUris.isNotEmpty() && playlistName.value.isNotBlank()
            )
        ),
        onDismiss = onDismiss
    ) {

        ModifyPlaylistComponent(
            playlistName = playlistName,
            selectedSongUris = selectedSongUris,
            allSongs = songs
        )

    }
}


@ExperimentalMaterial3Api
@ExperimentalFoundationApi
@Composable
fun EditPlaylistDialogComponent(
    playlistFile: Playlist,
    playlistSongUris: List<String>,
    songs: List<Song>,
    onClickDelete: () -> Unit,
    onClickEdit: (name: String, playlist: List<String>) -> Unit,
    onDismiss: () -> Unit
) {

    val playlistName = remember { mutableStateOf(playlistFile.name) }
    val selectedSongUris = remember { playlistSongUris.toMutableStateList() }

    DialogComponent(
        titleId = R.string.dialog_edit_playlist_title,
        iconId = R.drawable.icon_edit,
        buttons = listOf(
            DialogButton(
                textId = R.string.dialog_edit_playlist_button_delete,
                onClick = onClickDelete
            ),
            DialogButton(
                textId = R.string.dialog_edit_playlist_button_cancel,
                onClick = onDismiss
            ),
            DialogButton(
                textId = R.string.dialog_edit_playlist_button_save,
                onClick = {
                    onClickEdit(playlistName.value, selectedSongUris.toList())
                },
                enabled = selectedSongUris.isNotEmpty() && playlistName.value.isNotBlank()
            )
        ),
        onDismiss = onDismiss
    ) {

        ModifyPlaylistComponent(
            playlistName = playlistName,
            selectedSongUris = selectedSongUris,
            allSongs = songs
        )

    }
}

@ExperimentalMaterial3Api
@Composable
private fun ModifyPlaylistComponent(
    playlistName: MutableState<String>,
    selectedSongUris: SnapshotStateList<String>,
    allSongs: List<Song>
) {
    Column {

        OutlinedTextField(
            value = playlistName.value,
            onValueChange = { playlistName.value = it },
            label = {
                Text(text = stringResource(id = R.string.dialog_createedit_playlist_label_name))
            },
            singleLine = true,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp)
        )

        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = stringResource(
                    id = R.string.dialog_createedit_playlist_title_selected,
                    selectedSongUris.size
                ),
                modifier = Modifier.weight(1f)
            )
            TextButton(
                onClick = {
                    selectedSongUris.apply {
                        clear()
                        addAll(allSongs.map { it.contentUri.toString() })
                    }
                },
                modifier = Modifier.padding(4.dp)
            ) {
                Text(text = stringResource(id = R.string.dialog_createedit_playlist_button_select_all))
            }
        }

        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
        ) {
            items(allSongs) { song ->

                val songUri = song.contentUri.toString()
                val isSelected = selectedSongUris.any { it == songUri }

                CheckableListItemComponent(
                    text = song.fileName,
                    maxLines = 1,
                    checked = isSelected,
                    onCheckedChange = {
                        if (selectedSongUris.any { it == songUri })
                            selectedSongUris.remove(songUri)
                        else
                            selectedSongUris.add(songUri)
                    },
                    modifier = Modifier.padding(4.dp)
                )

            }
        }

    }
}