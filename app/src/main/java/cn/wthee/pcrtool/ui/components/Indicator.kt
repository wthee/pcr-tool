package cn.wthee.pcrtool.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CutCornerShape
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
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.max
import cn.wthee.pcrtool.ui.theme.CombinedPreviews
import cn.wthee.pcrtool.ui.theme.Dimen
import cn.wthee.pcrtool.ui.theme.PreviewLayout
import cn.wthee.pcrtool.ui.theme.invertedTriangleShape
import cn.wthee.pcrtool.utils.px2dp
import kotlinx.coroutines.launch
import kotlin.math.absoluteValue
import kotlin.math.sign

/**
 * 带指示器图标
 * @param urlList 最大5个
 */
@Composable
fun IconHorizontalPagerIndicator(
    pagerState: PagerState,
    urlList: List<String>,
    extraContent: @Composable (Int) -> Unit = {}
) {
    val scope = rememberCoroutineScope()
    val context = LocalContext.current

    var itemWidth by remember {
        mutableStateOf(0.dp)
    }
    val indicatorSize = Dimen.indicatorSize
    val spacing = max(0.dp, itemWidth - indicatorSize)

    Column(
        modifier = Modifier
            .fillMaxWidth(urlList.size * 0.2f)
            .onSizeChanged {
                itemWidth = px2dp(context, it.width) / urlList.size.toFloat()
            },
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        //fixme 未居中对齐
        MainHorizontalPagerIndicator(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    bottom = Dimen.smallPadding,
                    start = spacing / 2f
                ),
            pagerState = pagerState,
            pageCount = urlList.size,
            indicatorSize = indicatorSize,
            inactiveColor = Color.Transparent,
            spacing = spacing,
            indicatorShape = invertedTriangleShape
        )

        Row(modifier = Modifier.fillMaxWidth()) {
            urlList.forEachIndexed { index, url ->
                Column(
                    modifier = Modifier
                        .weight(1f),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    MainIcon(
                        data = url,
                        onClick = {
                            scope.launch {
                                pagerState.scrollToPage(index)
                            }
                        }
                    )

                    extraContent(index)
                }
            }
        }

    }
}

/**
 * 指示器
 */
@Composable
fun MainHorizontalPagerIndicator(
    modifier: Modifier = Modifier,
    pagerState: PagerState,
    pageCount: Int,
    activeColor: Color = MaterialTheme.colorScheme.primary,
    inactiveColor: Color = activeColor.copy(alpha = 0.5f),
    indicatorSize: Dp = Dimen.indicatorSize,
    spacing: Dp = 1.dp,
    indicatorShape: Shape = CutCornerShape(45)
) {
    val indicatorWidthPx = LocalDensity.current.run { indicatorSize.toPx() }
    val spacingPx = LocalDensity.current.run { spacing.toPx() }

    Box(
        modifier = modifier,
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(spacing),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            val indicatorModifier = Modifier
                .size(width = indicatorSize, height = indicatorSize)
                .background(color = inactiveColor, shape = indicatorShape)

            repeat(pageCount) {
                Box(indicatorModifier)
            }
        }

        Box(
            Modifier
                .offset {
                    val position = pagerState.currentPage
                    val offset = pagerState.currentPageOffsetFraction
                    val next = pagerState.currentPage + offset.sign.toInt()
                    val scrollPosition = ((next - position) * offset.absoluteValue + position)
                        .coerceIn(
                            0f,
                            (pageCount - 1)
                                .coerceAtLeast(0)
                                .toFloat()
                        )

                    IntOffset(
                        x = ((spacingPx + indicatorWidthPx) * scrollPosition).toInt(),
                        y = 0
                    )
                }
                .size(width = indicatorSize, height = indicatorSize)
                .then(
                    if (pageCount > 0) Modifier.background(
                        color = activeColor,
                        shape = indicatorShape,
                    )
                    else Modifier
                )
        )
    }
}

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