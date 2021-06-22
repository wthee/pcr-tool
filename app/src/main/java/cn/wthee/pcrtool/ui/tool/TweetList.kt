package cn.wthee.pcrtool.ui.tool

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLifecycleOwner
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
import cn.wthee.pcrtool.ui.compose.*
import cn.wthee.pcrtool.ui.theme.Dimen
import cn.wthee.pcrtool.viewmodel.TweetViewModel
import com.google.accompanist.placeholder.PlaceholderHighlight
import com.google.accompanist.placeholder.material.placeholder
import com.google.accompanist.placeholder.material.shimmer
import kotlinx.coroutines.launch

/**
 * 推特列表
 */
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
        val visible = tweet != null
        LazyColumn(state = scrollState, contentPadding = PaddingValues(Dimen.largePadding)) {
            if (visible) {
                items(tweet!!) {
                    if (it != null) {
                        TweetItem(it)
                    }
                }
            } else {
                items(12) {
                    TweetItem(TweetData())
                }
            }
            item {
                CommonSpacer()
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
@ExperimentalMaterialApi
@Composable
private fun TweetItem(data: TweetData) {
    val placeholder = data.id == ""

    Row(
        modifier = Modifier.padding(bottom = Dimen.mediuPadding),
        verticalAlignment = Alignment.CenterVertically
    ) {
        MainTitleText(
            text = data.date,
            modifier = Modifier.placeholder(
                visible = placeholder,
                highlight = PlaceholderHighlight.shimmer()
            )
        )
    }

    MainCard(modifier = Modifier
        .padding(bottom = Dimen.largePadding)
        .placeholder(
            visible = placeholder,
            highlight = PlaceholderHighlight.shimmer()
        ),
        onClick = {
            if (!placeholder) {
                // TODO 浏览器打开
            }
        }
    ) {
        Column(modifier = Modifier.padding(Dimen.largePadding)) {
            //内容
            MainContentText(
                text = data.tweet,
                textAlign = TextAlign.Start
            )
        }

    }
}
