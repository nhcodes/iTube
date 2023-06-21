package codes.nh.itube.frontend.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import codes.nh.itube.R


@ExperimentalMaterial3Api
@Composable
fun TopBarComponent(onClickTitleText: () -> Unit) {

    CenterAlignedTopAppBar(
        title = {
            ClickableAppName(onClick = onClickTitleText)
        },
        modifier = Modifier.shadow(4.dp)
    )

}

@Composable
fun ClickableAppName(onClick: () -> Unit) {
    Text(
        text = stringResource(id = R.string.app_name),
        modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .clickable(onClick = onClick)
            .padding(2.dp)
    )
}