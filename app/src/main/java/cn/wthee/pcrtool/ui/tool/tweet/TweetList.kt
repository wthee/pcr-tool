package cn.wthee.pcrtool.ui.tool.tweet

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.rememberLazyStaggeredGridState
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.rememberDateRangePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.paging.LoadState
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemContentType
import androidx.paging.compose.itemKey
import cn.wthee.pcrtool.R
import cn.wthee.pcrtool.data.db.entity.TweetData
import cn.wthee.pcrtool.data.enums.MainIconType
import cn.wthee.pcrtool.navigation.navigateUp
import cn.wthee.pcrtool.ui.components.BottomSearchBar
import cn.wthee.pcrtool.ui.components.CenterTipText
import cn.wthee.pcrtool.ui.components.CircularProgressCompose
import cn.wthee.pcrtool.ui.components.CommonSpacer
import cn.wthee.pcrtool.ui.components.DateRangePickerCompose
import cn.wthee.pcrtool.ui.components.IconTextButton
import cn.wthee.pcrtool.ui.components.MainCard
import cn.wthee.pcrtool.ui.components.MainContentText
import cn.wthee.pcrtool.ui.components.MainScaffold
import cn.wthee.pcrtool.ui.components.MainTitleText
import cn.wthee.pcrtool.ui.components.VerticalStaggeredGrid
import cn.wthee.pcrtool.ui.components.getDatePickerYearRange
import cn.wthee.pcrtool.ui.components.getItemWidth
import cn.wthee.pcrtool.ui.media.PictureItem
import cn.wthee.pcrtool.ui.theme.CombinedPreviews
import cn.wthee.pcrtool.ui.theme.Dimen
import cn.wthee.pcrtool.ui.theme.ExpandAnimation
import cn.wthee.pcrtool.ui.theme.FadeAnimation
import cn.wthee.pcrtool.ui.theme.PreviewLayout
import cn.wthee.pcrtool.ui.theme.noShape
import cn.wthee.pcrtool.utils.BrowserUtil
import cn.wthee.pcrtool.utils.VibrateUtil
import java.util.regex.Pattern
import kotlin.math.min

/**
 * 推特列表
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TweetList(
    tweetViewModel: TweetViewModel = hiltViewModel()
) {
    val scrollState = rememberLazyStaggeredGridState()
    val uiState by tweetViewModel.uiState.collectAsStateWithLifecycle()
    val dateRangePickerState = rememberDateRangePickerState(yearRange = getDatePickerYearRange())


    //获取分页数据
    uiState.pager?.let { pager ->
        val tweetItems = pager.flow.collectAsLazyPagingItems()

        MainScaffold(
            enableClickClose = uiState.openDialog || uiState.openSearch,
            onCloseClick = {
                tweetViewModel.changeDialog(false)
                tweetViewModel.changeSearchBar(false)
            },
            secondLineFab = {
                //日期选择
                if (!uiState.openSearch) {
                    DateRangePickerCompose(
                        dateRangePickerState = dateRangePickerState,
                        dateRange = uiState.dateRange,
                        openDialog = uiState.openDialog,
                        changeRange = tweetViewModel::changeRange,
                        changeDialog = tweetViewModel::changeDialog
                    )
                }
            },
            fabWithCustomPadding = {
                //搜索栏
                BottomSearchBar(
                    labelStringId = R.string.tweet,
                    keyword = uiState.keyword,
                    openSearch = uiState.openSearch,
                    leadingIcon = MainIconType.TWEET,
                    defaultKeywordList = uiState.keywordList,
                    showReset = uiState.dateRange.hasFilter() || uiState.keyword != "",
                    changeSearchBar = tweetViewModel::changeSearchBar,
                    changeKeyword = tweetViewModel::changeKeyword,
                    onTopClick = {
                        scrollState.scrollToItem(0)
                    },
                    onResetClick = {
                        tweetViewModel.reset()
                        dateRangePickerState.setSelection(null, null)
                    }
                )
            },
            mainFabIcon = if (uiState.openDialog || uiState.openSearch) MainIconType.CLOSE else MainIconType.BACK,
            onMainFabClick = {
                if (uiState.openDialog || uiState.openSearch) {
                    tweetViewModel.changeDialog(false)
                    tweetViewModel.changeSearchBar(false)
                } else {
                    navigateUp()
                }
            }
        ) {
            LazyVerticalStaggeredGrid(
                state = scrollState,
                columns = StaggeredGridCells.Adaptive(getItemWidth())
            ) {
                //头部加载中提示
                item {
                    ExpandAnimation(tweetItems.loadState.refresh == LoadState.Loading) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(Dimen.largePadding)
                        ) {
                            CircularProgressCompose(Modifier.align(Alignment.Center))
                        }
                    }
                }
                items(
                    count = tweetItems.itemCount,
                    key = tweetItems.itemKey(
                        key = {
                            it.id
                        }
                    ),
                    contentType = tweetItems.itemContentType()
                ) { index ->
                    val item = tweetItems[index]
                    TweetItem(item ?: TweetData())
                }
                //暂无更多提示
                if (tweetItems.loadState.refresh != LoadState.Loading) {
                    item {
                        FadeAnimation(tweetItems.loadState.append is LoadState.NotLoading) {
                            CenterTipText(stringResource(id = R.string.no_more))
                        }
                    }
                }
                items(2) {
                    CommonSpacer()
                }
            }
        }
    }

}


/**
 * 推特内容
 */
@Composable
private fun TweetItem(data: TweetData) {
    val photos = data.getImageList()
    val context = LocalContext.current

    Column(
        modifier = Modifier.padding(
            horizontal = Dimen.largePadding,
            vertical = Dimen.mediumPadding
        )
    ) {
        Row(
            modifier = Modifier
                .padding(bottom = Dimen.mediumPadding)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            MainTitleText(text = data.date)
        }


        MainCard(
            onClick = {
                BrowserUtil.open(data.link)
            }
        ) {
            //来源
            val jpInfoUrl = stringResource(id = R.string.jp_info_url)
            IconTextButton(
                text = "@" + stringResource(id = R.string.title_jp_info),
                contentColor = MaterialTheme.colorScheme.primary
            ) {
                BrowserUtil.open(jpInfoUrl)
            }

            //文本
            if (data.tweet.contains("http")) {
                val annotatedLinkString: AnnotatedString = buildAnnotatedString {
                    val str = data.getFormatTweet()
                    val urlIndexList = clickableLink(str)

                    urlIndexList.forEachIndexed { i, it ->
                        val startIndex = it.first
                        val endIndex = it.second
                        //多个url时 只添加一次
                        if (i == 0) {
                            append(str)
                        }
                        addStyle(
                            style = SpanStyle(
                                color = MaterialTheme.colorScheme.primary,
                                fontSize = 16.sp,
                                textDecoration = TextDecoration.Underline
                            ),
                            start = startIndex,
                            end = endIndex
                        )

                        addStringAnnotation(
                            tag = "URL",
                            annotation = str.substring(startIndex, endIndex),
                            start = startIndex,
                            end = endIndex
                        )
                    }
                }

                ClickableText(
                    modifier = Modifier.padding(
                        start = Dimen.smallPadding,
                        end = Dimen.smallPadding,
                        bottom = Dimen.mediumPadding,
                    ),
                    text = annotatedLinkString,
                    onClick = {
                        annotatedLinkString
                            .getStringAnnotations("URL", it, it)
                            .firstOrNull()?.let { stringAnnotation ->
                                VibrateUtil(context).single()
                                BrowserUtil.open(stringAnnotation.item)
                            }
                    }
                )
            } else {
                MainContentText(
                    text = data.getFormatTweet(),
                    textAlign = TextAlign.Start,
                    selectable = true,
                    modifier = Modifier.padding(
                        start = Dimen.smallPadding,
                        end = Dimen.smallPadding,
                        bottom = Dimen.mediumPadding,
                    ),
                )
            }

            //图片
            if (photos.isNotEmpty()) {
                if (photos.size > 1) {
                    VerticalStaggeredGrid(fixCount = min(photos.size, 3)) {
                        photos.forEach {
                            Box(
                                modifier = Modifier.aspectRatio(1f)
                            ) {
                                PictureItem(
                                    modifier = Modifier.fillMaxWidth(),
                                    picUrl = it,
                                    shape = noShape(),
                                    contentScale = ContentScale.Crop
                                )
                            }
                        }
                    }
                } else {
                    PictureItem(
                        modifier = Modifier.fillMaxWidth(),
                        picUrl = photos[0],
                        shape = noShape()
                    )
                }

            }
        }

    }

}

private val urlPattern: Pattern = Pattern.compile(
    "(?:^|\\W)((ht|f)tp(s?)://|www\\.)"
            + "(([\\w\\-]+\\.)+([\\w\\-.~]+/?)*"
            + "[\\p{Alnum}.,%_=?&#\\-+()\\[\\]*$~@!:/{};']*)",
    Pattern.CASE_INSENSITIVE or Pattern.MULTILINE or Pattern.DOTALL
)

private fun clickableLink(longText: String): ArrayList<Pair<Int, Int>> {
    val urlList = arrayListOf<Pair<Int, Int>>()
    try {
        val matcher = urlPattern.matcher(longText)
        var matchStart: Int
        var matchEnd: Int

        while (matcher.find()) {
            matchStart = matcher.start(1)
            matchEnd = matcher.end()
            urlList.add(Pair(matchStart, matchEnd))
        }
    } catch (e: Exception) {
        e.printStackTrace()
    }
    return urlList
}

@CombinedPreviews
@Composable
private fun TweetItemPreview() {
    PreviewLayout {
        TweetItem(data = TweetData(id = 1, tweet = "???"))
    }
}

