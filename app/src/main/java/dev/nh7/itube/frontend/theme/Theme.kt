package dev.nh7.itube.frontend.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import com.google.accompanist.systemuicontroller.rememberSystemUiController


@Composable
fun MainTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {

    val colors = if (darkTheme) {
        darkColorScheme(
            /*
            primary = YoutubeRed,
            onPrimary = YoutubeWhite,
            primaryContainer = YoutubeRed,
            secondary = YoutubeRed,
            onSecondary = YoutubeWhite,
            secondaryContainer = YoutubeRed,
            surface = YoutubeBlack
            */
        )
    } else {
        lightColorScheme(
            /*
            primary = YoutubeRed,
            onPrimary = YoutubeWhite,
            primaryContainer = YoutubeRed,
            secondary = YoutubeRed,
            onSecondary = YoutubeWhite,
            secondaryContainer = YoutubeRed,
            surface = YoutubeWhite
            */
        )
    }

    MaterialTheme(
        colorScheme = colors
    ) {

        val systemUiController = rememberSystemUiController()
        systemUiController.setSystemBarsColor(
            color = MaterialTheme.colorScheme.surface,
            darkIcons = !darkTheme
        )

        content()

    }

}