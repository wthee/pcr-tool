package cn.wthee.pcrtool.ui.tool

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.paging.LoadState
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemContentType
import androidx.paging.compose.itemKey
import cn.wthee.pcrtool.R
import cn.wthee.pcrtool.data.db.entity.TweetData
import cn.wthee.pcrtool.data.enums.KeywordType
import cn.wthee.pcrtool.data.enums.MainIconType
import cn.wthee.pcrtool.ui.components.BottomSearchBar
import cn.wthee.pcrtool.ui.components.CenterTipText
import cn.wthee.pcrtool.ui.components.CircularProgressCompose
import cn.wthee.pcrtool.ui.components.CommonSpacer
import cn.wthee.pcrtool.ui.components.DateRange
import cn.wthee.pcrtool.ui.components.DateRangePickerCompose
import cn.wthee.pcrtool.ui.components.IconTextButton
import cn.wthee.pcrtool.ui.components.MainCard
import cn.wthee.pcrtool.ui.components.MainContentText
import cn.wthee.pcrtool.ui.components.MainImage
import cn.wthee.pcrtool.ui.components.MainTitleText
import cn.wthee.pcrtool.ui.components.VerticalGrid
import cn.wthee.pcrtool.ui.theme.CombinedPreviews
import cn.wthee.pcrtool.ui.theme.Dimen
import cn.wthee.pcrtool.ui.theme.ExpandAnimation
import cn.wthee.pcrtool.ui.theme.FadeAnimation
import cn.wthee.pcrtool.ui.theme.PreviewLayout
import cn.wthee.pcrtool.utils.BrowserUtil
import cn.wthee.pcrtool.viewmodel.CommonApiViewModel
import cn.wthee.pcrtool.viewmodel.TweetViewModel
import java.util.regex.Pattern

/**
 * 推特列表
 */
@Composable
fun TweetList(
    tweetViewModel: TweetViewModel = hiltViewModel(),
    commonAPIViewModel: CommonApiViewModel = hiltViewModel(),
) {
    val scrollState = rememberLazyListState()
    //关键词输入
    val keywordInputState = remember {
        mutableStateOf("")
    }
    //关键词查询
    val keywordState = remember {
        mutableStateOf("")
    }
    //时间范围
    val dateRange = remember {
        mutableStateOf(DateRange())
    }
    //获取分页数据
    val tweetPager = remember(keywordState.value, dateRange.value) {
        tweetViewModel.getTweet(keywordState.value, dateRange.value)
    }
    val tweetItems = tweetPager.flow.collectAsLazyPagingItems()

    //获取关键词
    val keywordFlow = remember {
        commonAPIViewModel.getKeywords(KeywordType.TWEET)
    }
    val keywordList = keywordFlow.collectAsState(initial = arrayListOf()).value

    Box(modifier = Modifier.fillMaxSize()) {
        LazyColumn(state = scrollState) {
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

        //日期选择
        DateRangePickerCompose(dateRange = dateRange)

        //搜索栏
        BottomSearchBar(
            modifier = Modifier
                .align(Alignment.BottomEnd),
            labelStringId = R.string.tweet,
            keywordInputState = keywordInputState,
            keywordState = keywordState,
            leadingIcon = MainIconType.TWEET,
            scrollState = scrollState,
            defaultKeywordList = keywordList,
            onResetClick = {
                //同时重置时间筛选
                dateRange.value = DateRange()
            }
        )
    }
}


/**
 * 推特内容
 */
@Composable
private fun TweetItem(data: TweetData) {
    val photos = data.getImageList()


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


        MainCard {
            Column {

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

                //相关链接跳转
                IconTextButton(
                    icon = MainIconType.BROWSER,
                    text = stringResource(id = R.string.twitter),
                    modifier = Modifier
                        .padding(bottom = Dimen.smallPadding, end = Dimen.smallPadding)
                        .align(Alignment.End)
                ) {
                    BrowserUtil.open(data.link)
                }

                //图片
                if (photos.isNotEmpty()) {
                    if (photos.size > 1) {
                        VerticalGrid(fixCount = 3, contentPadding = Dimen.mediumPadding) {
                            photos.forEach {
                                MainImage(
                                    data = it,
                                    ratio = -1f,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(Dimen.tweetImgHeight),
                                    contentScale = ContentScale.Crop
                                )
                            }
                        }
                    } else {
                        MainImage(
                            data = photos[0],
                            ratio = -1f,
                            modifier = Modifier.fillMaxWidth(),
                            contentScale = ContentScale.Crop
                        )
                    }

                }
            }
        }

    }

}

private val urlPattern: Pattern = Pattern.compile(
    "(?:^|[\\W])((ht|f)tp(s?):\\/\\/|www\\.)"
            + "(([\\w\\-]+\\.){1,}?([\\w\\-.~]+\\/?)*"
            + "[\\p{Alnum}.,%_=?&#\\-+()\\[\\]\\*$~@!:/{};']*)",
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

