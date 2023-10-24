package cn.wthee.pcrtool.ui.components

import androidx.compose.foundation.ExperimentalFoundationApi
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import cn.wthee.pcrtool.ui.theme.Dimen
import cn.wthee.pcrtool.utils.VibrateUtil
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch


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
    tabs: List<String>,
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
            MainTabList(pagerState, tabs, colorList, onClickCurrentTab)
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
            MainTabList(pagerState, tabs, colorList, onClickCurrentTab)
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
    tabs: List<String>,
    colorList: ArrayList<Color>,
    onClickCurrentTab: (suspend CoroutineScope.(Int) -> Unit)? = null
) {
    val scope = rememberCoroutineScope()
    val context = LocalContext.current

    tabs.forEachIndexed { index, s ->
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
            }) {
            Subtitle1(
                text = s,
                modifier = Modifier.padding(Dimen.smallPadding),
                color = if (colorList.isNotEmpty()) {
                    colorList[index]
                } else {
                    if (pagerState.currentPage == index) {
                        MaterialTheme.colorScheme.primary
                    } else {
                        MaterialTheme.colorScheme.onSurface
                    }
                }
            )
        }
    }
}