package cn.wthee.pcrtool.ui.tool

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.flowWithLifecycle
import androidx.paging.ExperimentalPagingApi
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.items
import cn.wthee.pcrtool.R
import cn.wthee.pcrtool.data.db.entity.TweetData
import cn.wthee.pcrtool.data.enums.MainIconType
import cn.wthee.pcrtool.ui.compose.CommonSpacer
import cn.wthee.pcrtool.ui.compose.FabCompose
import cn.wthee.pcrtool.ui.compose.FadeAnimation
import cn.wthee.pcrtool.ui.compose.MainContentText
import cn.wthee.pcrtool.ui.theme.Dimen
import cn.wthee.pcrtool.viewmodel.TweetViewModel
import com.google.accompanist.coil.rememberCoilPainter
import com.google.accompanist.imageloading.ImageLoadState
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
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
                items(tweet!!) {
                    TweetItem(it ?: TweetData())
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
@ExperimentalPagerApi
@ExperimentalMaterialApi
@Composable
private fun TweetItem(data: TweetData) {
    val placeholder = data.id == ""
    val urls = data.getImageList()
    val pagerState = rememberPagerState(pageCount = urls.size)

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
    Column(
        modifier = Modifier
            .padding(Dimen.largePadding)
            .fillMaxWidth()
            .defaultMinSize(minHeight = Dimen.cardHeight * 2)
            .placeholder(
                visible = placeholder,
                highlight = PlaceholderHighlight.shimmer()
            ),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        //内容
        MainContentText(
            text = data.getFormatTweet(),
            textAlign = TextAlign.Start,
            selectable = true
        )

        if (urls.isNotEmpty()) {
            HorizontalPager(state = pagerState) { pageIndex ->
                val painter = rememberCoilPainter(request = urls[pageIndex])
                Image(
                    painter = when (painter.loadState) {
                        is ImageLoadState.Success -> painter
                        is ImageLoadState.Loading -> rememberCoilPainter(request = R.drawable.load)
                        else -> rememberCoilPainter(request = R.drawable.error)
                    },
                    contentDescription = null,
                    contentScale = ContentScale.FillWidth,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}
