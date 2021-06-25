package cn.wthee.pcrtool.ui.tool

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.TextButton
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
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.flowWithLifecycle
import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadState
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.items
import cn.wthee.pcrtool.R
import cn.wthee.pcrtool.data.db.entity.TweetData
import cn.wthee.pcrtool.data.db.entity.urlGetId
import cn.wthee.pcrtool.data.enums.MainIconType
import cn.wthee.pcrtool.data.model.TweetButtonData
import cn.wthee.pcrtool.ui.compose.*
import cn.wthee.pcrtool.ui.theme.Dimen
import cn.wthee.pcrtool.utils.Constants
import cn.wthee.pcrtool.utils.openWebView
import cn.wthee.pcrtool.viewmodel.TweetViewModel
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.HorizontalPagerIndicator
import com.google.accompanist.pager.rememberPagerState
import com.google.accompanist.placeholder.PlaceholderHighlight
import com.google.accompanist.placeholder.material.placeholder
import com.google.accompanist.placeholder.material.shimmer
import kotlinx.coroutines.launch

/**
 * 推特列表
 */
@ExperimentalPagerApi
@ExperimentalPagingApi
@ExperimentalMaterialApi
@ExperimentalAnimationApi
@Composable
fun TweetList(
    scrollState: LazyListState,
    toDetail: (String) -> Unit,
    tweetViewModel: TweetViewModel = hiltViewModel()
) {
    val lifecycle = LocalLifecycleOwner.current.lifecycle
    val coroutineScope = rememberCoroutineScope()

    tweetViewModel.getTweet()
    val flow = tweetViewModel.tweetPageList
    val tweet = remember(flow, lifecycle) {
        flow?.flowWithLifecycle(lifecycle = lifecycle)
    }?.collectAsLazyPagingItems()


    Box(
        modifier = Modifier
            .fillMaxSize()
    ) {
        val visible = tweet != null && tweet.itemCount > 0
        FadeAnimation(visible = visible) {
            LazyColumn(state = scrollState) {
                if (tweet!!.loadState.prepend is LoadState.Loading) {
                    item {
                        TweetItem(TweetData())
                    }
                }
                items(tweet) {
                    TweetItem(it ?: TweetData(), toDetail)
                }
                if (tweet.loadState.append is LoadState.Loading) {
                    item {
                        TweetItem(TweetData())
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
                    TweetItem(TweetData())
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
@ExperimentalAnimationApi
@ExperimentalPagerApi
@ExperimentalMaterialApi
@Composable
private fun TweetItem(data: TweetData, toDetail: ((String) -> Unit)? = null) {
    val placeholder = data.id == ""
    val photos = data.getImageList()
    val pagerState = rememberPagerState(pageCount = photos.size)

    // 时间
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(top = Dimen.largePadding)
    ) {
        Text(
            text = data.date,
            color = MaterialTheme.colors.primary,
            style = MaterialTheme.typography.subtitle1,
            modifier = Modifier
                .padding(start = Dimen.mediuPadding)
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
                TweetButton(data.link, toDetail)
                data.getUrlList().forEach {
                    TweetButton(it, toDetail)
                }
            }
        }
        //图片
        if (photos.isNotEmpty()) {
            HorizontalPager(
                state = pagerState,
                modifier = Modifier.padding(top = Dimen.mediuPadding, bottom = Dimen.mediuPadding)
            ) { pageIndex ->
                val url = if (data.tweet.startsWith("【ぷりこねっ！りだいぶ】")) {
                    // 四格漫画
                    Constants.COMIC_URL + getComicId(data.tweet) + Constants.PNG
                } else {
                    photos[pageIndex]
                }
                ImageCompose(url)
            }
            if (pagerState.pageCount > 1) {
                HorizontalPagerIndicator(pagerState = pagerState)
            }
        }
    }
}

/**
 * 相关链接按钮
 */
@ExperimentalAnimationApi
@Composable
private fun TweetButton(url: String, toDetail: ((String) -> Unit)? = null) {
    val context = LocalContext.current
    //根据链接获取相符的图标
    val btn = when {
        url.contains("youtu.be") || url.contains("www.youtube.com") -> TweetButtonData(
            stringResource(id = R.string.youtube), MainIconType.YOUTUBE
        )
        url.contains("priconne-redive.jp/news/") -> TweetButtonData(
            stringResource(id = R.string.tool_news), MainIconType.NEWS
        )
        url.contains("twitter.com") -> TweetButtonData(
            stringResource(id = R.string.twitter), MainIconType.TWEET
        )
        url.contains("hibiki-radio.jp") -> TweetButtonData(
            stringResource(id = R.string.hibiki), MainIconType.HIBIKI
        )
        else -> TweetButtonData(stringResource(id = R.string.other), MainIconType.BROWSER)
    }


    TextButton(
        onClick = {
            if (btn.iconType == MainIconType.NEWS) {
                //跳转至公告详情
                if (toDetail != null) {
                    toDetail(url.urlGetId())
                }
            } else {
                openWebView(context, url)
            }
        },
    ) {
        IconCompose(data = btn.iconType.icon, size = Dimen.smallIconSize)
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


