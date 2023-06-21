package codes.nh.itube.frontend.components

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import codes.nh.itube.backend.browser.YoutubeBrowser
import codes.nh.itube.backend.download.YoutubeDownloadInfo

var currentBrowser: YoutubeBrowser? = null //todo improve

@Composable
fun BrowserComponent(
    onLoad: (browser: YoutubeBrowser, firstLoad: Boolean) -> Unit,
    onFindDownload: (downloadInfo: YoutubeDownloadInfo?) -> Unit
) {

    var progress by remember { mutableStateOf(100) }

    DisposableEffect(Unit) {
        currentBrowser?.onResume()
        onDispose {
            currentBrowser?.onPause()
        }
    }

    BackHandler(currentBrowser?.canGoBack() == true) {
        currentBrowser?.goBack()
    }

    Column(modifier = Modifier.fillMaxSize()) {

        if (progress < 100) {
            LinearProgressIndicator(
                progress = progress / 100f,
                modifier = Modifier.fillMaxWidth()
            )
        }

        AndroidView(
            factory = { context ->
                var first = false
                if (currentBrowser == null) {
                    currentBrowser = YoutubeBrowser(context)
                    first = true
                }
                val browser = currentBrowser!!
                onLoad(browser, first)
                browser.downloadListener = onFindDownload
                browser.progressListener = { newProgress ->
                    progress = newProgress
                }
                browser
            },
            modifier = Modifier.weight(1f)
        )

    }

}