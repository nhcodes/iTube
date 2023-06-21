package codes.nh.itube

import android.app.Application
import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.net.Uri
import android.widget.Toast
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import codes.nh.itube.backend.download.YoutubeDownloadInfo
import codes.nh.itube.backend.download.YoutubeDownloadManager
import codes.nh.itube.backend.library.LibraryManager
import codes.nh.itube.backend.permission.AppPermissionManager
import codes.nh.itube.backend.player.PlayerManager
import codes.nh.itube.backend.screen.Screen
import codes.nh.itube.backend.screen.ScreenManager
import codes.nh.itube.backend.setting.Setting
import codes.nh.itube.backend.setting.SettingManager
import codes.nh.itube.backend.update.UpdateManager
import codes.nh.itube.backend.utils.LOG
import codes.nh.itube.backend.utils.sync
import codes.nh.itube.frontend.components.currentBrowser

class MainViewModel(application: Application) : AndroidViewModel(application) {

    init {
        LOG("init MainViewModel")
    }

    private fun getAppContext(): Context {
        return getApplication<Application>().applicationContext
    }

    val screenManager = ScreenManager(getAppContext())

    val libraryManager = LibraryManager(getAppContext())

    val playerManager = PlayerManager()

    val settingManager = SettingManager(getAppContext())

    val permissionManager = AppPermissionManager(getAppContext())

    // download info ==========================================

    private var downloadInfoState by mutableStateOf(null as YoutubeDownloadInfo?)

    private val autoDownloadUrls = mutableListOf<String>()

    fun hasFoundDownloadInfo(): Boolean {
        return downloadInfoState != null
    }

    fun updateDownloadInfo(downloadInfo: YoutubeDownloadInfo?) {
        if (downloadInfoState == downloadInfo) return

        LOG("url is ${downloadInfo?.downloadUrl}")
        downloadInfoState = downloadInfo

        if (
            downloadInfo != null &&
            settingManager.getSetting(Setting.AUTO_DOWNLOAD).toBoolean() &&
            !autoDownloadUrls.contains(downloadInfo.videoUrl.toString())
        ) {
            autoDownloadUrls.add(downloadInfo.videoUrl.toString())
            startDownload()
        }
    }

    // download ==========================================

    val downloadManager = YoutubeDownloadManager(libraryManager.songManager)

    fun startDownload() {
        val downloadInfo = downloadInfoState ?: return
        val bufferBytes = settingManager.getSetting(Setting.DOWNLOAD_BUFFER).toInt() * 1000L
        downloadManager.start(downloadInfo, bufferBytes) { song ->

            libraryManager.addToSongs(song)

            sync {
                if (settingManager.getSetting(Setting.PLAY_SONG_AFTER_DOWNLOAD).toBoolean()) {
                    playerManager.play(libraryManager.getSongs(), song)
                    screenManager.openScreen(Screen.PLAYER)
                }
            }

            //insert uri & videoId into database for later usage (e.g. loading thumbnails from yt)
            val videoId = downloadInfo.videoUrl.getQueryParameter("v")
            if (videoId != null) {
                libraryManager.databaseManager.writeLine("${song.contentUri} $videoId")
            }

        }

    }

    // update ==========================================

    val updateManager = UpdateManager(getAppContext())

    fun hasUpdatesEnabled(): Boolean {
        return settingManager.getSetting(Setting.UPDATES).toBoolean()
    }

    // browser ==========================================

    fun loadUrl(url: String) {
        currentBrowser?.loadUrl(url)
    }

    fun isResetBrowserEnabled(): Boolean {
        return settingManager.getSetting(Setting.RESET_BROWSER).toBoolean()
    }

    // theme ==========================================

    //check isSystemInDarkMode
    private fun checkDarkMode(): Boolean {
        val uiMode = getAppContext().resources.configuration.uiMode
        return (uiMode and Configuration.UI_MODE_NIGHT_MASK) == Configuration.UI_MODE_NIGHT_YES
    }

    fun getTheme(): Boolean {
        return when (settingManager.getSetting(Setting.THEME)) {
            "Dark" -> true
            "Light" -> false
            else -> checkDarkMode()
        }
    }

    // other ==========================================

    var hasRequestedFinish by mutableStateOf(false)

    fun showToast(stringId: Int) {
        showToast(getAppContext().getString(stringId))
    }

    fun showToast(text: String) {
        Toast.makeText(getAppContext(), text, Toast.LENGTH_SHORT).show()
    }

    fun openUrlInStandardBrowser(url: String) {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        getAppContext().startActivity(intent)
    }

    fun loadSharedYoutubeUrlInBrowser(intent: Intent?) {
        val youtubeUrl = intent?.getCharSequenceExtra(Intent.EXTRA_TEXT)?.toString() ?: return
        loadUrl(youtubeUrl)
        screenManager.openScreen(Screen.SEARCH)
    }

    fun getGithubUrl(): String {
        val appName = getAppContext().getString(R.string.app_name)
        val accountName = getAppContext().getString(R.string.github_account)
        return "https://github.com/$accountName/$appName"
    }

}