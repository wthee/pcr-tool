package cn.wthee.pcrtool.ui.tool.comic

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.paging.ItemSnapshotList
import androidx.paging.LoadState
import androidx.paging.compose.collectAsLazyPagingItems
import cn.wthee.pcrtool.R
import cn.wthee.pcrtool.data.db.entity.ComicData
import cn.wthee.pcrtool.data.enums.MainIconType
import cn.wthee.pcrtool.navigation.navigateUp
import cn.wthee.pcrtool.ui.components.CommonSpacer
import cn.wthee.pcrtool.ui.components.ExpandableFab
import cn.wthee.pcrtool.ui.components.MainInputText
import cn.wthee.pcrtool.ui.components.MainScaffold
import cn.wthee.pcrtool.ui.components.MainSmallFab
import cn.wthee.pcrtool.ui.components.RATIO_COMIC
import cn.wthee.pcrtool.ui.components.SelectText
import cn.wthee.pcrtool.ui.components.Subtitle1
import cn.wthee.pcrtool.ui.components.Subtitle2
import cn.wthee.pcrtool.ui.media.PictureItem
import cn.wthee.pcrtool.ui.theme.CombinedPreviews
import cn.wthee.pcrtool.ui.theme.Dimen
import cn.wthee.pcrtool.ui.theme.PreviewLayout
import cn.wthee.pcrtool.ui.theme.noShape
import kotlinx.coroutines.launch

/**
 * 漫画列表
 */
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ComicListScreen(
    comicListViewModel: ComicListViewModel = hiltViewModel()
) {
    val scrollState = rememberLazyListState()
    val scope = rememberCoroutineScope()
    val uiState by comicListViewModel.uiState.collectAsStateWithLifecycle()


    uiState.pager?.let { pager ->
        val lazyPagingItems = pager.flow.collectAsLazyPagingItems()
        val items = lazyPagingItems.itemSnapshotList
        //目录
        val count = lazyPagingItems.itemCount

        //分页状态
        val pagerState = rememberPagerState { count }

        //同步滚动目录位置
        LaunchedEffect(uiState.selectedIndex) {
            pagerState.scrollToPage(uiState.selectedIndex)
            scrollState.scrollToItem(uiState.selectedIndex)
        }


        MainScaffold(
            secondLineFab = {
                //目录
                if (count > 0) {
                    ComicIndexChange(
                        items = items,
                        pagerState = pagerState,
                        scrollState = scrollState,
                        openDialog = uiState.openDialog,
                        changeDialog = comicListViewModel::changeDialog,
                        changeSelect = comicListViewModel::changeSelect,
                    )
                }
            },
            fab = {
                //回到顶部
                MainSmallFab(
                    iconType = MainIconType.COMIC,
                    text = count.toString(),
                    onClick = {
                        scope.launch {
                            try {
                                pagerState.scrollToPage(0)
                            } catch (_: Exception) {
                            }
                        }
                    },
                    loading = lazyPagingItems.loadState.refresh == LoadState.Loading
                )
            },
            mainFabIcon = if (uiState.openDialog) MainIconType.CLOSE else MainIconType.BACK,
            onMainFabClick = {
                if (uiState.openDialog) {
                    comicListViewModel.changeDialog(false)
                } else {
                    navigateUp()
                }
            },
            enableClickClose = uiState.openDialog,
            onCloseClick = {
                comicListViewModel.changeDialog(false)
            }
        ) {
            HorizontalPager(
                modifier = Modifier.align(Alignment.Center),
                state = pagerState,
                key = {
                    if (items.isEmpty()) {
                        it
                    } else {
                        items[it]?.id ?: it
                    }
                }
            ) { pagerIndex ->
                items[pagerIndex]?.let { ComicItem(it) }
            }
        }
    }

}


/**
 * 漫画内容
 */
@Composable
private fun ComicItem(data: ComicData) {

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier
            .fillMaxSize()
            .padding(Dimen.largePadding)
            .verticalScroll(rememberScrollState())

    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Subtitle2(
                text = stringResource(
                    id = R.string.comic_toc_index,
                    data.id
                )
            )
            Subtitle1(
                text = data.title,
                selectable = true,
                textAlign = TextAlign.Center
            )
        }

        PictureItem(
            picUrl = data.url,
            ratio = RATIO_COMIC,
            contentScale = ContentScale.FillHeight,
            shape = noShape()
        )
        CommonSpacer()
        CommonSpacer()
    }

}

/**
 * 漫画目录
 */
@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun ComicIndexChange(
    scrollState: LazyListState,
    items: ItemSnapshotList<ComicData>,
    pagerState: PagerState,
    openDialog: Boolean,
    changeDialog: (Boolean) -> Unit,
    changeSelect: ((Int) -> Unit)
) {

    //切换
    ExpandableFab(
        expanded = openDialog,
        onClick = {
            changeDialog(true)
        },
        icon = MainIconType.COMIC_NAV,
        text = stringResource(id = R.string.comic_toc),
        isSecondLineFab = true
    ) {
        //展开目录
        ComicTocList(
            scrollState = scrollState,
            items = items,
            pagerState = pagerState,
            changeSelect = changeSelect,
            changeDialog = changeDialog,
        )
    }
}

@OptIn(
    ExperimentalFoundationApi::class
)
@Composable
private fun ComicTocList(
    scrollState: LazyListState,
    items: ItemSnapshotList<ComicData>,
    pagerState: PagerState,
    changeSelect: ((Int) -> Unit),
    changeDialog: (Boolean) -> Unit,
) {
    val tabs = arrayListOf<String>()
    if (items.size > 0) {
        for (i in 0 until items.size) {
            val title = if (items[i] == null) {
                "${items.size - i}"
            } else {
                val titleIndex = items[i]!!.id
                val titleText = items[i]!!.title
                val zh = if (items[i]!!.url.contains("_zh")) {
                    stringResource(R.string.to_zh)
                } else {
                    stringResource(R.string.none)
                }
                "$titleIndex$zh $titleText"

            }
            tabs.add(title)
        }
    } else {
        tabs.add("")
    }

    val pageCount = tabs.size
    val input = remember {
        mutableStateOf("")
    }


    Column {
        //目录
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .padding(Dimen.smallPadding),
            state = scrollState,
        ) {
            itemsIndexed(tabs) { index, tab ->
                SelectText(
                    selected = pagerState.currentPage == index,
                    text = tab,
                    textStyle = MaterialTheme.typography.titleMedium,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(Dimen.mediumPadding),
                    textAlign = TextAlign.Start,
                    onClick = {
                        changeSelect(index)
                        changeDialog(false)
                    }
                )
            }
        }

        //目录输入框
        MainInputText(
            modifier = Modifier.padding(Dimen.smallPadding),
            textState = input,
            onDone = {
                if (input.value != "") {
                    changeSelect(pageCount - input.value.toInt())
                    changeDialog(false)
                }
            },
            placeholder = stringResource(id = R.string.comic_input_hint),
            hasImePadding = true
        )
    }

}


@CombinedPreviews
@Composable
private fun ComicItemPreview() {
    PreviewLayout {
        ComicItem(ComicData(id = 1, title = stringResource(id = R.string.debug_short_text)))
    }
}

@OptIn(ExperimentalFoundationApi::class)
@CombinedPreviews
@Composable
private fun ComicIndexChangePreview() {
    PreviewLayout {
        ComicIndexChange(
            items = ItemSnapshotList(
                items = arrayListOf(
                    ComicData(id = 1, title = stringResource(id = R.string.debug_short_text))
                ),
                placeholdersAfter = 10,
                placeholdersBefore = 1
            ),
            pagerState = rememberPagerState {
                1
            },
            scrollState = rememberLazyListState(),
            openDialog = true,
            changeDialog = {},
            changeSelect = {},
        )
    }
}