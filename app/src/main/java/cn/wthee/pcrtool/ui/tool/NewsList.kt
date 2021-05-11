package cn.wthee.pcrtool.ui.tool

import android.annotation.SuppressLint
import android.net.http.SslError
import android.view.ViewGroup
import android.webkit.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.Card
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.viewinterop.AndroidView
import androidx.hilt.navigation.compose.hiltNavGraphViewModel
import androidx.paging.ExperimentalPagingApi
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemsIndexed
import cn.wthee.pcrtool.R
import cn.wthee.pcrtool.data.db.entity.NewsTable
import cn.wthee.pcrtool.data.db.entity.fix
import cn.wthee.pcrtool.data.db.entity.original
import cn.wthee.pcrtool.data.enums.MainIconType
import cn.wthee.pcrtool.ui.MainActivity.Companion.navViewModel
import cn.wthee.pcrtool.ui.compose.ExtendedFabCompose
import cn.wthee.pcrtool.ui.compose.MainText
import cn.wthee.pcrtool.ui.compose.MainTitleText
import cn.wthee.pcrtool.ui.compose.PagerIndicator
import cn.wthee.pcrtool.ui.theme.Dimen
import cn.wthee.pcrtool.ui.theme.Shapes
import cn.wthee.pcrtool.viewmodel.NewsViewModel
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.rememberPagerState
import kotlinx.coroutines.launch

/**
 * 公告列表
 */
@ExperimentalMaterialApi
@ExperimentalPagingApi
@ExperimentalPagerApi
@Composable
fun NewsList(
    toDetail: (String, String, Int) -> Unit,
    viewModel: NewsViewModel = hiltNavGraphViewModel()
) {
    val coroutineScope = rememberCoroutineScope()
    val states = listOf(rememberLazyListState(), rememberLazyListState(), rememberLazyListState())
    val pagerState = rememberPagerState(pageCount = 3, initialOffscreenLimit = 2)
    val newsPagerData = listOf(
        viewModel.getNewsCN().collectAsLazyPagingItems(),
        viewModel.getNewsTW().collectAsLazyPagingItems(),
        viewModel.getNewsJP().collectAsLazyPagingItems(),
    )
    val regions = listOf(2, 3, 4)
    val tabs = listOf(
        stringResource(id = R.string.tool_news_cn),
        stringResource(id = R.string.tool_news_tw),
        stringResource(id = R.string.tool_news_jp),
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(colorResource(id = R.color.bg_gray))
    ) {
        //fixme 闪动问题
        HorizontalPager(state = pagerState, modifier = Modifier.fillMaxSize()) { index ->
            LazyColumn(state = states[index]) {
                itemsIndexed(newsPagerData[index]) { _, it ->
                    if (it != null) {
                        NewsItem(regions[index], news = it, toDetail)
                    }
                }
            }
        }
        PagerIndicator(pagerState = pagerState, modifier = Modifier.align(Alignment.BottomCenter))

        //回到顶部
        ExtendedFabCompose(
            iconType = MainIconType.NEWS,
            text = tabs[pagerState.currentPage],
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(end = Dimen.fabMarginEnd, bottom = Dimen.fabMargin)
        ) {
            coroutineScope.launch {
                states[pagerState.currentPage].scrollToItem(0)
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
    toDetail: (String, String, Int) -> Unit
) {

    val tag = news.getTag()
    val colorId = when (tag) {
        "公告" -> R.color.news_update
        "系統" -> R.color.news_system
        else -> R.color.colorPrimary
    }

    Column(
        modifier = Modifier
            .padding(Dimen.mediuPadding)
            .fillMaxWidth()
    ) {
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
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = Dimen.newsCardHeight)
                .shadow(elevation = Dimen.cardElevation, shape = Shapes.large, clip = true)
                .clickable {
                    toDetail(news.title.fix(), news.url.fix(), region)
                }
        ) {
            Column(modifier = Modifier.padding(Dimen.mediuPadding)) {
                //内容
                Text(
                    text = news.title,
                    modifier = Modifier.padding(
                        top = Dimen.smallPadding,
                        bottom = Dimen.smallPadding
                    ),
                    textAlign = TextAlign.Start,
                    style = MaterialTheme.typography.subtitle1,
                )
            }
        }
    }
}

@SuppressLint("SetJavaScriptEnabled")
@Composable
fun NewsDetail(text: String, url: String, region: Int) {
    val originalUrl = url.original()
    val originalTitle = text.original()
    val loading = remember {
        mutableStateOf(true)
    }
    val alpha = if (loading.value) 0f else 1f
    navViewModel.loading.postValue(loading.value)

    Column(modifier = Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
        MainText(text = originalTitle, modifier = Modifier.padding(Dimen.mediuPadding))
        AndroidView(modifier = Modifier.alpha(alpha), factory = {
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

                    override fun shouldOverrideUrlLoading(view: WebView, url: String?): Boolean {
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
                                $('.news-detail').css('top','0.3rem');
                                $('.top').css('display','none');
                                $('.title').css('visibility','hidden');
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
                                $('h3').css('display','none');
                                $('.paging').css('display','none');
                                $('.news_con').css('margin','0px');
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
                                $('h3').css('display','none');
                                $('header').css('display','none');
                                $('footer').css('display','none');
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
    }
}

