package codes.nh.itube.backend.player

import android.content.ComponentName
import android.content.Context
import android.os.Bundle
import androidx.media3.common.C
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import androidx.media3.common.Player
import androidx.media3.session.MediaController
import androidx.media3.session.SessionToken
import com.google.common.util.concurrent.ListenableFuture
import com.google.common.util.concurrent.MoreExecutors
import codes.nh.itube.backend.library.Song
import codes.nh.itube.backend.utils.LOG
import org.json.JSONObject
import java.util.*
import kotlin.concurrent.fixedRateTimer

class PlayerManager {

    private val playerState = PlayerState()

    fun getPlayerState(): PlayerState {
        return playerState
    }

    private lateinit var player: MediaController

    private lateinit var playerFuture: ListenableFuture<MediaController>

    private lateinit var positionIncrementTimer: Timer

    fun load(context: Context) {
        LOG("loadPlayer")

        val componentName = ComponentName(context, PlayerService::class.java)
        val sessionToken = SessionToken(context, componentName)
        playerFuture = MediaController.Builder(context, sessionToken).buildAsync()
        playerFuture.addListener(
            {
                player = playerFuture.get()
                init(player)
            },
            MoreExecutors.directExecutor()
        )

        val period = 1000L
        positionIncrementTimer = fixedRateTimer(period = period) {
            if (playerState.isPlaying) {
                playerState.position += period
            }
        }
    }

    fun unload() {
        LOG("unloadPlayer")
        positionIncrementTimer.cancel()
        MediaController.releaseFuture(playerFuture)
    }

    private fun init(player: Player) {
        player.addListener(object : Player.Listener {

            override fun onMediaItemTransition(mediaItem: MediaItem?, reason: Int) {
                super.onMediaItemTransition(mediaItem, reason)
                playerState.currentSong = getSongFromMediaItem(mediaItem)
            }

            override fun onIsPlayingChanged(playing: Boolean) {
                super.onIsPlayingChanged(playing)
                playerState.isPlaying = playing
            }

            override fun onPositionDiscontinuity(
                oldPosition: Player.PositionInfo,
                newPosition: Player.PositionInfo,
                reason: Int
            ) {
                super.onPositionDiscontinuity(oldPosition, newPosition, reason)
                playerState.position = player.currentPosition //newPosition.positionMs buggy
            }

            override fun onPlaybackStateChanged(playbackState: Int) {
                super.onPlaybackStateChanged(playbackState)
                val duration = player.duration.takeUnless { it == C.TIME_UNSET } ?: 0L
                playerState.duration = duration
            }

            override fun onShuffleModeEnabledChanged(shuffleModeEnabled: Boolean) {
                super.onShuffleModeEnabledChanged(shuffleModeEnabled)
                playerState.isShuffle = shuffleModeEnabled
            }

            override fun onRepeatModeChanged(repeatMode: Int) {
                super.onRepeatModeChanged(repeatMode)
                playerState.isRepeat = repeatMode == Player.REPEAT_MODE_ALL
            }

        })

        player.repeatMode = Player.REPEAT_MODE_ALL

        playerState.currentSong = getSongFromMediaItem(player.currentMediaItem)
        playerState.isPlaying = player.isPlaying
        playerState.position = player.currentPosition
        playerState.duration = player.duration.takeUnless { it == C.TIME_UNSET } ?: 0L
        playerState.isShuffle = player.shuffleModeEnabled
        playerState.isRepeat = player.repeatMode == Player.REPEAT_MODE_ALL
    }

    fun play(
        songList: List<Song>,
        song: Song? = null,
        shuffle: Boolean? = null
    ) {

        if (shuffle != null) {
            player.shuffleModeEnabled = shuffle
        }

        val mediaItems = getMediaItemsFromSongList(songList)
        player.setMediaItems(mediaItems)

        if (song != null) {
            val songIndex =
                mediaItems.indexOfFirst { item -> item.mediaId == song.contentUri.toString() }
            if (songIndex != -1) {
                player.seekTo(songIndex, 0L)
            }
        }

        player.prepare()
        player.play()

    }

    fun handlePlayerAction(action: PlayerAction, args: Any?) {
        when (action) {
            PlayerAction.PREVIOUS -> {
                player.seekToPreviousMediaItem()
            }
            PlayerAction.NEXT -> {
                player.seekToNextMediaItem()
            }
            PlayerAction.PLAY_PAUSE -> {
                if (player.isPlaying) {
                    player.pause()
                } else {
                    player.play()
                }
            }
            PlayerAction.SHUFFLE -> {
                player.shuffleModeEnabled = !player.shuffleModeEnabled
            }
            PlayerAction.REPEAT -> {
                player.repeatMode =
                    if (player.repeatMode == Player.REPEAT_MODE_ALL) Player.REPEAT_MODE_OFF
                    else Player.REPEAT_MODE_ALL
            }
            PlayerAction.SEEK -> {
                val position = args as Long
                player.seekTo(position)
            }
        }
    }

    private fun getSongFromMediaItem(mediaItem: MediaItem?): Song? {
        val songJson = mediaItem?.mediaMetadata?.extras?.getString("song") ?: return null
        return Song.fromJson(JSONObject(songJson))
    }

    private fun getMediaItemFromSong(song: Song): MediaItem {
        val bundle = Bundle()
        bundle.putString("song", song.toJson().toString())
        val metadata = MediaMetadata.Builder()
            .setArtist(song.artist)
            .setTitle(song.title)
            .setExtras(bundle)
            .build()
        return MediaItem.Builder()
            .setUri(song.contentUri)
            .setMediaId(song.contentUri.toString())
            .setMediaMetadata(metadata)
            .build()
    }

    private fun getMediaItemsFromSongList(songList: List<Song>): List<MediaItem> {
        return songList.map { song ->
            getMediaItemFromSong(song)
        }
    }

}