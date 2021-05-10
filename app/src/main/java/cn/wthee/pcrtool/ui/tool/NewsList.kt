package cn.wthee.pcrtool.ui.tool

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.Card
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.hilt.navigation.compose.hiltNavGraphViewModel
import androidx.paging.ExperimentalPagingApi
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemsIndexed
import cn.wthee.pcrtool.R
import cn.wthee.pcrtool.data.db.entity.NewsTable
import cn.wthee.pcrtool.data.enums.MainIconType
import cn.wthee.pcrtool.ui.compose.ExtendedFabCompose
import cn.wthee.pcrtool.ui.compose.MainContentText
import cn.wthee.pcrtool.ui.compose.MainTitleText
import cn.wthee.pcrtool.ui.theme.Dimen
import cn.wthee.pcrtool.ui.theme.Shapes
import cn.wthee.pcrtool.utils.BrowserUtil
import cn.wthee.pcrtool.viewmodel.NewsViewModel
import com.google.accompanist.pager.ExperimentalPagerApi
import kotlinx.coroutines.launch

/**
 * 公告列表
 */
@ExperimentalPagingApi
@ExperimentalPagerApi
@Composable
fun NewsList(region: Int, viewModel: NewsViewModel = hiltNavGraphViewModel()) {
    val coroutineScope = rememberCoroutineScope()
    val state = rememberLazyListState()
    val news = when (region) {
        2 -> viewModel.getNewsCN()
        3 -> viewModel.getNewsTW()
        else -> viewModel.getNewsJP()
    }
    val title = when (region) {
        2 -> stringResource(id = R.string.tool_news_cn)
        3 -> stringResource(id = R.string.tool_news_tw)
        else -> stringResource(id = R.string.tool_news_jp)
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(colorResource(id = R.color.bg_gray))
    ) {
        val data = news.collectAsLazyPagingItems()
        LazyColumn(state = state) {
            itemsIndexed(data) { _, it ->
                if (it != null) {
                    NewsItem(news = it)
                }
            }
        }
        //回到顶部
        ExtendedFabCompose(
            iconType = MainIconType.NEWS,
            text = title,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(end = Dimen.fabMarginEnd, bottom = Dimen.fabMargin)
        ) {
            coroutineScope.launch {
                state.scrollToItem(0)
            }
        }
    }


}


/**
 * 新闻公告
 */
@Composable
private fun NewsItem(news: NewsTable) {
    val context = LocalContext.current
    val tags = news.getTagList()
    if (news.getTagList().size > 1) tags.remove("お知らせ")
    val colorId = when (tags[0]) {
        "公告", "更新", "アップデート" -> R.color.news_update
        "系統", "メンテナンス" -> R.color.news_system
        else -> R.color.colorPrimary
    }
    val fTag = when (tags[0]) {
        "アップデート" -> "更新"
        "系統", "メンテナンス" -> "系统"
        "お知らせ" -> "新闻"
        "活動", "イベント" -> "活动"
        "グッズ" -> "周边"
        else -> tags[0]
    }

    Column(
        modifier = Modifier
            .padding(Dimen.mediuPadding)
            .fillMaxWidth()
    ) {
        //标题
        Row(modifier = Modifier.padding(bottom = Dimen.mediuPadding)) {
            MainTitleText(
                text = fTag,
                backgroundColor = colorResource(id = colorId)
            )
            MainTitleText(
                text = news.date,
                modifier = Modifier.padding(start = Dimen.smallPadding),
            )
        }
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .shadow(elevation = Dimen.cardElevation, shape = Shapes.large, clip = true)
                .clickable {
                    BrowserUtil.OpenWebView(context, news.url)
                }
        ) {
            Column(modifier = Modifier.padding(Dimen.mediuPadding)) {
                //内容
                MainContentText(
                    text = news.title,
                    modifier = Modifier.padding(
                        top = Dimen.smallPadding,
                        bottom = Dimen.smallPadding
                    ),
                    textAlign = TextAlign.Start
                )
            }
        }
    }
}

