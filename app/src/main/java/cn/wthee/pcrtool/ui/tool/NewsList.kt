package cn.wthee.pcrtool.ui.tool

import android.annotation.SuppressLint
import android.net.http.SslError
import android.view.ViewGroup
import android.webkit.*
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.viewinterop.AndroidView
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.flowWithLifecycle
import androidx.paging.ExperimentalPagingApi
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemsIndexed
import cn.wthee.pcrtool.R
import cn.wthee.pcrtool.data.db.entity.NewsTable
import cn.wthee.pcrtool.data.db.entity.fix
import cn.wthee.pcrtool.data.db.entity.original
import cn.wthee.pcrtool.data.enums.MainIconType
import cn.wthee.pcrtool.ui.MainActivity.Companion.navViewModel
import cn.wthee.pcrtool.ui.compose.*
import cn.wthee.pcrtool.ui.theme.Dimen
import cn.wthee.pcrtool.utils.ShareIntentUtil
import cn.wthee.pcrtool.utils.openWebView
import cn.wthee.pcrtool.viewmodel.NewsViewModel
import kotlinx.coroutines.launch

/**
 * 公告列表
 */
@ExperimentalAnimationApi
@ExperimentalMaterialApi
@ExperimentalPagingApi
@Composable
fun NewsList(
    scrollState: LazyListState,
    region: Int,
    toDetail: (String, String, Int, String) -> Unit,
    viewModel: NewsViewModel = hiltViewModel()
) {
    val lifecycle = LocalLifecycleOwner.current.lifecycle
    val coroutineScope = rememberCoroutineScope()
    val tab = when (region) {
        2 -> stringResource(id = R.string.tool_news_cn)
        3 -> stringResource(id = R.string.tool_news_tw)
        else -> stringResource(id = R.string.tool_news_jp)
    }
    viewModel.getNews(region)
    val flow = viewModel.newsPageList
    if (flow != null) {
        val news = remember(flow, lifecycle) {
            flow.flowWithLifecycle(lifecycle = lifecycle)
        }.collectAsLazyPagingItems()
        navViewModel.loading.postValue(true)

        Box(
            modifier = Modifier
                .fillMaxSize()
        ) {
            LazyColumn(
                state = scrollState,
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .fillMaxSize(),
                contentPadding = PaddingValues(Dimen.largePadding)
            ) {
                itemsIndexed(news) { _, it ->
                    if (it != null) {
                        if (navViewModel.loading.value == true) {
                            navViewModel.loading.postValue(false)
                        }
                        NewsItem(region, news = it, toDetail)
                    }
                }
            }
            FabCompose(
                iconType = MainIconType.NEWS,
                text = tab,
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(
                        end = Dimen.fabMarginEnd,
                        bottom = Dimen.fabMargin
                    )
            ) {
                coroutineScope.launch {
                    scrollState.scrollToItem(0)
                }
            }
        }
    }
}


/**
 * 新闻公告
 */
@ExperimentalMaterialApi
@Composable
private fun NewsItem(
    region: Int,
    news: NewsTable,
    toDetail: (String, String, Int, String) -> Unit,
) {
    val tag = news.getTag()
    val colorId = when (tag) {
        "公告", "更新" -> R.color.news_update
        "系統" -> R.color.news_system
        else -> R.color.colorPrimary
    }
    //标题
    Row(modifier = Modifier.padding(bottom = Dimen.mediuPadding)) {
        MainTitleText(
            text = tag,
            backgroundColor = colorResource(id = colorId)
        )
        MainTitleText(
            text = news.date,
            modifier = Modifier.padding(start = Dimen.smallPadding),
        )
    }
    MainCard(modifier = Modifier.padding(bottom = Dimen.largePadding), onClick = {
        toDetail(news.title.fix(), news.url.fix(), region, news.date)
    }) {
        //内容
        Subtitle1(
            text = news.title,
            modifier = Modifier.padding(Dimen.mediuPadding),
        )
    }
}

@ExperimentalAnimationApi
@SuppressLint("SetJavaScriptEnabled")
@Composable
fun NewsDetail(text: String, url: String, region: Int, date: String) {
    val originalUrl = url.original()
    val originalTitle = text.original()
    val loading = remember {
        mutableStateOf(true)
    }
    val alpha = if (loading.value) 0f else 1f
    navViewModel.loading.postValue(loading.value)
    val context = LocalContext.current

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    top = Dimen.mediuPadding,
                    start = Dimen.mediuPadding,
                    end = Dimen.mediuPadding
                ),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            MainText(text = originalTitle, modifier = Modifier.padding(Dimen.mediuPadding))
            Subtitle2(text = date)
            AndroidView(modifier = Modifier
                .alpha(alpha)
                .padding(Dimen.largePadding), factory = {
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
                            view: WebView,
                            url: String?
                        ): Boolean {
                            view.loadUrl(url!!)
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
            })
            CommonSpacer()
        }
        Row(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(end = Dimen.fabMarginEnd, bottom = Dimen.fabMargin)
        ) {
            //浏览器打开
            FabCompose(
                iconType = MainIconType.BROWSER,
                modifier = Modifier.padding(end = Dimen.fabSmallMarginEnd)
            ) {
                openWebView(context, originalUrl)
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
