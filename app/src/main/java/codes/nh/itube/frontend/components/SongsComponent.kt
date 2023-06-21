package codes.nh.itube.frontend.components

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
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
import androidx.compose.ui.unit.dp
import codes.nh.itube.R
import codes.nh.itube.backend.library.Song


@ExperimentalMaterial3Api
@ExperimentalFoundationApi
@Composable
fun SongsComponent(
    songs: List<Song>,
    playingSong: Song?,
    onClickSong: (song: Song) -> Unit,
    onLongClickSong: (song: Song) -> Unit,
    onClickShufflePlay: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(modifier = modifier) {

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(vertical = 4.dp)
        ) {
            items(songs) { song ->
                val isPlaying = song.contentUri.toString() == playingSong?.contentUri.toString()
                SongComponent(
                    song = song,
                    isPlaying = isPlaying,
                    onClick = { onClickSong(song) },
                    onLongClick = { onLongClickSong(song) },
                    modifier = Modifier.padding(4.dp)
                )
            }
        }

        if (songs.isNotEmpty()) {

            FloatingActionButton(
                onClick = onClickShufflePlay,
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(16.dp)
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.icon_player_shuffle),
                    contentDescription = stringResource(id = R.string.songs_button_shuffleplay)
                )
            }

        } else {

            Text(
                text = stringResource(id = R.string.songs_empty),
                modifier = Modifier
                    .align(Alignment.Center)
                    .alpha(0.75f)
            )

        }

    }
}

@ExperimentalFoundationApi
@Composable
private fun SongComponent(
    song: Song,
    isPlaying: Boolean,
    onClick: () -> Unit,
    onLongClick: () -> Unit,
    modifier: Modifier = Modifier
) {

    val iconId = if (isPlaying) R.drawable.icon_player_play else R.drawable.icon_song

    ClickableListItemComponent(
        modifier = modifier,
        text = song.fileName,
        maxLines = 2,
        onClick = onClick,
        onLongClick = onLongClick,
        height = 64.dp,
        imageContent = {
            Image(
                painter = painterResource(id = iconId),
                colorFilter = ColorFilter.tint(color = MaterialTheme.colorScheme.primary),
                contentDescription = stringResource(id = R.string.songs_cover),
                modifier = Modifier.size(32.dp)
            )
        }
    )

}