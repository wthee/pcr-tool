package cn.wthee.pcrtool.ui.tool

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.paging.LoadState
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.items
import cn.wthee.pcrtool.R
import cn.wthee.pcrtool.data.db.entity.TweetData
import cn.wthee.pcrtool.data.enums.KeywordType
import cn.wthee.pcrtool.data.enums.MainIconType
import cn.wthee.pcrtool.data.model.TweetButtonData
import cn.wthee.pcrtool.ui.common.BottomSearchBar
import cn.wthee.pcrtool.ui.common.CenterTipText
import cn.wthee.pcrtool.ui.common.CircularProgressCompose
import cn.wthee.pcrtool.ui.common.CommonSpacer
import cn.wthee.pcrtool.ui.common.DateRange
import cn.wthee.pcrtool.ui.common.DateRangePickerCompose
import cn.wthee.pcrtool.ui.common.IconTextButton
import cn.wthee.pcrtool.ui.common.ImageCompose
import cn.wthee.pcrtool.ui.common.MainContentText
import cn.wthee.pcrtool.ui.common.MainTitleText
import cn.wthee.pcrtool.ui.common.RATIO_COMIC
import cn.wthee.pcrtool.ui.common.RATIO_COMMON
import cn.wthee.pcrtool.ui.common.VerticalGrid
import cn.wthee.pcrtool.ui.common.commonPlaceholder
import cn.wthee.pcrtool.ui.common.getItemWidth
import cn.wthee.pcrtool.ui.theme.CombinedPreviews
import cn.wthee.pcrtool.ui.theme.Dimen
import cn.wthee.pcrtool.ui.theme.ExpandAnimation
import cn.wthee.pcrtool.ui.theme.FadeAnimation
import cn.wthee.pcrtool.ui.theme.PreviewLayout
import cn.wthee.pcrtool.utils.BrowserUtil
import cn.wthee.pcrtool.utils.ImageRequestHelper.Companion.COMIC4
import cn.wthee.pcrtool.utils.ImageRequestHelper.Companion.PNG
import cn.wthee.pcrtool.viewmodel.CommonApiViewModel
import cn.wthee.pcrtool.viewmodel.TweetViewModel

/**
 * 推特列表
 */
@Composable
fun TweetList(
    tweetViewModel: TweetViewModel = hiltViewModel(),
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
    val tweetPager = remember(keywordState.value, dateRange.value) {
        tweetViewModel.getTweet(keywordState.value, dateRange.value)
    }
    val tweetItems = tweetPager.flow.collectAsLazyPagingItems()

    //获取关键词
    val keywordFlow = remember {
        commonAPIViewModel.getKeywords(KeywordType.TWEET)
    }
    val keywordList = keywordFlow.collectAsState(initial = arrayListOf()).value

    Box(modifier = Modifier.fillMaxSize()) {
        LazyColumn(state = scrollState) {
            //头部加载中提示
            item {
                ExpandAnimation(tweetItems.loadState.refresh == LoadState.Loading) {
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
                items = tweetItems,
                key = {
                    it.id
                }
            ) {
                TweetItem(it ?: TweetData())
            }
            //暂无更多提示
            if (tweetItems.loadState.refresh != LoadState.Loading) {
                item {
                    FadeAnimation(tweetItems.loadState.append is LoadState.NotLoading) {
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
            labelStringId = R.string.tweet,
            keywordInputState = keywordInputState,
            keywordState = keywordState,
            leadingIcon = MainIconType.TWEET,
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
 * 推特内容
 */
@Composable
private fun TweetItem(data: TweetData) {
    val placeholder = data.id == 0
    val photos = data.getImageList()
    val isComicUrl = data.tweet.contains("【4コマ更新】")
    var url = if (isComicUrl) {
        // 四格漫画
        COMIC4 + getComicId(data.tweet) + PNG
    } else {
        ""
    }
    // 时间
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(top = Dimen.largePadding, start = Dimen.largePadding)
    ) {
        MainTitleText(
            text = data.date,
            modifier = Modifier.commonPlaceholder(placeholder)
        )
    }

    //内容
    Column(
        modifier = Modifier
            .padding(
                start = Dimen.largePadding,
                end = Dimen.largePadding,
                top = Dimen.smallPadding,
                bottom = Dimen.largePadding
            )
            .fillMaxWidth()
            .defaultMinSize(minHeight = Dimen.cardHeight * 2)
            .commonPlaceholder(visible = placeholder),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        //文本
        MainContentText(
            text = data.getFormatTweet(),
            textAlign = TextAlign.Start,
            selectable = true
        )
        //相关链接跳转
        if (!placeholder) {
            Row(
                modifier = Modifier
                    .padding(vertical = Dimen.smallPadding)
                    .fillMaxWidth()
            ) {
                TweetButton(data.link)
                data.getUrlList().forEach {
                    TweetButton(it)
                }
            }
        }

        //图片
        if (photos.isNotEmpty()) {
            VerticalGrid(itemWidth = getItemWidth()) {
                photos.forEach {
                    if (!isComicUrl) {
                        url = it
                    }
                    ImageCompose(
                        data = url,
                        ratio = if (isComicUrl) RATIO_COMIC else RATIO_COMMON,
                        modifier = Modifier.fillMaxWidth(),
                        contentScale = ContentScale.FillHeight
                    )
                }
            }
        }
    }
}

/**
 * 相关链接按钮
 */
@Composable
private fun TweetButton(
    url: String
) {
    //根据链接获取相符的图标
    val btn = when {
        url.contains("youtu.be") || url.contains("www.youtube.com") -> TweetButtonData(
            stringResource(id = R.string.youtube), MainIconType.YOUTUBE,
        ) {
            BrowserUtil.open(url)
        }

        url.contains("priconne-redive.jp/news/") -> TweetButtonData(
            stringResource(id = R.string.read_news), MainIconType.NEWS
        ) {
            //公告详情
            BrowserUtil.open(url)
        }

        url.contains("twitter.com") -> TweetButtonData(
            stringResource(id = R.string.twitter), MainIconType.TWEET
        ) {
            BrowserUtil.open(url)
        }

        url.contains("hibiki-radio.jp") -> TweetButtonData(
            stringResource(id = R.string.hibiki), MainIconType.HIBIKI
        ) {
            BrowserUtil.open(url)
        }

        else -> TweetButtonData(stringResource(id = R.string.other), MainIconType.BROWSER) {
            BrowserUtil.open(url)
        }
    }

    IconTextButton(icon = btn.iconType, text = btn.text) {
        btn.action()
    }
}

/**
 * 获取id
 */
private fun getComicId(title: String): String {
    return try {
        val result = Regex("(.*)第(.*?)話「(.*?)」").findAll(title)
        result.first().groups[2]!!.value
    } catch (e: Exception) {
        ""
    }
}

@CombinedPreviews
@Composable
private fun TweetItemPreview() {
    PreviewLayout {
        TweetItem(data = TweetData(id = 1, tweet = "???"))
    }
}

