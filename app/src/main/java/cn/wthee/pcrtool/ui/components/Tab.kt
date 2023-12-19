package cn.wthee.pcrtool.ui.components

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.PagerState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import cn.wthee.pcrtool.ui.theme.Dimen
import cn.wthee.pcrtool.utils.VibrateUtil
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch


data class TabData(
    var tab: String,
    var count: Int? = null,
    var isLoading: Boolean = false,
    var color: Color? = null
)

/**
 * 通用 TabRow
 *
 * @param onClickCurrentTab 在当前页面再次点击 tab
 */
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun MainTabRow(
    modifier: Modifier = Modifier,
    pagerState: PagerState,
    tabs: List<TabData>,
    scrollable: Boolean = false,
    colorList: ArrayList<Color> = arrayListOf(),
    onClickCurrentTab: (suspend CoroutineScope.(Int) -> Unit)? = null
) {
    val contentColor = if (colorList.isNotEmpty()) {
        colorList[pagerState.currentPage]
    } else {
        MaterialTheme.colorScheme.primary
    }

    if (scrollable) {
        ScrollableTabRow(
            selectedTabIndex = pagerState.currentPage,
            containerColor = Color.Transparent,
            contentColor = contentColor,
            indicator = { tabPositions ->
                TabRowDefaults.Indicator(
                    modifier = Modifier.tabIndicatorOffset(tabPositions[pagerState.currentPage]),
                    color = contentColor
                )
            },
            modifier = modifier
        ) {
            MainTabList(pagerState, tabs, onClickCurrentTab)
        }
    } else {
        TabRow(
            selectedTabIndex = pagerState.currentPage,
            containerColor = Color.Transparent,
            contentColor = contentColor,
            indicator = { tabPositions ->
                TabRowDefaults.Indicator(
                    modifier = Modifier.tabIndicatorOffset(tabPositions[pagerState.currentPage]),
                    color = contentColor
                )
            },
            modifier = modifier
        ) {
            MainTabList(pagerState, tabs, onClickCurrentTab)
        }
    }

}

/**
 * 通用 Tab
 */
@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun MainTabList(
    pagerState: PagerState,
    tabs: List<TabData>,
    onClickCurrentTab: (suspend CoroutineScope.(Int) -> Unit)? = null
) {
    val scope = rememberCoroutineScope()
    val context = LocalContext.current

    tabs.forEachIndexed { index, tab ->
        val color = if (tab.color != null) {
            tab.color!!
        } else {
            if (pagerState.currentPage == index) {
                MaterialTheme.colorScheme.primary
            } else {
                MaterialTheme.colorScheme.onSurface
            }
        }
        Tab(
            selected = pagerState.currentPage == index,
            onClick = {
                scope.launch {
                    VibrateUtil(context).single()
                    if (pagerState.currentPage == index && onClickCurrentTab != null) {
                        onClickCurrentTab(index)
                    } else {
                        pagerState.scrollToPage(index)
                    }
                }
            }
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Subtitle1(
                    text = tab.tab,
                    modifier = Modifier.padding(Dimen.smallPadding),
                    color = color
                )
                tab.count?.let {
                    Subtitle2(
                        text = it.toString(),
                        modifier = Modifier.padding(Dimen.smallPadding),
                        color = color
                    )
                }

                if (tab.isLoading) {
                    CircularProgressCompose(
                        size = Dimen.smallIconSize,
                        strokeWidth = Dimen.smallStrokeWidth
                    )
                }
            }

        }
    }
}