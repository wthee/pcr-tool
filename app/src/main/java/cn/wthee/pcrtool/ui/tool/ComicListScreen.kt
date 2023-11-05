package cn.wthee.pcrtool.ui.tool

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.isImeVisible
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SmallFloatingActionButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
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
import cn.wthee.pcrtool.ui.MainActivity
import cn.wthee.pcrtool.ui.components.CircularProgressCompose
import cn.wthee.pcrtool.ui.components.CommonSpacer
import cn.wthee.pcrtool.ui.components.MainContentText
import cn.wthee.pcrtool.ui.components.MainIcon
import cn.wthee.pcrtool.ui.components.MainImage
import cn.wthee.pcrtool.ui.components.MainScaffold
import cn.wthee.pcrtool.ui.components.MainSmallFab
import cn.wthee.pcrtool.ui.components.MainText
import cn.wthee.pcrtool.ui.components.RATIO_COMIC
import cn.wthee.pcrtool.ui.components.SelectText
import cn.wthee.pcrtool.ui.components.Subtitle1
import cn.wthee.pcrtool.ui.components.Subtitle2
import cn.wthee.pcrtool.ui.components.getItemWidth
import cn.wthee.pcrtool.ui.theme.CombinedPreviews
import cn.wthee.pcrtool.ui.theme.Dimen
import cn.wthee.pcrtool.ui.theme.PreviewLayout
import cn.wthee.pcrtool.ui.theme.defaultSpring
import cn.wthee.pcrtool.utils.VibrateUtil
import cn.wthee.pcrtool.utils.deleteSpace
import kotlinx.coroutines.launch

/**
 * 漫画列表
 */
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ComicListScreen(
    comicListViewModel: ComicListViewModel = hiltViewModel()
) {
    val gridState = rememberLazyGridState()
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
            gridState.scrollToItem(uiState.selectedIndex)
        }


        MainScaffold(
            secondLineFab = {
                //目录
                if (count > 0) {
                    ComicIndexChange(
                        items = items,
                        pagerState = pagerState,
                        gridState = gridState,
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
                    extraContent = if (lazyPagingItems.loadState.refresh == LoadState.Loading) {
                        //加载提示
                        {
                            CircularProgressCompose()
                        }
                    } else {
                        null
                    }
                ) {
                    scope.launch {
                        try {
                            pagerState.scrollToPage(0)
                        } catch (_: Exception) {
                        }
                    }
                }
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
                state = pagerState
            ) { pagerIndex ->
                if (items[pagerIndex] == null) {
                    MainText(text = "$pagerIndex")
                } else {
                    ComicItem(items[pagerIndex]!!)
                }
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
                selectable = true
            )
        }

        MainImage(
            data = data.url,
            ratio = RATIO_COMIC,
            modifier = Modifier.fillMaxWidth(),
            contentScale = ContentScale.FillHeight,
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
    modifier: Modifier = Modifier,
    gridState: LazyGridState,
    items: ItemSnapshotList<ComicData>,
    pagerState: PagerState,
    openDialog: Boolean,
    changeDialog: (Boolean) -> Unit,
    changeSelect: ((Int) -> Unit)
) {
    val context = LocalContext.current

    //切换
    SmallFloatingActionButton(
        modifier = modifier
            .animateContentSize(defaultSpring())
            .padding(
                start = Dimen.fabMargin,
                end = Dimen.fabMargin,
                bottom = Dimen.fabMargin * 2 + Dimen.fabSize,
                top = Dimen.fabMargin
            )
            .padding(start = Dimen.textFabMargin, end = Dimen.textFabMargin),
        shape = if (openDialog) MaterialTheme.shapes.medium else CircleShape,
        onClick = {
            VibrateUtil(context).single()
            if (!openDialog) {
                MainActivity.navViewModel.fabMainIcon.postValue(MainIconType.CLOSE)
                changeDialog(true)
            } else {
                MainActivity.navViewModel.fabCloseClick.postValue(true)
            }
        },
        elevation = FloatingActionButtonDefaults.elevation(
            defaultElevation = if (openDialog) {
                Dimen.popupMenuElevation
            } else {
                Dimen.fabElevation
            }
        ),
    ) {
        if (openDialog) {
            //展开目录
            ComicTocList(
                gridState,
                items,
                pagerState,
                changeSelect,
            )
        } else {
            //fab
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(start = Dimen.largePadding)
            ) {
                MainIcon(
                    data = MainIconType.COMIC_NAV,
                    size = Dimen.fabIconSize
                )
                Text(
                    text = stringResource(id = R.string.comic_toc),
                    style = MaterialTheme.typography.titleSmall,
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(
                        start = Dimen.mediumPadding, end = Dimen.largePadding
                    )
                )
            }

        }
    }
}

@OptIn(ExperimentalComposeUiApi::class, ExperimentalLayoutApi::class,
    ExperimentalFoundationApi::class
)
@Composable
private fun ComicTocList(
    gridState: LazyGridState,
    items: ItemSnapshotList<ComicData>,
    pagerState: PagerState,
    changeSelect: ((Int) -> Unit)
) {
    val context = LocalContext.current

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
    val keyboardController = LocalSoftwareKeyboardController.current
    val focusManager = LocalFocusManager.current
    val isImeVisible = WindowInsets.isImeVisible


    Column {
        //目录
        LazyVerticalGrid(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .padding(Dimen.smallPadding),
            state = gridState,
            columns = GridCells.Adaptive(getItemWidth())
        ) {
            itemsIndexed(tabs) { index, tab ->
                val mModifier = if (pagerState.currentPage == index) {
                    Modifier.fillMaxWidth()
                } else {
                    Modifier
                        .fillMaxWidth()
                        .clip(MaterialTheme.shapes.medium)
                        .clickable {
                            VibrateUtil(context).single()
                            changeSelect(index)
                        }
                }
                SelectText(
                    selected = pagerState.currentPage == index,
                    text = tab,
                    textStyle = MaterialTheme.typography.titleMedium,
                    modifier = mModifier.padding(Dimen.mediumPadding),
                    textAlign = TextAlign.Start
                )
            }
        }

        //输入
        OutlinedTextField(
            modifier = Modifier
                .padding(Dimen.smallPadding)
                .fillMaxWidth()
                .imePadding(),
            value = input.value,
            placeholder = {
                MainContentText(
                    text = stringResource(id = R.string.comic_input_hint),
                    color = MaterialTheme.colorScheme.outline
                )
            },
            onValueChange = {
                var filterStr = ""
                it.deleteSpace.forEach { ch ->
                    if (Regex("\\d").matches(ch.toString())) {
                        filterStr += ch
                    }
                }
                input.value = when {
                    filterStr == "" -> ""
                    filterStr.toInt() < 1 -> "1"
                    filterStr.toInt() in 1..pageCount -> filterStr
                    else -> pageCount.toString()
                }
            },
            trailingIcon = {
                if (isImeVisible) {
                    MainIcon(
                        data = MainIconType.OK, size = Dimen.fabIconSize
                    ) {
                        keyboardController?.hide()
                        focusManager.clearFocus()
                        if (input.value != "") {
                            changeSelect(pageCount - input.value.toInt())
                        }
                    }
                }
            },
            shape = MaterialTheme.shapes.medium,
            textStyle = MaterialTheme.typography.bodyMedium,
            keyboardOptions = KeyboardOptions(
                imeAction = ImeAction.Done,
                keyboardType = KeyboardType.Number
            ),
            keyboardActions = KeyboardActions(
                onDone = {
                    keyboardController?.hide()
                    focusManager.clearFocus()
                    if (input.value != "") {
                        changeSelect(pageCount - input.value.toInt())
                    }
                }
            )
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
            gridState = rememberLazyGridState(),
            openDialog = true,
            changeDialog = {},
            changeSelect = {},
        )
    }
}