package dev.nh7.itube.backend.screen

import android.content.Context
import android.content.res.Configuration
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

class ScreenManager(context: Context) {

    //screen

    private var screenState by mutableStateOf(Screen.SEARCH)

    fun getScreen(): Screen {
        return screenState
    }

    fun openScreen(screen: Screen) {
        screenState = screen
    }

    //orientation

    private var orientationState by mutableStateOf(getCurrentOrientation(context))

    private fun getCurrentOrientation(context: Context): Int {
        return context.resources.configuration.orientation
    }

    fun isLandscapeOrientation(): Boolean {
        return orientationState == Configuration.ORIENTATION_LANDSCAPE
    }

    fun updateOrientation(orientation: Int) {
        orientationState = orientation
    }

}