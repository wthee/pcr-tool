package cn.wthee.pcrtool.ui.tool.news

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.rememberLazyStaggeredGridState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.rememberDateRangePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.paging.LoadState
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemContentType
import androidx.paging.compose.itemKey
import cn.wthee.pcrtool.R
import cn.wthee.pcrtool.data.db.entity.NewsTable
import cn.wthee.pcrtool.data.enums.MainIconType
import cn.wthee.pcrtool.data.enums.NewsType
import cn.wthee.pcrtool.navigation.navigateUp
import cn.wthee.pcrtool.ui.components.BottomSearchBar
import cn.wthee.pcrtool.ui.components.CenterTipText
import cn.wthee.pcrtool.ui.components.CircularProgressCompose
import cn.wthee.pcrtool.ui.components.CommonSpacer
import cn.wthee.pcrtool.ui.components.DateRangePickerCompose
import cn.wthee.pcrtool.ui.components.MainCard
import cn.wthee.pcrtool.ui.components.MainScaffold
import cn.wthee.pcrtool.ui.components.MainTitleText
import cn.wthee.pcrtool.ui.components.Subtitle1
import cn.wthee.pcrtool.ui.components.commonPlaceholder
import cn.wthee.pcrtool.ui.components.getDatePickerYearRange
import cn.wthee.pcrtool.ui.components.getItemWidth
import cn.wthee.pcrtool.ui.theme.CombinedPreviews
import cn.wthee.pcrtool.ui.theme.Dimen
import cn.wthee.pcrtool.ui.theme.ExpandAnimation
import cn.wthee.pcrtool.ui.theme.FadeAnimation
import cn.wthee.pcrtool.ui.theme.PreviewLayout
import cn.wthee.pcrtool.ui.theme.colorPurple
import cn.wthee.pcrtool.ui.theme.colorRed
import cn.wthee.pcrtool.utils.BrowserUtil
import cn.wthee.pcrtool.utils.formatTime

/**
 * 公告列表
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NewsScreen(
    newsViewModel: NewsViewModel = hiltViewModel()
) {
    val scrollState = rememberLazyStaggeredGridState()
    val uiState by newsViewModel.uiState.collectAsStateWithLifecycle()
    val dateRangePickerState = rememberDateRangePickerState(yearRange = getDatePickerYearRange())


    //获取分页数据
    uiState.pager?.let { pager ->
        val newsItems = pager.flow.collectAsLazyPagingItems()

        MainScaffold(
            enableClickClose = uiState.openDialog || uiState.openSearch,
            onCloseClick = {
                newsViewModel.changeDialog(false)
                newsViewModel.changeSearchBar(false)
            },
            secondLineFab = {
                //日期选择
                DateRangePickerCompose(
                    dateRangePickerState = dateRangePickerState,
                    dateRange = uiState.dateRange,
                    openDialog = uiState.openDialog,
                    changeRange = newsViewModel::changeRange,
                    changeDialog = newsViewModel::changeDialog
                )
            },
            fabWithCustomPadding = {
                //搜索栏
                BottomSearchBar(
                    labelStringId = R.string.news_title,
                    keyword = uiState.keyword,
                    openSearch = uiState.openSearch,
                    leadingIcon = MainIconType.NEWS,
                    defaultKeywordList = uiState.keywordList,
                    showReset = uiState.dateRange.hasFilter() || uiState.keyword != "",
                    changeSearchBar = newsViewModel::changeSearchBar,
                    changeKeyword = newsViewModel::changeKeyword,
                    onTopClick = {
                        scrollState.scrollToItem(0)
                    },
                    onResetClick = {
                        newsViewModel.reset()
                        dateRangePickerState.setSelection(null, null)
                    }
                )
            },
            mainFabIcon = if (uiState.openDialog || uiState.openSearch) MainIconType.CLOSE else MainIconType.BACK,
            onMainFabClick = {
                if (uiState.openDialog || uiState.openSearch) {
                    newsViewModel.changeDialog(false)
                    newsViewModel.changeSearchBar(false)
                } else {
                    navigateUp()
                }
            }
        ) {
            LazyVerticalStaggeredGrid(
                state = scrollState,
                columns = StaggeredGridCells.Adaptive(getItemWidth())
            ) {
                //头部加载中提示
                item {
                    ExpandAnimation(newsItems.loadState.refresh == LoadState.Loading) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(Dimen.largePadding)
                        ) {
                            CircularProgressCompose(Modifier.align(Alignment.Center))
                        }
                    }
                }
                items(
                    count = newsItems.itemCount,
                    key = newsItems.itemKey(
                        key = {
                            it.id
                        }
                    ),
                    contentType = newsItems.itemContentType()
                ) { index ->
                    val item = newsItems[index]
                    if (item != null) {
                        NewsItem(news = item)
                    }
                }
                //暂无更多提示
                if (newsItems.loadState.refresh != LoadState.Loading) {
                    item {
                        FadeAnimation(newsItems.loadState.append is LoadState.NotLoading) {
                            CenterTipText(stringResource(id = R.string.no_more))
                        }
                    }
                }
                items(2) {
                    CommonSpacer()
                }
            }
        }
    }
}

/**
 * 新闻公告
 */
@Composable
fun NewsItem(
    news: NewsTable
) {
    val placeholder = news.id == -1
    val tag = news.getTag()
    val color = when (tag) {
        NewsType.NEWS, NewsType.UPDATE -> colorRed
        NewsType.SYSTEM -> colorPurple
        else -> MaterialTheme.colorScheme.primary
    }
    Column(
        modifier = Modifier.padding(horizontal = Dimen.largePadding, vertical = Dimen.mediumPadding)
    ) {
        //标题
        Row(modifier = Modifier.padding(bottom = Dimen.mediumPadding)) {
            MainTitleText(
                text = stringResource(id = tag.stringId),
                backgroundColor = color,
                modifier = Modifier.commonPlaceholder(visible = placeholder)
            )
            MainTitleText(
                text = news.date.formatTime,
                modifier = Modifier
                    .padding(start = Dimen.smallPadding)
                    .commonPlaceholder(visible = placeholder)
            )
        }
        MainCard(modifier = Modifier
            .commonPlaceholder(visible = placeholder)
            .heightIn(min = Dimen.cardHeight),
            onClick = {
                if (!placeholder) {
                    BrowserUtil.open(news.url)
                }
            }
        ) {
            //内容
            Subtitle1(
                text = news.title,
                modifier = Modifier.padding(Dimen.mediumPadding),
                selectable = true
            )
        }
    }

}


@CombinedPreviews
@Composable
private fun NewsItemPreview() {
    PreviewLayout {
        NewsItem(news = NewsTable(title = "?"))
    }
}