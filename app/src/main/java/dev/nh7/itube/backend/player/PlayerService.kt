package dev.nh7.itube.backend.player

import android.app.PendingIntent
import android.content.Intent
import android.os.Build
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.session.MediaSession
import androidx.media3.session.MediaSessionService
import com.google.common.util.concurrent.Futures
import com.google.common.util.concurrent.ListenableFuture
import dev.nh7.itube.MainActivity
import dev.nh7.itube.backend.utils.LOG

class PlayerService : MediaSessionService(), MediaSession.Callback {

    private lateinit var mediaSession: MediaSession

    override fun onCreate() {
        LOG("onCreate PlayerService")
        super.onCreate()
        val player = ExoPlayer.Builder(this).build()
        mediaSession = MediaSession.Builder(this, player)
            .setCallback(this)
            .setSessionActivity(getActivityPendingIntent())
            .build()
    }

    override fun onDestroy() {
        LOG("onDestroy PlayerService")
        mediaSession.player.release()
        mediaSession.release()
        super.onDestroy()
    }

    override fun onGetSession(controllerInfo: MediaSession.ControllerInfo): MediaSession =
        mediaSession

    override fun onAddMediaItems(
        mediaSession: MediaSession,
        controller: MediaSession.ControllerInfo,
        mediaItems: MutableList<MediaItem>
    ): ListenableFuture<MutableList<MediaItem>> {
        val updatedMediaItems =
            mediaItems.map { it.buildUpon().setUri(it.mediaId).build() }.toMutableList()
        return Futures.immediateFuture(updatedMediaItems)
    }

    private fun getActivityPendingIntent(): PendingIntent {
        val activityIntent = Intent(this, MainActivity::class.java)
        val flags =
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) PendingIntent.FLAG_IMMUTABLE else 0
        return PendingIntent.getActivity(this, 111, activityIntent, flags)
    }
}