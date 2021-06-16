package cn.wthee.pcrtool.ui.tool

import android.annotation.SuppressLint
import android.net.http.SslError
import android.view.ViewGroup
import android.webkit.*
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.viewinterop.AndroidView
import androidx.paging.ExperimentalPagingApi
import androidx.paging.compose.LazyPagingItems
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
import cn.wthee.pcrtool.utils.VibrateUtil
import cn.wthee.pcrtool.utils.openWebView
import com.google.accompanist.insets.navigationBarsPadding
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.pagerTabIndicatorOffset
import com.google.accompanist.pager.rememberPagerState
import kotlinx.coroutines.launch

/**
 * 公告列表
 */
@ExperimentalPagerApi
@ExperimentalAnimationApi
@ExperimentalMaterialApi
@ExperimentalPagingApi
@Composable
fun NewsList(
    news0: LazyPagingItems<NewsTable>?,
    news1: LazyPagingItems<NewsTable>?,
    news2: LazyPagingItems<NewsTable>?,
    toDetail: (String, String, Int, String) -> Unit
) {
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current
    val tab = arrayListOf(
        stringResource(id = R.string.db_cn),
        stringResource(id = R.string.db_tw),
        stringResource(id = R.string.db_jp)
    )
    val pagerState = rememberPagerState(pageCount = 3)
    val scrollState0 = rememberLazyListState()
    val scrollState1 = rememberLazyListState()
    val scrollState2 = rememberLazyListState()

    Box(
        modifier = Modifier
            .fillMaxSize()
    ) {
        HorizontalPager(state = pagerState, modifier = Modifier.fillMaxSize()) { pagerIndex ->
            val scrollState = when (pagerIndex) {
                0 -> scrollState0
                1 -> scrollState1
                else -> scrollState2
            }
            LazyColumn(
                state = scrollState,
                modifier = Modifier
                    .fillMaxSize(),
                contentPadding = PaddingValues(Dimen.largePadding)
            ) {
                val news = when (pagerIndex) {
                    0 -> news0
                    1 -> news1
                    else -> news2
                }
                news?.let { list ->
                    itemsIndexed(list) { _, it ->
                        if (it != null) {
                            if (navViewModel.loading.value == true) {
                                navViewModel.loading.postValue(false)
                            }
                            NewsItem(pagerIndex + 2, news = it, toDetail)
                        }
                    }
                }

            }
        }

        Card(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth(0.618f)
                .padding(bottom = Dimen.fabMargin)
                .navigationBarsPadding(),
            shape = CircleShape,
            elevation = Dimen.cardElevation,
        ) {
            TabRow(
                selectedTabIndex = pagerState.currentPage,
                backgroundColor = MaterialTheme.colors.background,
                contentColor = MaterialTheme.colors.primary,
                indicator = { tabPositions ->
                    TabRowDefaults.Indicator(
                        Modifier.pagerTabIndicatorOffset(pagerState, tabPositions)
                    )
                },
            ) {
                tab.forEachIndexed { index, s ->
                    Tab(
                        modifier = Modifier
                            .width(Dimen.fabSize)
                            .height(Dimen.fabSize),
                        selected = pagerState.currentPage == index,
                        onClick = {
                            VibrateUtil(context).single()
                            coroutineScope.launch {
                                if (pagerState.currentPage == index) {
                                    when (pagerState.currentPage) {
                                        0 -> scrollState0
                                        1 -> scrollState1
                                        else -> scrollState2
                                    }.scrollToItem(0)
                                } else {
                                    pagerState.scrollToPage(index)
                                }
                            }
                        }) {
                        Subtitle2(text = s)
                    }
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
            selectable = true
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
                .padding(Dimen.mediuPadding),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            MainText(
                text = originalTitle,
                modifier = Modifier.padding(Dimen.mediuPadding),
                selectable = true
            )
            Subtitle2(text = date)
            AndroidView(
                modifier = Modifier
                    .alpha(alpha)
                    .padding(
                        top = Dimen.mediuPadding,
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
