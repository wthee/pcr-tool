package cn.wthee.pcrtool.ui.components

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CutCornerShape
import androidx.compose.material.ContentAlpha
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import cn.wthee.pcrtool.ui.theme.CombinedPreviews
import cn.wthee.pcrtool.ui.theme.Dimen
import cn.wthee.pcrtool.ui.theme.PreviewLayout
import cn.wthee.pcrtool.utils.px2dp
import com.google.accompanist.pager.HorizontalPagerIndicator
import kotlinx.coroutines.launch

/**
 * 带指示器图标
 * @param urlList 最大5个
 */
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun IconHorizontalPagerIndicator(pagerState: PagerState, urlList: List<String>) {
    val scope = rememberCoroutineScope()
    val context = LocalContext.current

    var itemWidth by remember {
        mutableStateOf(0.dp)
    }
    val indicatorSize = Dimen.indicatorSize

    Column(modifier = Modifier.fillMaxWidth(urlList.size * 0.2f)) {
        Row(modifier = Modifier.fillMaxWidth()) {
            urlList.forEachIndexed { index, url ->
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .onSizeChanged {
                            itemWidth = px2dp(context, it.width)
                        },
                    contentAlignment = Alignment.Center
                ) {
                    MainIcon(
                        data = url,
                        onClick = {
                            scope.launch {
                                pagerState.scrollToPage(index)
                            }
                        }
                    )
                }
            }
        }
        MainHorizontalPagerIndicator(
            modifier = Modifier.padding(
                top = Dimen.smallPadding,
                start = maxOf(0.dp, itemWidth / 2 - Dimen.indicatorSize / 2)
            ),
            pagerState = pagerState,
            pageCount = urlList.size,
            inactiveColor = Color.Transparent,
            spacing = itemWidth - indicatorSize
        )
    }
}

/**
 * 指示器
 */
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun MainHorizontalPagerIndicator(
    modifier: Modifier = Modifier,
    pagerState: PagerState,
    pageCount: Int,
    activeColor: Color = MaterialTheme.colorScheme.primary,
    inactiveColor: Color = activeColor.copy(ContentAlpha.disabled),
    indicatorSize: Dp = Dimen.indicatorSize,
    spacing: Dp = 1.dp
) {
    HorizontalPagerIndicator(
        modifier = modifier.navigationBarsPadding(),
        pagerState = pagerState,
        pageCount = pageCount,
        indicatorWidth = indicatorSize,
        activeColor = activeColor,
        inactiveColor = inactiveColor,
        indicatorShape = CutCornerShape(45),
        spacing = spacing
    )
}

@OptIn(ExperimentalFoundationApi::class)
@CombinedPreviews
@Composable
private fun IconHorizontalPagerIndicatorPreview() {
    PreviewLayout {
        IconHorizontalPagerIndicator(
            pagerState = rememberPagerState { 5 },
            urlList = arrayListOf("1", "2", "3", "4", "5")
        )
        MainHorizontalPagerIndicator(
            pagerState = rememberPagerState { 5 },
            pageCount = 5
        )
    }
}