package cn.wthee.pcrtool.ui.components

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.LazyGridState
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
import kotlinx.coroutines.launch


/**
 * 通用 TabRow
 */
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun MainTabRow(
    modifier: Modifier = Modifier,
    pagerState: PagerState,
    tabs: List<String>,
    scrollable: Boolean = false,
    colorList: ArrayList<Color> = arrayListOf(),
    gridStateList: ArrayList<LazyGridState>? = null
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
            MainTabList(pagerState, tabs, colorList, gridStateList)
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
            MainTabList(pagerState, tabs, colorList, gridStateList)
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
    gridStateList: ArrayList<LazyGridState>? = null
) {
    val scope = rememberCoroutineScope()
    val context = LocalContext.current

    tabs.forEachIndexed { index, s ->
        Tab(
            selected = pagerState.currentPage == index,
            onClick = {
                scope.launch {
                    VibrateUtil(context).single()
                    if (pagerState.currentPage == index && gridStateList != null) {
                        gridStateList[index].scrollToItem(0)
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