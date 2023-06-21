package codes.nh.itube.frontend.components

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import codes.nh.itube.R
import codes.nh.itube.backend.library.Playlist
import codes.nh.itube.backend.library.Song


@ExperimentalFoundationApi
@ExperimentalMaterial3Api
@Composable
fun PlaylistsComponent(
    playlists: List<Playlist>,
    selectedPlaylist: Playlist?,
    playingSong: Song?,
    onClickPlaylist: (playlist: Playlist) -> Unit,
    onLongClickPlaylist: (playlist: Playlist) -> Unit,
    onClickCreate: () -> Unit,
    onClickSong: (song: Song) -> Unit,
    onLongClickSong: (song: Song) -> Unit,
    onClickShufflePlay: () -> Unit,
    onClickBack: () -> Unit
) {

    if (selectedPlaylist == null) {

        PlaylistListComponent(
            playlists = playlists,
            onClickPlaylist = onClickPlaylist,
            onLongClickPlaylist = onLongClickPlaylist,
            onClickCreate = onClickCreate
        )

    } else {

        BackHandler(onBack = onClickBack)

        Column {

            Text(
                text = selectedPlaylist.name,
                style = MaterialTheme.typography.titleSmall,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp)
            )

            SongsComponent(
                songs = selectedPlaylist.songs ?: emptyList(),
                playingSong = playingSong,
                onClickSong = onClickSong,
                onLongClickSong = onLongClickSong,
                onClickShufflePlay = onClickShufflePlay,
                modifier = Modifier.weight(1f)
            )

        }

    }
}

@ExperimentalMaterial3Api
@ExperimentalFoundationApi
@Composable
private fun PlaylistListComponent(
    playlists: List<Playlist>,
    onClickPlaylist: (playlist: Playlist) -> Unit,
    onLongClickPlaylist: (playlist: Playlist) -> Unit,
    onClickCreate: () -> Unit
) {
    Box {

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(vertical = 4.dp)
        ) {
            items(playlists) { playlist ->

                PlaylistElementComponent(
                    playlist = playlist,
                    onClick = { onClickPlaylist(playlist) },
                    onLongClick = { onLongClickPlaylist(playlist) },
                    modifier = Modifier.padding(4.dp)
                )

            }
        }

        FloatingActionButton(
            onClick = onClickCreate,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp)
        ) {
            Icon(
                painter = painterResource(id = R.drawable.icon_create),
                contentDescription = stringResource(id = R.string.playlists_button_create)
            )
        }

        if (playlists.isEmpty()) {

            Text(
                text = stringResource(id = R.string.playlists_empty),
                modifier = Modifier
                    .align(Alignment.Center)
                    .alpha(0.75f)
            )

        }

    }
}


@ExperimentalFoundationApi
@Composable
private fun PlaylistElementComponent(
    playlist: Playlist,
    onClick: () -> Unit,
    onLongClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    ClickableListItemComponent(
        modifier = modifier,
        text = playlist.name,
        maxLines = 1,
        onClick = onClick,
        onLongClick = onLongClick,
        height = 64.dp,
        imageContent = {
            Image(
                painter = painterResource(id = R.drawable.icon_playlist),
                colorFilter = ColorFilter.tint(color = MaterialTheme.colorScheme.primary),
                contentDescription = stringResource(id = R.string.playlists_cover),
                modifier = Modifier.size(32.dp)
            )
        }
    )
}