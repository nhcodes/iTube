package dev.nh7.itube.frontend.components

import android.text.format.DateUtils
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import dev.nh7.itube.R
import dev.nh7.itube.backend.player.PlayerAction
import dev.nh7.itube.backend.player.PlayerState


@Composable
fun PlayerComponent(
    state: PlayerState,
    onClick: (action: PlayerAction, args: Any?) -> Unit
) {

    val songTitle = state.currentSong?.title
    val songArtist = state.currentSong?.artist

    val positionString = remember(state.position) {
        DateUtils.formatElapsedTime(state.position / 1000L)
    }
    val durationString = remember(state.duration) {
        DateUtils.formatElapsedTime(state.duration / 1000L)
    }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {

        Spacer(modifier = Modifier.weight(1f))

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {

            Text(
                text = songTitle ?: "",
                maxLines = if (songArtist == null) 2 else 1,
                style = MaterialTheme.typography.headlineMedium,
                overflow = TextOverflow.Ellipsis,
                textAlign = TextAlign.Center
            )

            if (songArtist != null) {

                Text(
                    text = songArtist,
                    maxLines = 1,
                    style = MaterialTheme.typography.headlineSmall,
                    overflow = TextOverflow.Ellipsis,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.alpha(0.75f)
                )

            }

        }

        Spacer(modifier = Modifier.weight(1f))

        Column(modifier = Modifier.padding(16.dp)) {

            Slider(
                value = state.position.toFloat(),
                valueRange = 0f..state.duration.toFloat(),
                onValueChange = { onClick(PlayerAction.SEEK, it.toLong()) }
            )

            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp)
            ) {

                Text(text = positionString)
                Text(text = durationString)

            }

        }

        Spacer(modifier = Modifier.weight(1f))

        Row(
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {

            IconButton(onClick = { onClick(PlayerAction.REPEAT, null) }) {
                Icon(
                    painter = painterResource(id = R.drawable.icon_player_repeat),
                    contentDescription = stringResource(id = R.string.player_repeat),
                    modifier = Modifier.alpha(if (state.isRepeat) 1f else 0.5f)
                )
            }

            IconButton(onClick = { onClick(PlayerAction.PREVIOUS, null) }) {
                Icon(
                    painter = painterResource(id = R.drawable.icon_player_previous),
                    contentDescription = stringResource(id = R.string.player_previous)
                )
            }

            FloatingActionButton(onClick = { onClick(PlayerAction.PLAY_PAUSE, null) }) {
                val drawable =
                    if (state.isPlaying) R.drawable.icon_player_pause else R.drawable.icon_player_play
                Icon(
                    painter = painterResource(id = drawable),
                    contentDescription = stringResource(id = R.string.player_playpause)
                )
            }

            IconButton(onClick = { onClick(PlayerAction.NEXT, null) }) {
                Icon(
                    painter = painterResource(id = R.drawable.icon_player_next),
                    contentDescription = stringResource(id = R.string.player_next)
                )
            }

            IconButton(onClick = { onClick(PlayerAction.SHUFFLE, null) }) {
                Icon(
                    painter = painterResource(id = R.drawable.icon_player_shuffle),
                    contentDescription = stringResource(id = R.string.player_shuffle),
                    modifier = Modifier.alpha(if (state.isShuffle) 1f else 0.5f)
                )
            }

        }

        Spacer(modifier = Modifier.weight(1f))

    }

}