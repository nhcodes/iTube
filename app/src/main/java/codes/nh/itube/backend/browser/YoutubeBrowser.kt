package codes.nh.itube.backend.browser

import android.content.Context
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams
import android.webkit.*
import androidx.annotation.RequiresApi
import codes.nh.itube.backend.download.YoutubeDownloadInfo
import codes.nh.itube.backend.utils.LOG

class YoutubeBrowser(context: Context) : WebView(context) {

    init {
        LOG("init YoutubeBrowser")
        layoutParams = LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT
        )
        setBackgroundColor(Color.TRANSPARENT)
        settings.javaScriptEnabled = true
        webViewClient = Client()
        webChromeClient = ChromeClient()
    }

    var progressListener: ((Int) -> Unit)? = null

    var downloadListener: ((YoutubeDownloadInfo?) -> Unit)? = null

    private var videoTitle: String? = null
    private var videoUrl: Uri? = null
    private var downloadUrl: Uri? = null

    fun checkNewDownload() {
        val finalVideoTitle = videoTitle
        val finalVideoUrl = videoUrl
        val finalDownloadUrl = downloadUrl
        val downloadInfo =
            if (finalVideoTitle == null || finalVideoUrl == null || finalDownloadUrl == null)
                null
            else
                YoutubeDownloadInfo(finalVideoTitle, finalVideoUrl, finalDownloadUrl)
        downloadListener?.invoke(downloadInfo)
    }

    fun reset() {
        clearCache(true)
        CookieManager.getInstance().removeAllCookies(null)
    }

    class Client : WebViewClient() {

        init {
            LOG("init YoutubeBrowser.Client")
        }

        @Deprecated("Deprecated in API level 24")
        override fun shouldOverrideUrlLoading(view: WebView?, url: String?): Boolean {
            val uri = if (url != null) Uri.parse(url) else null
            return !checkIfYoutube(uri)
        }

        @RequiresApi(Build.VERSION_CODES.N)
        override fun shouldOverrideUrlLoading(
            view: WebView?,
            request: WebResourceRequest?
        ): Boolean {
            val uri = request?.url
            return !checkIfYoutube(uri)
        }

        private fun checkIfYoutube(url: Uri?): Boolean {
            return url?.host?.endsWith("youtube.com") == true
        }

        override fun shouldInterceptRequest(
            view: WebView?,
            request: WebResourceRequest?
        ): WebResourceResponse? {

            val browser = view as YoutubeBrowser

            if (browser.downloadUrl != null) {
                return null
            }

            val url = request?.url
            val path = url?.path
            val mime = if (url?.isHierarchical == true) url.getQueryParameter("mime") else null
            if (path == "/videoplayback" && (mime == "audio/webm" || mime == "audio/mp4")) {

                browser.downloadUrl = url
                browser.checkNewDownload()

            }

            return null
        }

        override fun doUpdateVisitedHistory(view: WebView?, url: String?, isReload: Boolean) {
            super.doUpdateVisitedHistory(view, url, isReload)

            val browser = view as YoutubeBrowser
            browser.videoTitle = null
            browser.videoUrl = Uri.parse(url)
            browser.downloadUrl = null
            browser.checkNewDownload()
        }

        @RequiresApi(Build.VERSION_CODES.M)
        override fun onReceivedError(
            view: WebView?,
            request: WebResourceRequest?,
            error: WebResourceError?
        ) {
            super.onReceivedError(view, request, error)
            LOG("onReceivedError ${error?.errorCode} ${error?.description}")
        }

    }

    class ChromeClient : WebChromeClient() {

        init {
            LOG("init YoutubeBrowser.ChromeClient")
        }

        override fun onReceivedTitle(view: WebView?, title: String?) {
            super.onReceivedTitle(view, title)

            title ?: return

            val titleSuffix = "- YouTube"

            val updatedTitle =
                if (title.endsWith(titleSuffix))
                    title.substring(0, title.length - titleSuffix.length)
                else title

            if (updatedTitle.isBlank() || updatedTitle.removePrefix("Home").isBlank()) {
                return
            }

            val browser = view as YoutubeBrowser
            browser.videoTitle = updatedTitle.removeSuffix(" ")
            browser.checkNewDownload()
        }

        override fun onProgressChanged(view: WebView?, newProgress: Int) {
            super.onProgressChanged(view, newProgress)
            val browser = view as YoutubeBrowser
            browser.progressListener?.invoke(newProgress)
        }

    }

}