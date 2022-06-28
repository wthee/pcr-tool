package cn.wthee.pcrtool.ui.tool

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.flowWithLifecycle
import androidx.paging.LoadState
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.items
import cn.wthee.pcrtool.R
import cn.wthee.pcrtool.data.db.entity.TweetData
import cn.wthee.pcrtool.data.db.entity.getNewsId
import cn.wthee.pcrtool.data.enums.MainIconType
import cn.wthee.pcrtool.data.model.TweetButtonData
import cn.wthee.pcrtool.ui.PreviewBox
import cn.wthee.pcrtool.ui.common.*
import cn.wthee.pcrtool.ui.theme.Dimen
import cn.wthee.pcrtool.ui.theme.FadeAnimation
import cn.wthee.pcrtool.utils.BrowserUtil
import cn.wthee.pcrtool.utils.ImageResourceHelper.Companion.COMIC4
import cn.wthee.pcrtool.utils.ImageResourceHelper.Companion.PNG
import cn.wthee.pcrtool.viewmodel.TweetViewModel
import com.google.accompanist.placeholder.PlaceholderHighlight
import com.google.accompanist.placeholder.material.placeholder
import com.google.accompanist.placeholder.material.shimmer
import kotlinx.coroutines.launch

/**
 * 推特列表
 */
@Composable
fun TweetList(
    scrollState: LazyListState,
    toNewsDetail: (String) -> Unit,
    toComic: (Int) -> Unit,
    tweetViewModel: TweetViewModel = hiltViewModel()
) {
    val coroutineScope = rememberCoroutineScope()
    //推文
    val lifecycle = LocalLifecycleOwner.current.lifecycle
    tweetViewModel.getTweet()
    val flow = tweetViewModel.tweetPageList
    val tweet = remember(flow, lifecycle) {
        flow?.flowWithLifecycle(lifecycle = lifecycle)
    }?.collectAsLazyPagingItems()

    Box(modifier = Modifier.fillMaxSize()) {
        val visible = tweet != null && tweet.itemCount > 0
        FadeAnimation(visible = visible) {
            LazyColumn(state = scrollState) {
                if (tweet!!.loadState.prepend is LoadState.Loading) {
                    item {
                        TweetItem(TweetData(), toNewsDetail, toComic)
                    }
                }
                items(
                    items = tweet,
                    key = {
                        it.id
                    }
                ) {
                    TweetItem(it ?: TweetData(), toNewsDetail, toComic)
                }
                if (tweet.loadState.append is LoadState.Loading) {
                    item {
                        TweetItem(TweetData(), toNewsDetail, toComic)
                    }
                }
                item {
                    CommonSpacer()
                }
            }
        }
        FadeAnimation(visible = !visible) {
            LazyColumn(state = rememberLazyListState()) {
                items(12) {
                    TweetItem(TweetData(), toNewsDetail, toComic)
                }
                item {
                    CommonSpacer()
                }
            }
        }
        //回到顶部
        FabCompose(
            iconType = MainIconType.TWEET,
            text = stringResource(id = R.string.tweet),
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(end = Dimen.fabMarginEnd, bottom = Dimen.fabMargin)
        ) {
            coroutineScope.launch {
                try {
                    scrollState.scrollToItem(0)
                } catch (e: Exception) {
                }
            }
        }
    }
}


/**
 * 推特内容
 */
@Composable
private fun TweetItem(data: TweetData, toNewsDetail: (String) -> Unit, toComic: (Int) -> Unit) {
    val placeholder = data.id == ""
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
        modifier = Modifier.padding(top = Dimen.largePadding)
    ) {
        Text(
            text = data.date,
            color = MaterialTheme.colorScheme.primary,
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier
                .padding(start = Dimen.mediumPadding)
                .placeholder(
                    visible = placeholder,
                    highlight = PlaceholderHighlight.shimmer()
                )
        )
        if (!placeholder) {
            Spacer(
                modifier = Modifier
                    .padding(start = Dimen.largePadding)
                    .weight(1f)
                    .height(Dimen.divLineHeight)
                    .background(colorResource(id = R.color.div_line))
            )
        }
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
            .placeholder(
                visible = placeholder,
                highlight = PlaceholderHighlight.shimmer()
            ),
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
            Row(modifier = Modifier.fillMaxWidth()) {
                TweetButton(data.link, toNewsDetail = toNewsDetail, toComic = toComic)
                data.getUrlList().forEach {
                    TweetButton(it, comicId, toNewsDetail = toNewsDetail, toComic = toComic)
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
                        loadingId = R.drawable.load,
                        errorId = R.drawable.error,
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
    toNewsDetail: (String) -> Unit,
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
            //跳转至公告详情
            toNewsDetail(url.getNewsId())
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


    TextButton(
        onClick = {
            btn.action()
        },
    ) {
        IconCompose(data = btn.iconType, size = Dimen.smallIconSize)
        MainContentText(text = btn.text)
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
        TweetItem(data = TweetData(id = "?"), toNewsDetail = {}, toComic = {})
    }
}

