package cn.wthee.pcrtool.ui.tool

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.isImeVisible
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
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
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.livedata.observeAsState
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
import androidx.paging.LoadState
import androidx.paging.compose.collectAsLazyPagingItems
import cn.wthee.pcrtool.R
import cn.wthee.pcrtool.data.db.entity.ComicData
import cn.wthee.pcrtool.data.enums.MainIconType
import cn.wthee.pcrtool.ui.MainActivity
import cn.wthee.pcrtool.ui.common.CircularProgressCompose
import cn.wthee.pcrtool.ui.common.CommonSpacer
import cn.wthee.pcrtool.ui.common.FabCompose
import cn.wthee.pcrtool.ui.common.IconCompose
import cn.wthee.pcrtool.ui.common.ImageCompose
import cn.wthee.pcrtool.ui.common.MainContentText
import cn.wthee.pcrtool.ui.common.MainText
import cn.wthee.pcrtool.ui.common.RATIO_COMIC
import cn.wthee.pcrtool.ui.common.SelectText
import cn.wthee.pcrtool.ui.common.Subtitle1
import cn.wthee.pcrtool.ui.common.Subtitle2
import cn.wthee.pcrtool.ui.common.clickClose
import cn.wthee.pcrtool.ui.common.getItemWidth
import cn.wthee.pcrtool.ui.theme.Dimen
import cn.wthee.pcrtool.ui.theme.defaultSpring
import cn.wthee.pcrtool.utils.VibrateUtil
import cn.wthee.pcrtool.utils.deleteSpace
import cn.wthee.pcrtool.viewmodel.ComicViewModel
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.PagerState
import com.google.accompanist.pager.rememberPagerState
import kotlinx.coroutines.launch
import kotlin.math.max

/**
 * 漫画列表
 */
@OptIn(ExperimentalPagerApi::class)
@Composable
fun ComicList(
    comicViewModel: ComicViewModel = hiltViewModel()
) {
    val gridState = rememberLazyGridState()
    val scope = rememberCoroutineScope()

    //获取分页数据
    val comicPager = remember {
        comicViewModel.getComic("")
    }
    val lazyPagingItems = comicPager.flow.collectAsLazyPagingItems()
    val items = lazyPagingItems.itemSnapshotList

    //目录
    val count = lazyPagingItems.itemCount
    val tabs = arrayListOf<String>()
    if (count > 0) {
        for (i in 0 until count) {
            val title = if (items[i] == null) {
                "${count - i}"
            } else {
                val titleIndex = items[i]!!.id
                val titleText = items[i]!!.title
                "$titleIndex $titleText"
            }
            tabs.add(title)
        }
    } else {
        tabs.add("")
    }
    //选中的目录下标
    val tocSelectedIndex = remember {
        mutableStateOf(max(0, count - 1))
    }
    //分页状态
    val pagerState = rememberPagerState()
    //同步滚动目录位置
    LaunchedEffect(pagerState.currentPage) {
        gridState.scrollToItem(pagerState.currentPage)
    }


    Box(modifier = Modifier.fillMaxSize()) {
        HorizontalPager(
            count = count,
            modifier = Modifier.align(Alignment.Center),
            state = pagerState
        ) { pagerIndex ->
            if (items[pagerIndex] == null) {
                MainText(text = "$pagerIndex")
            } else {
                ComicItem(items[pagerIndex]!!)
            }
        }

        //目录
        if (count > 0) {
            ComicIndexChange(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .navigationBarsPadding(),
                tabs = tabs,
                pagerState = pagerState,
                gridState = gridState,
                tocSelectedIndex = tocSelectedIndex
            ) {
                scope.launch {
                    pagerState.scrollToPage(tocSelectedIndex.value)
                }
            }
        }

        //回到顶部
        FabCompose(
            iconType = MainIconType.COMIC,
            text = count.toString(),
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(end = Dimen.fabMarginEnd, bottom = Dimen.fabMargin),
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
                scope.launch {
                    try {
                        pagerState.scrollToPage(0)
                    } catch (_: Exception) {
                    }
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

        ImageCompose(
            data = data.url,
            ratio = RATIO_COMIC,
            modifier = Modifier.fillMaxWidth(),
            contentScale = ContentScale.FillHeight
        )
        CommonSpacer()
        CommonSpacer()
    }

}

/**
 * 漫画目录
 */
@OptIn(ExperimentalPagerApi::class)
@Composable
private fun ComicIndexChange(
    modifier: Modifier = Modifier,
    gridState: LazyGridState,
    tabs: List<String>,
    pagerState: PagerState,
    tocSelectedIndex: MutableState<Int>,
    changeListener: (() -> Unit)
) {
    val context = LocalContext.current

    val openDialog = MainActivity.navViewModel.openChangeDataDialog.observeAsState().value ?: false
    val close = MainActivity.navViewModel.fabCloseClick.observeAsState().value ?: false
    val mainIcon = MainActivity.navViewModel.fabMainIcon.observeAsState().value ?: MainIconType.BACK
    //切换关闭监听
    if (close) {
        MainActivity.navViewModel.openChangeDataDialog.postValue(false)
        MainActivity.navViewModel.fabMainIcon.postValue(MainIconType.BACK)
        MainActivity.navViewModel.fabCloseClick.postValue(false)
    }
    if (mainIcon == MainIconType.BACK) {
        MainActivity.navViewModel.openChangeDataDialog.postValue(false)
    }


    Box(
        modifier = Modifier
            .clickClose(openDialog)
    ) {
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
                .padding(start = Dimen.textfabMargin, end = Dimen.textfabMargin),
            shape = if (openDialog) MaterialTheme.shapes.medium else CircleShape,
            onClick = {
                VibrateUtil(context).single()
                if (!openDialog) {
                    MainActivity.navViewModel.fabMainIcon.postValue(MainIconType.CLOSE)
                    MainActivity.navViewModel.openChangeDataDialog.postValue(true)
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
                ComicTocList(
                    gridState,
                    tabs,
                    pagerState,
                    tocSelectedIndex,
                    changeListener,
                )
            } else {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(start = Dimen.largePadding)
                ) {
                    IconCompose(
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
}

@OptIn(ExperimentalPagerApi::class, ExperimentalComposeUiApi::class, ExperimentalLayoutApi::class)
@Composable
private fun ComicTocList(
    gridState: LazyGridState,
    tabs: List<String>,
    pagerState: PagerState,
    tocSelectedIndex: MutableState<Int>,
    changeListener: (() -> Unit)
) {
    val context = LocalContext.current
    val input = remember {
        mutableStateOf("")
    }
    val keyboardController = LocalSoftwareKeyboardController.current
    val focusManager = LocalFocusManager.current
    val isImeVisible = WindowInsets.isImeVisible


    Column {
        OutlinedTextField(
            modifier = Modifier
                .padding(Dimen.smallPadding)
                .fillMaxWidth(),
            value = input.value,
            placeholder = {
                MainContentText(
                    text = stringResource(id = R.string.comic_intput_hint),
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
                    filterStr.toInt() in 1..pagerState.pageCount -> filterStr
                    else -> pagerState.pageCount.toString()
                }
            },
            trailingIcon = {
                if (isImeVisible) {
                    IconCompose(
                        data = MainIconType.OK, size = Dimen.fabIconSize
                    ) {
                        keyboardController?.hide()
                        focusManager.clearFocus()
                        if (input.value != "") {
                            MainActivity.navViewModel.openChangeDataDialog.postValue(false)
                            MainActivity.navViewModel.fabCloseClick.postValue(true)
                            tocSelectedIndex.value = pagerState.pageCount - input.value.toInt()
                            changeListener()
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
                        MainActivity.navViewModel.openChangeDataDialog.postValue(false)
                        MainActivity.navViewModel.fabCloseClick.postValue(true)
                        tocSelectedIndex.value = pagerState.pageCount - input.value.toInt()
                        changeListener()
                    }
                }
            )
        )

        LazyVerticalGrid(
            modifier = Modifier
                .fillMaxWidth()
                .padding(Dimen.smallPadding)
                .fillMaxHeight(0.85f),
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
                            MainActivity.navViewModel.openChangeDataDialog.postValue(false)
                            MainActivity.navViewModel.fabCloseClick.postValue(true)
                            if (pagerState.currentPage != index) {
                                tocSelectedIndex.value = index
                                changeListener()
                            }
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
    }

}