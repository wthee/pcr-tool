package cn.wthee.pcrtool.ui.tool

import android.annotation.SuppressLint
import android.net.http.SslError
import android.view.ViewGroup
import android.webkit.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.viewinterop.AndroidView
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.flowWithLifecycle
import androidx.paging.LoadState
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.items
import cn.wthee.pcrtool.R
import cn.wthee.pcrtool.data.db.entity.NewsTable
import cn.wthee.pcrtool.data.db.entity.region
import cn.wthee.pcrtool.data.enums.MainIconType
import cn.wthee.pcrtool.ui.MainActivity
import cn.wthee.pcrtool.ui.MainActivity.Companion.navViewModel
import cn.wthee.pcrtool.ui.PreviewBox
import cn.wthee.pcrtool.ui.common.*
import cn.wthee.pcrtool.ui.theme.Dimen
import cn.wthee.pcrtool.utils.BrowserUtil
import cn.wthee.pcrtool.utils.ShareIntentUtil
import cn.wthee.pcrtool.utils.formatTime
import cn.wthee.pcrtool.viewmodel.NewsViewModel
import com.google.accompanist.placeholder.PlaceholderHighlight
import com.google.accompanist.placeholder.material.placeholder
import com.google.accompanist.placeholder.material.shimmer

/**
 * 公告列表
 */
@Composable
fun NewsList(
    scrollState: LazyListState,
    toNewsDetail: (String) -> Unit,
    newsViewModel: NewsViewModel = hiltViewModel(),
) {
    //区服
    val tabs = arrayListOf(
        stringResource(id = R.string.db_cn),
        stringResource(id = R.string.db_tw),
        stringResource(id = R.string.db_jp)
    )

    val type = remember {
        mutableStateOf(MainActivity.regionType - 2)
    }

    val lifecycle = LocalLifecycleOwner.current.lifecycle
    newsViewModel.getNews(type.value + 2)
    val flow = when (type.value + 2) {
        2 -> newsViewModel.newsPageList0
        3 -> newsViewModel.newsPageList1
        else -> newsViewModel.newsPageList2
    }
    val news = remember(flow, lifecycle) {
        flow?.flowWithLifecycle(lifecycle = lifecycle)
    }?.collectAsLazyPagingItems()


    Box(modifier = Modifier.fillMaxSize()) {
        LazyColumn(state = scrollState) {
            if (news != null && news.itemCount > 0) {
                items(
                    items = news,
                    key = {
                        it.id
                    }
                ) {
                    if (it != null) {
                        if (navViewModel.loading.value == true) {
                            navViewModel.loading.postValue(false)
                        }
                        NewsItem(news = it, toNewsDetail)
                    }
                }
                if (news.loadState.append is LoadState.Loading) {
                    item {
                        NewsItem(news = NewsTable(), toNewsDetail)
                    }
                }
                item {
                    CommonSpacer()
                }
            }
        }

        //公告区服选择
        SelectTypeCompose(
            icon = MainIconType.NEWS,
            tabs = tabs,
            type = type,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .navigationBarsPadding()
        )
    }
}


/**
 * 新闻公告
 */
@Composable
fun NewsItem(
    news: NewsTable,
    toNewsDetail: (String) -> Unit,
) {
    val placeholder = news.title == ""
    val tag = news.getTag()
    val color = when (tag) {
        "公告", "更新" -> colorResource(R.color.news_update)
        "系統" -> colorResource(R.color.news_system)
        else -> MaterialTheme.colorScheme.primary
    }
    Column(
        modifier = Modifier.padding(horizontal = Dimen.largePadding, vertical = Dimen.mediumPadding)
    ) {
        //标题
        Row(modifier = Modifier.padding(bottom = Dimen.mediumPadding)) {
            MainTitleText(
                text = tag,
                backgroundColor = color,
                modifier = Modifier.placeholder(
                    visible = placeholder,
                    highlight = PlaceholderHighlight.shimmer()
                )
            )
            MainTitleText(
                text = news.date.formatTime,
                modifier = Modifier
                    .padding(start = Dimen.smallPadding)
                    .placeholder(
                        visible = placeholder,
                        highlight = PlaceholderHighlight.shimmer()
                    ),
            )
        }
        MainCard(modifier = Modifier
            .placeholder(
                visible = placeholder,
                highlight = PlaceholderHighlight.shimmer()
            )
            .heightIn(min = Dimen.cardHeight),
            onClick = {
                toNewsDetail(news.id)
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

/**
 * 公告详情
 */
@SuppressLint("SetJavaScriptEnabled")
@Composable
fun NewsDetail(key: String, newsViewModel: NewsViewModel = hiltViewModel()) {
    val loading = remember {
        mutableStateOf(true)
    }
    val alpha = if (loading.value) 0f else 1f
    navViewModel.loading.postValue(loading.value)
    val id = if (key.indexOf('-') != -1) {
        key.split("-")[1]
    } else {
        key
    }
    newsViewModel.getNewsDetail(id)
    val news = newsViewModel.newsDetail.observeAsState()
    val context = LocalContext.current

    Box(modifier = Modifier.fillMaxSize()) {
        if (news.value != null && news.value!!.data != null) {
            val originalUrl = news.value!!.data!!.url
            val originalTitle = news.value!!.data!!.title
            val date = news.value!!.data!!.date
            val region = originalUrl.region

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(Dimen.mediumPadding),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                MainText(
                    text = originalTitle,
                    modifier = Modifier.padding(Dimen.mediumPadding),
                    selectable = true
                )
                Subtitle2(text = date)
                AndroidView(
                    modifier = Modifier
                        .alpha(alpha)
                        .padding(
                            top = Dimen.mediumPadding,
                            start = Dimen.largePadding,
                            end = Dimen.largePadding
                        ),
                    factory = {
                        WebView(it).apply {
                            layoutParams = ViewGroup.LayoutParams(
                                ViewGroup.LayoutParams.MATCH_PARENT,
                                ViewGroup.LayoutParams.MATCH_PARENT,
                            )
                            settings.apply {
                                domStorageEnabled = true
                                javaScriptEnabled = true
                                cacheMode = WebSettings.LOAD_CACHE_ELSE_NETWORK
                                useWideViewPort = true //将图片调整到适合webView的大小
                                loadWithOverviewMode = true // 缩放至屏幕的大小
                                javaScriptCanOpenWindowsAutomatically = true
                                loadsImagesAutomatically = false
                                blockNetworkImage = true
                            }
                            webChromeClient = WebChromeClient()
                            webViewClient = object : WebViewClient() {

                                override fun shouldOverrideUrlLoading(
                                    view: WebView?,
                                    request: WebResourceRequest?
                                ): Boolean {
                                    view?.loadUrl(url!!)
                                    return true
                                }

                                override fun onReceivedSslError(
                                    view: WebView?,
                                    handler: SslErrorHandler?,
                                    error: SslError?
                                ) {
                                    handler?.proceed()
                                }

                                override fun onPageFinished(view: WebView?, url: String?) {
                                    super.onPageFinished(view, url)
                                    settings.apply {
                                        loadsImagesAutomatically = true
                                        blockNetworkImage = false
                                    }
                                    if (region == 2) {
                                        //取消内部滑动
                                        loadUrl(
                                            """
                                                javascript:
                                                $('#news-content').css('overflow','inherit');
                                                $('#news-content').css('margin-top','0');
                                                $('.news-detail').css('top','0');
                                                $('.news-detail').css('padding','0');
                                                $('.top').css('display','none');
                                                $('.header').css('display','none');
                                                $('.header').css('visibility','hidden');
                                                $('#news-content').css('margin-bottom','1rem');
                                            """.trimIndent()
                                        )
                                    }
                                    if (region == 3) {
                                        loadUrl(
                                            """
                                                javascript:
                                                $('.menu').css('display','none');
                                                $('.story_container_m').css('display','none');                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                     
                                                $('.title').css('display','none');
                                                $('header').css('display','none');
                                                $('footer').css('display','none');
                                                $('aside').css('display','none');
                                                $('#pageTop').css('display','none');
                                                $('h2').css('display','none');
                                                $('h3').css('display','none');
                                                $('.paging').css('display','none');
                                                $('.news_con').css('margin','0px');
                                                $('.news_con').css('padding','0px');
                                                $('section').css('padding','0px');
                                                $('body').css('background-image','none');
                                                $('.news_con').css('box-shadow','none');
                                            """.trimIndent()
                                        )
                                    }
                                    if (region == 4) {
                                        loadUrl(
                                            """
                                                javascript:
                                                $('#main_area').css('display','none');
                                                $('.bg-gray').css('display','none');
                                                $('.news_prev').css('display','none');
                                                $('.news_next').css('display','none');
                                                $('time').css('display','none');
                                                $('.post-categories').css('display','none');
                                                $('h3').css('display','none');
                                                $('header').css('display','none');
                                                $('.news_detail').css('box-shadow','none');
                                                $('footer').css('display','none');
                                                $('hr').css('display','none');
                                                $('#page').css('background-image','none');
                                                $('.news_detail_container').css('background-image','none');
                                                $('.news_detail').css('padding','0');
                                                $('.news_detail_container').css('width','100%');
                                                $('.meta-info').css('margin','0');
                                            """.trimIndent()
                                        )
                                    }
                                    loading.value = false
                                }
                            }
                            //加载网页
                            loadUrl(originalUrl)
                        }
                    }
                )
            }
            Row(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(end = Dimen.fabMarginEnd, bottom = Dimen.fabMargin)
            ) {
                //浏览器打开
                FabCompose(
                    iconType = MainIconType.BROWSER
                ) {
                    BrowserUtil.open(context, originalUrl)
                }
                //分享
                FabCompose(
                    iconType = MainIconType.SHARE,
                    text = stringResource(id = R.string.share),
                ) {
                    ShareIntentUtil.text(originalTitle + "\n" + originalUrl)
                }
            }

        }

    }
}


@Preview
@Composable
private fun NewsItemPreview() {
    PreviewBox {
        Column {
            NewsItem(news = NewsTable(title = "?"), toNewsDetail = {})
        }
    }
}