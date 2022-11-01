package dev.nh7.itube.frontend.components

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.google.accompanist.pager.ExperimentalPagerApi
import dev.nh7.itube.MainViewModel
import dev.nh7.itube.R
import dev.nh7.itube.backend.library.Playlist
import dev.nh7.itube.backend.screen.Screen


@OptIn(
    ExperimentalMaterial3Api::class,
    ExperimentalFoundationApi::class,
    ExperimentalPagerApi::class
)
@Composable
fun MainComponent(viewModel: MainViewModel) {

    val screenManager = viewModel.screenManager
    val libraryManager = viewModel.libraryManager
    val playerManager = viewModel.playerManager
    val downloadManager = viewModel.downloadManager
    val updateManager = viewModel.updateManager
    val settingManager = viewModel.settingManager
    val appPermissionManager = viewModel.permissionManager
    val filePermissionManager = viewModel.libraryManager.filePermissionManager

    Scaffold(

        topBar = {
            if (!screenManager.isLandscapeOrientation()) {
                TopBarComponent(onClickTitleText = {
                    viewModel.openUrlInStandardBrowser(viewModel.getGithubUrl())
                })
            }
        },

        bottomBar = {
            if (!screenManager.isLandscapeOrientation()) {
                BottomNavigationComponent(
                    selectedScreen = screenManager.getScreen(),
                    onClick = { screen -> screenManager.openScreen(screen) }
                )
            }
        },

        ) { padding ->
        Box(modifier = Modifier.padding(padding)) {

            Row(modifier = Modifier.fillMaxSize()) {

                if (screenManager.isLandscapeOrientation()) {
                    SideNavigationComponent(
                        selectedScreen = screenManager.getScreen(),
                        onClick = { screen -> screenManager.openScreen(screen) },
                        onClickTitleText = {
                            viewModel.openUrlInStandardBrowser(viewModel.getGithubUrl())
                        }
                    )
                }

                //screens

                Crossfade(
                    targetState = screenManager.getScreen(),
                    modifier = Modifier.weight(1f)
                ) { screen ->

                    when (screen) {

                        Screen.SEARCH -> {
                            Box {

                                BrowserComponent(
                                    onLoad = { browser, isFirst ->
                                        if (isFirst) {
                                            browser.loadUrl("https://youtube.com")
                                        }
                                        if (viewModel.isResetBrowserEnabled()) {
                                            browser.reset()
                                        }
                                    },
                                    onFindDownload = { downloadInfo ->
                                        viewModel.updateDownloadInfo(downloadInfo = downloadInfo)
                                    }
                                )

                                DownloadButtonComponent(
                                    visible = viewModel.hasFoundDownloadInfo(),
                                    onClick = {
                                        viewModel.startDownload()
                                    },
                                    modifier = Modifier
                                        .padding(16.dp)
                                        .align(Alignment.BottomCenter)
                                )

                            }
                        }

                        Screen.LIBRARY -> {

                            val tabs = listOf(

                                TabContent(R.string.songs_tab_title) {
                                    SongsComponent(
                                        songs = libraryManager.getSongs(),
                                        playingSong = playerManager.getPlayerState().currentSong,
                                        onClickSong = { song ->
                                            playerManager.play(
                                                songList = libraryManager.getSongs(),
                                                song = song,
                                                shuffle = false
                                            )
                                            screenManager.openScreen(Screen.PLAYER)
                                        },
                                        onLongClickSong = { song ->
                                            libraryManager.openEditSongDialog(song)
                                        },
                                        onClickShufflePlay = {
                                            playerManager.play(
                                                songList = libraryManager.getSongs(),
                                                shuffle = true
                                            )
                                            screenManager.openScreen(Screen.PLAYER)
                                        }
                                    )
                                },

                                TabContent(R.string.playlists_tab_title) {

                                    var selectedPlaylistState by remember { mutableStateOf(null as Playlist?) }

                                    Crossfade(targetState = selectedPlaylistState) { selectedPlaylist ->

                                        PlaylistsComponent(
                                            playlists = libraryManager.getPlaylists(),
                                            selectedPlaylist = selectedPlaylist,
                                            playingSong = playerManager.getPlayerState().currentSong,
                                            onClickPlaylist = { playlistFile ->
                                                libraryManager.loadPlaylistSongs(playlistFile) {
                                                    playlistFile.songs = it
                                                    selectedPlaylistState = playlistFile
                                                }
                                            },
                                            onLongClickPlaylist = { playlistFile ->
                                                libraryManager.openEditPlaylistDialog(playlistFile)
                                            },
                                            onClickCreate = {
                                                libraryManager.openCreatePlaylistDialog()
                                            },
                                            onClickSong = { song ->
                                                playerManager.play(
                                                    songList = selectedPlaylist?.songs
                                                        ?: emptyList(),
                                                    song = song,
                                                    shuffle = false
                                                )
                                                screenManager.openScreen(Screen.PLAYER)
                                            },
                                            onLongClickSong = {
                                                val playlist =
                                                    selectedPlaylist ?: return@PlaylistsComponent
                                                selectedPlaylistState = null
                                                libraryManager.openEditPlaylistDialog(playlist)
                                            },
                                            onClickShufflePlay = {
                                                playerManager.play(
                                                    songList = selectedPlaylist?.songs
                                                        ?: emptyList(),
                                                    shuffle = true
                                                )
                                                screenManager.openScreen(Screen.PLAYER)
                                            },
                                            onClickBack = {
                                                selectedPlaylistState = null
                                            }
                                        )
                                    }
                                }

                            )

                            TabsComponent(tabs = tabs)

                        }

                        Screen.PLAYER -> {

                            PlayerComponent(
                                state = playerManager.getPlayerState(),
                                onClick = { action, args ->
                                    playerManager.handlePlayerAction(action, args)
                                }
                            )

                        }

                        Screen.SETTINGS -> {

                            SettingsComponent(
                                settings = settingManager.getSettings(),
                                onSettingChange = { key, value ->
                                    settingManager.setSetting(key, value)
                                }
                            )

                        }

                    }

                }

            }

            //dialogs

            val update = updateManager.isUpdateAvailable()
            val missingPermission = appPermissionManager.getMissingPermission()
            val filePermissionRequest = filePermissionManager.getRequest()
            val downloadState = downloadManager.getDownloadState()
            val editSong = libraryManager.getEditSong()
            val editPlaylist = libraryManager.getEditPlaylist()
            val createPlaylistDialogOpen = libraryManager.isCreatePlaylistDialogOpen()
            when {

                update != null -> {
                    UpdateDialogComponent(
                        title = update.title,
                        version = update.version,
                        description = update.description,
                        onCancel = {
                            updateManager.hideUpdate()
                        },
                        onDownload = {
                            updateManager.enqueueUpdateDownload(update)
                            updateManager.hideUpdate()
                            viewModel.showToast(R.string.toast_update_start_download)
                        },
                        onOpen = {
                            viewModel.openUrlInStandardBrowser(update.githubUrl)
                        }
                    )
                }

                missingPermission != null -> {
                    AppPermissionHandler(
                        missingPermission = missingPermission,
                        onResult = { appPermissionManager.updateMissingPermission() },
                        onDeny = {
                            viewModel.showToast(R.string.toast_app_permission_request_denied)
                            viewModel.hasRequestedFinish = true
                        }
                    )
                }

                filePermissionRequest != null -> {
                    FilePermissionHandler(
                        filePermissionRequest = filePermissionRequest,
                        onResult = { success ->
                            val request =
                                filePermissionManager.getRequest() ?: return@FilePermissionHandler
                            request.onResult(success)
                            filePermissionManager.clearRequest()
                        },
                        onDeny = {
                            viewModel.showToast(R.string.toast_file_permission_request_denied)
                            filePermissionManager.clearRequest()
                        }
                    )
                }

                downloadState.active -> {
                    DownloadDialogComponent(
                        downloadState = downloadState,
                        onClickCancel = {
                            downloadManager.cancel()
                        }
                    )
                }

                editSong != null -> {
                    EditSongDialogComponent(
                        song = editSong,
                        onClickEdit = { artist, title ->
                            val newFileName =
                                if (artist.isBlank()) title
                                else "$artist - $title"
                            libraryManager.renameSong(song = editSong, newFileName = newFileName)
                            libraryManager.closeEditSongDialog()
                        },
                        onClickDelete = {
                            libraryManager.deleteSong(song = editSong)
                            libraryManager.closeEditSongDialog()
                        },
                        onDismiss = {
                            libraryManager.closeEditSongDialog()
                        }
                    )
                }

                editPlaylist != null -> {
                    val songContentUris = editPlaylist.songs?.map { it.contentUri.toString() }
                    EditPlaylistDialogComponent(
                        playlistFile = editPlaylist,
                        playlistSongUris = songContentUris ?: emptyList(),
                        songs = libraryManager.getSongs(),
                        onClickDelete = {
                            libraryManager.deletePlaylist(editPlaylist)
                            libraryManager.closeEditPlaylistDialog()
                        },
                        onClickEdit = { name, playlist ->
                            //todo edit instead of create
                            libraryManager.createPlaylist(name, playlist)
                            libraryManager.closeEditPlaylistDialog()
                        },
                        onDismiss = {
                            libraryManager.closeEditPlaylistDialog()
                        }
                    )
                }

                createPlaylistDialogOpen -> {
                    CreatePlaylistDialogComponent(
                        songs = libraryManager.getSongs(),
                        onClickCreate = { name, playlist ->
                            libraryManager.createPlaylist(name, playlist)
                            libraryManager.closeCreatePlaylistDialog()
                        },
                        onDismiss = {
                            libraryManager.closeCreatePlaylistDialog()
                        }
                    )
                }

            }

        }
    }
}