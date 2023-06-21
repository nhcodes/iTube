package codes.nh.itube

import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.ViewModelProvider
import codes.nh.itube.backend.utils.LOG
import codes.nh.itube.frontend.components.MainComponent
import codes.nh.itube.frontend.components.currentBrowser
import codes.nh.itube.frontend.theme.MainTheme

class MainActivity : ComponentActivity() {

    private lateinit var viewModel: MainViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        LOG("onCreate MainActivity")

        installSplashScreen()

        viewModel = ViewModelProvider(this)[MainViewModel::class.java]

        setContent {

            MainTheme(darkTheme = viewModel.getTheme()) {
                MainComponent(viewModel = viewModel)
            }

            //lifecycle listener
            LifecycleListener(onChange = { event ->
                when (event) {
                    Lifecycle.Event.ON_CREATE -> {

                        if (viewModel.hasUpdatesEnabled()) {
                            viewModel.updateManager.checkForUpdates()
                        }
                        viewModel.loadSharedYoutubeUrlInBrowser(intent)

                    }
                    Lifecycle.Event.ON_START -> viewModel.playerManager.load(applicationContext)
                    Lifecycle.Event.ON_STOP -> viewModel.playerManager.unload()
                    Lifecycle.Event.ON_DESTROY -> currentBrowser = null
                    else -> {}
                }
            })

            //load after all permissions are granted
            val hasMissingPermission = viewModel.permissionManager.getMissingPermission() != null
            LaunchedEffect(hasMissingPermission) {
                if (hasMissingPermission) return@LaunchedEffect

                LOG("all permissions are granted")
                viewModel.libraryManager.loadLibrary()
                viewModel.libraryManager.loadPlaylists()
            }

            //finish listener
            val finish = viewModel.hasRequestedFinish
            LaunchedEffect(finish) {
                if (!finish) return@LaunchedEffect
                viewModel.hasRequestedFinish = false

                LOG("finish listener")
                finish()
            }

            //back handler
            var lastBackClick = remember { 0L }
            BackHandler {
                val time = System.currentTimeMillis()
                if (time - lastBackClick < 1000L) {
                    finish()
                } else {
                    lastBackClick = time
                    viewModel.showToast(R.string.toast_back_button_close)
                }
            }

        }
    }


    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        viewModel.loadSharedYoutubeUrlInBrowser(intent)
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        viewModel.screenManager.updateOrientation(newConfig.orientation)
    }

    @Composable
    private fun LifecycleListener(onChange: (Lifecycle.Event) -> Unit) {
        val lifecycleOwner = LocalLifecycleOwner.current
        DisposableEffect(lifecycleOwner) {

            val observer = LifecycleEventObserver { _, event -> onChange(event) }
            lifecycleOwner.lifecycle.addObserver(observer)

            onDispose {
                lifecycleOwner.lifecycle.removeObserver(observer)
            }

        }
    }

}