package codes.nh.itube.backend.player

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import codes.nh.itube.backend.library.Song
import codes.nh.itube.backend.utils.LOG


class PlayerState {

    init {
        LOG("init PlayerState")
    }

    var currentSong by mutableStateOf(null as Song?)
    var isPlaying by mutableStateOf(false)
    var position by mutableStateOf(0L)
    var duration by mutableStateOf(0L)
    var isShuffle by mutableStateOf(false)
    var isRepeat by mutableStateOf(false)

}