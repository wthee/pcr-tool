package cn.wthee.pcrtool.ui.tool

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.paging.LoadState
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.items
import cn.wthee.pcrtool.R
import cn.wthee.pcrtool.data.db.entity.TweetData
import cn.wthee.pcrtool.data.enums.MainIconType
import cn.wthee.pcrtool.data.model.TweetButtonData
import cn.wthee.pcrtool.ui.PreviewBox
import cn.wthee.pcrtool.ui.common.*
import cn.wthee.pcrtool.ui.theme.Dimen
import cn.wthee.pcrtool.ui.theme.ExpandAnimation
import cn.wthee.pcrtool.ui.theme.FadeAnimation
import cn.wthee.pcrtool.utils.BrowserUtil
import cn.wthee.pcrtool.utils.ImageResourceHelper.Companion.COMIC4
import cn.wthee.pcrtool.utils.ImageResourceHelper.Companion.PNG
import cn.wthee.pcrtool.viewmodel.TweetViewModel

/**
 * 推特列表
 */
@Composable
fun TweetList(
    toComic: (Int) -> Unit,
    tweetViewModel: TweetViewModel = hiltViewModel()
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

    //获取分页数据
    val tweetPager = remember(keywordState.value) {
        tweetViewModel.getTweet(keywordState.value)
    }
    val tweetItems = tweetPager.flow.collectAsLazyPagingItems()

    Box(modifier = Modifier.fillMaxSize()) {
        LazyColumn(state = scrollState) {
            //头部加载中提示
            item {
                ExpandAnimation(tweetItems.loadState.refresh == LoadState.Loading) {
                    Box(modifier = Modifier
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
                TweetItem(it ?: TweetData(), toComic)
            }
            //暂无更多提示
            if (tweetItems.loadState.refresh != LoadState.Loading) {
                item {
                    FadeAnimation(tweetItems.loadState.append is LoadState.NotLoading) {
                        CenterTipText(stringResource(id = R.string.no_more))
                    }
                }
            }
            item {
                CommonSpacer()
            }
        }

        //搜索栏
        BottomSearchBar(
            modifier = Modifier
                .align(Alignment.BottomEnd),
            labelStringId = R.string.tweet,
            keywordInputState = keywordInputState,
            keywordState = keywordState,
            leadingIcon = MainIconType.TWEET,
            scrollState = scrollState
        )
    }
}


/**
 * 推特内容
 */
@Composable
private fun TweetItem(data: TweetData, toComic: (Int) -> Unit) {
    val placeholder = data.id == 0
    val photos = data.getImageList()
    var comicId = ""
    var url = if (data.tweet.startsWith("【ぷりこねっ！りだいぶ】")) {
        // 四格漫画
        comicId = getComicId(data.tweet)
        COMIC4 + comicId + PNG
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
                TweetButton(data.link, toComic = toComic)
                data.getUrlList().forEach {
                    TweetButton(it, comicId, toComic = toComic)
                }
            }
        }
        //图片
        if (photos.isNotEmpty()) {
            VerticalGrid(maxColumnWidth = getItemWidth()) {
                photos.forEach {
                    val isComic = url != ""
                    if (!isComic) {
                        url = it
                    }
                    ImageCompose(
                        data = url,
                        ratio = if (isComic) RATIO_COMIC else RATIO_COMMON,
                        modifier = Modifier.fillMaxWidth()
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
    url: String,
    comicId: String = "",
    toComic: (Int) -> Unit
) {
    val context = LocalContext.current
    //根据链接获取相符的图标
    val btn = when {
        url.contains("youtu.be") || url.contains("www.youtube.com") -> TweetButtonData(
            stringResource(id = R.string.youtube), MainIconType.YOUTUBE,
        ) {
            BrowserUtil.open(context, url)
        }
        url.contains("priconne-redive.jp/news/") -> TweetButtonData(
            stringResource(id = R.string.read_news), MainIconType.NEWS
        ) {
            //公告详情
            BrowserUtil.open(context, url)
        }
        url.contains("twitter.com") -> TweetButtonData(
            stringResource(id = R.string.twitter), MainIconType.TWEET
        ) {
            BrowserUtil.open(context, url)
        }
        url.contains("hibiki-radio.jp") -> TweetButtonData(
            stringResource(id = R.string.hibiki), MainIconType.HIBIKI
        ) {
            BrowserUtil.open(context, url)
        }
        url.contains("comic") -> TweetButtonData(
            stringResource(id = R.string.read_comic), MainIconType.COMIC
        ) {
            //跳转漫画
            if (comicId != "") {
                toComic(comicId.toInt())
            }
        }
        else -> TweetButtonData(stringResource(id = R.string.other), MainIconType.BROWSER) {
            BrowserUtil.open(context, url)
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

@Preview
@Composable
private fun TweetItemPreview() {
    PreviewBox {
        TweetItem(data = TweetData(id = 1), toComic = {})
    }
}

