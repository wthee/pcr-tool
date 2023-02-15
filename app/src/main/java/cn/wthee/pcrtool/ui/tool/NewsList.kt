package cn.wthee.pcrtool.ui.tool

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.paging.LoadState
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.items
import cn.wthee.pcrtool.R
import cn.wthee.pcrtool.data.db.entity.NewsTable
import cn.wthee.pcrtool.data.enums.KeywordType
import cn.wthee.pcrtool.data.enums.MainIconType
import cn.wthee.pcrtool.data.enums.NewsType
import cn.wthee.pcrtool.ui.MainActivity
import cn.wthee.pcrtool.ui.common.*
import cn.wthee.pcrtool.ui.theme.*
import cn.wthee.pcrtool.utils.BrowserUtil
import cn.wthee.pcrtool.utils.formatTime
import cn.wthee.pcrtool.viewmodel.CommonApiViewModel
import cn.wthee.pcrtool.viewmodel.NewsViewModel

/**
 * 公告列表
 */
@Composable
fun NewsList(
    newsViewModel: NewsViewModel = hiltViewModel(),
    commonAPIViewModel: CommonApiViewModel = hiltViewModel(),
) {
    val scrollState = rememberLazyListState()
    //关键词输入
    val keywordInputState = remember {
        mutableStateOf("")
    }
    //关键词查询
    val keywordState = remember {
        mutableStateOf("")
    }
    //时间范围
    val dateRange = remember {
        mutableStateOf(DateRange())
    }
    //获取分页数据
    val newsPager = remember(keywordState.value, dateRange.value) {
        newsViewModel.getNewsPager(MainActivity.regionType.value, keywordState.value, dateRange.value)
    }
    val newsItems = newsPager.flow.collectAsLazyPagingItems()

    //获取关键词
    val keywordFlow = remember {
        commonAPIViewModel.getKeywords(KeywordType.NEWS)
    }
    val keywordList = keywordFlow.collectAsState(initial = arrayListOf()).value

    Box(modifier = Modifier.fillMaxSize()) {
        LazyColumn(state = scrollState) {
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
                items = newsItems,
                key = {
                    it.id
                }
            ) {
                if (it != null) {
                    NewsItem(news = it)
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

        //日期选择
        DateRangePickerCompose(dateRange = dateRange)

        //搜索栏
        BottomSearchBar(
            modifier = Modifier
                .align(Alignment.BottomEnd),
            labelStringId = R.string.news_title,
            keywordInputState = keywordInputState,
            keywordState = keywordState,
            leadingIcon = MainIconType.NEWS,
            scrollState = scrollState,
            defaultKeywordList = keywordList,
            onResetClick = {
                //同时重置时间筛选
                dateRange.value = DateRange()
            }
        )
    }
}

/**
 * 新闻公告
 */
@Composable
fun NewsItem(
    news: NewsTable
) {
    val placeholder = news.title == ""
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