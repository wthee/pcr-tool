package cn.wthee.pcrtool.ui.tool

import android.annotation.SuppressLint
import android.net.http.SslError
import android.view.ViewGroup
import android.webkit.*
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SmallFloatingActionButton
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.viewinterop.AndroidView
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.flowWithLifecycle
import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadState
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.items
import cn.wthee.pcrtool.R
import cn.wthee.pcrtool.data.db.entity.NewsTable
import cn.wthee.pcrtool.data.db.entity.region
import cn.wthee.pcrtool.data.enums.MainIconType
import cn.wthee.pcrtool.database.getRegion
import cn.wthee.pcrtool.ui.MainActivity.Companion.navViewModel
import cn.wthee.pcrtool.ui.PreviewBox
import cn.wthee.pcrtool.ui.common.*
import cn.wthee.pcrtool.ui.theme.Dimen
import cn.wthee.pcrtool.ui.theme.defaultSpring
import cn.wthee.pcrtool.utils.ShareIntentUtil
import cn.wthee.pcrtool.utils.VibrateUtil
import cn.wthee.pcrtool.utils.formatTime
import cn.wthee.pcrtool.utils.openWebView
import cn.wthee.pcrtool.viewmodel.NewsViewModel
import com.google.accompanist.insets.navigationBarsPadding
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.placeholder.PlaceholderHighlight
import com.google.accompanist.placeholder.material.placeholder
import com.google.accompanist.placeholder.material.shimmer
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

/**
 * 公告列表
 */
@ExperimentalFoundationApi
@ExperimentalPagerApi
@ExperimentalMaterialApi
@ExperimentalPagingApi
@Composable
fun NewsList(
    scrollState: LazyListState,
    toDetail: (String) -> Unit,
    newsViewModel: NewsViewModel = hiltViewModel(),
) {
    val coroutineScope = rememberCoroutineScope()
    val openDialog = navViewModel.openChangeDataDialog.observeAsState().value ?: false
    val close = navViewModel.fabCloseClick.observeAsState().value ?: false
    val mainIcon = navViewModel.fabMainIcon.observeAsState().value ?: MainIconType.BACK
    //切换关闭监听
    if (close) {
        navViewModel.openChangeDataDialog.postValue(false)
        navViewModel.fabMainIcon.postValue(MainIconType.BACK)
        navViewModel.fabCloseClick.postValue(false)
    }
    if (mainIcon == MainIconType.BACK) {
        navViewModel.openChangeDataDialog.postValue(false)
    }

    val region = remember {
        mutableStateOf(getRegion())
    }

    val lifecycle = LocalLifecycleOwner.current.lifecycle
    newsViewModel.getNews(region.value)
    val flow = when (region.value) {
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
                items(news) {
                    if (it != null) {
                        if (navViewModel.loading.value == true) {
                            navViewModel.loading.postValue(false)
                        }
                        NewsItem(news = it, toDetail)
                    }
                }
                if (news.loadState.append is LoadState.Loading) {
                    item {
                        NewsItem(news = NewsTable(), toDetail)
                    }
                }
                item {
                    CommonSpacer()
                }
            }
        }

        //公告区服选择
        SelectNewsTypeCompose(
            region = region,
            openDialog = openDialog,
            coroutineScope = coroutineScope,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .navigationBarsPadding()
        )
    }
}

//公告区服选择
@Composable
private fun SelectNewsTypeCompose(
    region: MutableState<Int>,
    openDialog: Boolean,
    coroutineScope: CoroutineScope,
    modifier: Modifier
) {
    val context = LocalContext.current
    //区服
    val tabs = arrayListOf(
        stringResource(id = R.string.db_cn),
        stringResource(id = R.string.db_tw),
        stringResource(id = R.string.db_jp)
    )
    val sectionColor = MaterialTheme.colorScheme.primary

    //数据切换
    SmallFloatingActionButton(
        modifier = modifier
            .animateContentSize(defaultSpring())
            .padding(
                end = Dimen.fabMarginEnd,
                start = Dimen.fabMargin,
                top = Dimen.fabMargin,
                bottom = Dimen.fabMargin,
            ),
        containerColor = MaterialTheme.colorScheme.background,
        shape = if (openDialog) androidx.compose.material.MaterialTheme.shapes.medium else CircleShape,
        elevation = FloatingActionButtonDefaults.elevation(defaultElevation = Dimen.fabElevation),
        onClick = {
            VibrateUtil(context).single()
            if (!openDialog) {
                navViewModel.fabMainIcon.postValue(MainIconType.CLOSE)
                navViewModel.openChangeDataDialog.postValue(true)
            } else {
                navViewModel.fabCloseClick.postValue(true)
            }
        },
    ) {
        if (openDialog) {
            Column(
                modifier = Modifier.width(Dimen.dataChangeWidth),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                //区服选择
                tabs.forEachIndexed { index, tab ->
                    val mModifier = if (region.value == index + 2) {
                        Modifier.fillMaxWidth()
                    } else {
                        Modifier
                            .fillMaxWidth()
                            .clickable {
                                VibrateUtil(context).single()
                                navViewModel.openChangeDataDialog.postValue(false)
                                navViewModel.fabCloseClick.postValue(true)
                                if (region.value != index + 2) {
                                    coroutineScope.launch {
                                        region.value = index + 2
                                    }
                                }
                            }
                    }
                    SelectText(
                        selected = region.value == index + 2,
                        text = tab,
                        textStyle = MaterialTheme.typography.titleLarge,
                        selectedColor = sectionColor,
                        modifier = mModifier.padding(Dimen.mediumPadding)
                    )
                }
            }
        } else {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(start = Dimen.largePadding)
            ) {
                IconCompose(
                    data = MainIconType.NEWS.icon,
                    tint = sectionColor,
                    size = Dimen.menuIconSize
                )
                Text(
                    text = tabs[region.value - 2],
                    style = MaterialTheme.typography.titleSmall,
                    textAlign = TextAlign.Center,
                    color = sectionColor,
                    modifier = Modifier.padding(
                        start = Dimen.mediumPadding,
                        end = Dimen.largePadding
                    )
                )
            }

        }
    }
}

/**
 * 新闻公告
 */
@ExperimentalMaterialApi
@Composable
fun NewsItem(
    news: NewsTable,
    toDetail: (String) -> Unit,
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
                toDetail(news.id)
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
@ExperimentalPagingApi
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
                    iconType = MainIconType.BROWSER
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


}


@Preview
@ExperimentalMaterialApi
@Composable
private fun NewsItemPreview() {
    PreviewBox {
        Column {
            NewsItem(news = NewsTable(title = "?"), toDetail = {})
        }
    }
}