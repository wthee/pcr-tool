package cn.wthee.pcrtool.ui.tool

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ModalBottomSheetLayout
import androidx.compose.material.ModalBottomSheetValue
import androidx.compose.material.rememberModalBottomSheetState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.paging.ExperimentalPagingApi
import cn.wthee.pcrtool.R
import cn.wthee.pcrtool.data.enums.MainIconType
import cn.wthee.pcrtool.data.model.ComicData
import cn.wthee.pcrtool.ui.MainActivity
import cn.wthee.pcrtool.ui.common.*
import cn.wthee.pcrtool.ui.theme.Dimen
import cn.wthee.pcrtool.ui.theme.FadeAnimation
import cn.wthee.pcrtool.ui.theme.Shape
import cn.wthee.pcrtool.ui.theme.noShape
import cn.wthee.pcrtool.viewmodel.ComicViewModel
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.rememberPagerState
import kotlinx.coroutines.launch

/**
 * 推特列表
 */
@ExperimentalFoundationApi
@ExperimentalPagerApi
@ExperimentalPagingApi
@ExperimentalMaterialApi
@Composable
fun ComicList(comicId: Int = -1, comicViewModel: ComicViewModel = hiltViewModel()) {
    val coroutineScope = rememberCoroutineScope()
    val scrollState = rememberLazyListState()
    val selectIndex = remember {
        mutableStateOf(0)
    }
    val comicList = comicViewModel.getComic().collectAsState(initial = arrayListOf()).value
    val visible = comicList.isNotEmpty()
    // dialog 状态
    val state = rememberModalBottomSheetState(
        ModalBottomSheetValue.Hidden
    )
    if (!state.isVisible && !state.isAnimationRunning) {
        MainActivity.navViewModel.fabMainIcon.postValue(MainIconType.BACK)
        MainActivity.navViewModel.fabOKCilck.postValue(false)
    }

    //关闭监听
    val ok = MainActivity.navViewModel.fabOKCilck.observeAsState().value ?: false

    Box(modifier = Modifier.fillMaxSize()) {
        FadeAnimation(visible = visible) {
            val pagerState = rememberPagerState(
                initialPage = if (comicId != -1) comicList.size - comicId else 0
            )
            ModalBottomSheetLayout(
                sheetState = state,
                scrimColor = colorResource(id = if (isSystemInDarkTheme()) R.color.alpha_black else R.color.alpha_white),
                sheetElevation = Dimen.sheetElevation,
                sheetShape = if (state.offset.value == 0f) {
                    noShape
                } else {
                    Shape.large
                },
                sheetContent = {
                    //章节选择
                    SelectPager(scrollState, selectIndex, comicList)
                },
                sheetBackgroundColor = MaterialTheme.colorScheme.surface
            ) {
                if (ok) {
                    coroutineScope.launch {
                        state.hide()
                    }
                    MainActivity.navViewModel.fabOKCilck.postValue(false)
                }

                if (state.isAnimationRunning) {
                    coroutineScope.launch {
                        pagerState.scrollToPage(selectIndex.value)
                    }
                }

                Box(
                    modifier = Modifier
                        .fillMaxSize()
                ) {
                    HorizontalPager(count = comicList.size, state = pagerState) { pageIndex ->
                        if (comicList.isNotEmpty()) {
                            ComicItem(data = comicList[pageIndex])
                        }
                    }

                    //选择
                    FabCompose(
                        iconType = MainIconType.COMIC_NAV,
                        text = stringResource(id = R.string.comic_nav),
                        modifier = Modifier
                            .align(Alignment.BottomEnd)
                            .padding(end = Dimen.fabMarginEnd, bottom = Dimen.fabMargin)
                    ) {
                        coroutineScope.launch {
                            if (state.isVisible) {
                                MainActivity.navViewModel.fabMainIcon.postValue(MainIconType.BACK)
                                state.hide()
                            } else {
                                MainActivity.navViewModel.fabMainIcon.postValue(MainIconType.OK)
                                selectIndex.value = pagerState.currentPage
                                scrollState.scrollToItem(selectIndex.value)
                                state.show()
                            }
                        }
                    }

                }
            }
        }

        FadeAnimation(visible = !visible) {
            ComicItem(data = ComicData())
        }
    }

}

/**
 * 漫画内容
 */
@ExperimentalPagerApi
@ExperimentalMaterialApi
@Composable
private fun ComicItem(data: ComicData) {
    val placeholder = data.id == -1

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
                text = if (placeholder) "" else stringResource(
                    id = R.string.comic_order,
                    data.id.toString()
                )
            )
            Text(
                text = data.title,
                style = MaterialTheme.typography.titleLarge,
            )
        }

        if (placeholder) {
            MainActivity.navViewModel.loading.postValue(true)
        } else {
            MainActivity.navViewModel.loading.postValue(false)
            ImageCompose(data = data.url, ratio = RATIO_COMIC)
        }
        CommonSpacer()
    }
}


/**
 * 章节选择
 */
@ExperimentalFoundationApi
@ExperimentalPagerApi
@Composable
private fun SelectPager(
    scrollState: LazyListState,
    selectIndex: MutableState<Int>,
    comic: List<ComicData>
) {

    LazyColumn(
        state = scrollState,
        modifier = Modifier.padding(
            top = Dimen.mediumPadding,
            start = Dimen.largePadding,
            end = Dimen.largePadding
        )
    ) {
        items(comic) {
            TocItem(selectIndex, comic.size - it.id, it)
        }
        item {
            CommonSpacer()
        }
    }
}

/**
 * 目录
 */
@ExperimentalPagerApi
@Composable
private fun TocItem(
    selectIndex: MutableState<Int>,
    index: Int,
    it: ComicData,
) {
    val textColor = if (selectIndex.value == index) {
        MaterialTheme.colorScheme.primary
    } else {
        Color.Unspecified
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(Dimen.smallPadding)
            .clip(Shape.small)
            .clickable {
                selectIndex.value = index
            },
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = it.id.toString(),
            color = textColor,
            textAlign = TextAlign.Start,
            style = MaterialTheme.typography.titleLarge,
        )
        SelectionContainer(modifier = Modifier.padding(start = Dimen.mediumPadding)) {
            Text(
                text = it.title,
                color = textColor,
                textAlign = TextAlign.Start,
                style = MaterialTheme.typography.titleMedium,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }
    }
}