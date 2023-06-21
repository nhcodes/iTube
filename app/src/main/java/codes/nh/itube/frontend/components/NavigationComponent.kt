package codes.nh.itube.frontend.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import codes.nh.itube.backend.screen.Screen


@Composable
fun BottomNavigationComponent(
    selectedScreen: Screen,
    onClick: (screen: Screen) -> Unit
) {

    NavigationBar(tonalElevation = 0.dp, modifier = Modifier.shadow(4.dp)) {
        Screen.values().forEach { screen ->

            val displayName = stringResource(id = screen.displayName)

            NavigationBarItem(
                icon = {
                    Icon(
                        painter = painterResource(id = screen.icon),
                        contentDescription = displayName
                    )
                },
                label = { Text(text = displayName) },
                selected = selectedScreen == screen,
                onClick = { onClick(screen) }
            )

        }
    }

}

@Composable
fun SideNavigationComponent(
    selectedScreen: Screen,
    onClick: (screen: Screen) -> Unit,
    onClickTitleText: () -> Unit
) {

    NavigationRail(modifier = Modifier.shadow(4.dp)) {
        Column(
            verticalArrangement = Arrangement.SpaceEvenly,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxHeight()
        ) {

            ClickableAppName(onClick = onClickTitleText)

            Screen.values().forEach { screen ->

                val displayName = stringResource(id = screen.displayName)

                NavigationRailItem(
                    icon = {
                        Icon(
                            painter = painterResource(id = screen.icon),
                            contentDescription = displayName
                        )
                    },
                    label = { Text(text = displayName) },
                    selected = selectedScreen == screen,
                    onClick = { onClick(screen) }
                )

            }

        }
    }

}