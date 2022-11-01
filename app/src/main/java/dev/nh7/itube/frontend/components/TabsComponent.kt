package dev.nh7.itube.frontend.components

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.rememberPagerState
import kotlinx.coroutines.launch


class TabContent(
    val titleId: Int,
    val content: @Composable () -> Unit,
)

@ExperimentalPagerApi
@Composable
fun TabsComponent(
    tabs: List<TabContent>
) {

    val pagerState = rememberPagerState()
    val coroutineScope = rememberCoroutineScope()

    Column {

        TabRow(selectedTabIndex = pagerState.currentPage) {
            tabs.forEachIndexed { index, tabContent ->
                Tab(
                    selected = pagerState.currentPage == index,
                    onClick = {
                        coroutineScope.launch { pagerState.animateScrollToPage(index) }
                    },
                    text = {
                        Text(
                            text = stringResource(id = tabContent.titleId),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                )
            }
        }

        HorizontalPager(
            count = tabs.size,
            state = pagerState,
        ) { page ->
            tabs[page].content()
        }

    }

}