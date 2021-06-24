package cn.wthee.pcrtool.ui.tool

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
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
import cn.wthee.pcrtool.data.db.entity.ComicData
import cn.wthee.pcrtool.data.enums.MainIconType
import cn.wthee.pcrtool.ui.MainActivity
import cn.wthee.pcrtool.ui.compose.*
import cn.wthee.pcrtool.ui.theme.Dimen
import cn.wthee.pcrtool.ui.theme.Shapes
import cn.wthee.pcrtool.viewmodel.ComicViewModel
import com.google.accompanist.coil.rememberCoilPainter
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.PagerState
import com.google.accompanist.pager.rememberPagerState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

/**
 * 推特列表
 */
@ExperimentalFoundationApi
@ExperimentalPagerApi
@ExperimentalPagingApi
@ExperimentalMaterialApi
@ExperimentalAnimationApi
@Composable
fun ComicList(comicViewModel: ComicViewModel = hiltViewModel()) {
    val coroutineScope = rememberCoroutineScope()
    val scrollState = rememberLazyListState()

    comicViewModel.getComic()
    val comic = comicViewModel.comic.observeAsState().value ?: arrayListOf()
    val visible = comic.isNotEmpty()
    // dialog 状态
    val state = rememberModalBottomSheetState(
        ModalBottomSheetValue.Hidden
    )
    if (!state.isVisible) {
        MainActivity.navViewModel.fabMainIcon.postValue(MainIconType.BACK)
        MainActivity.navViewModel.fabOKCilck.postValue(false)
    }
    //关闭监听
    val ok = MainActivity.navViewModel.fabOKCilck.observeAsState().value ?: false

    Box {
        FadeAnimation(visible = visible) {
            val pagerState = rememberPagerState(pageCount = comic.size)
            ModalBottomSheetLayout(
                sheetState = state,
                scrimColor = colorResource(id = if (MaterialTheme.colors.isLight) R.color.alpha_white else R.color.alpha_black),
                sheetElevation = Dimen.sheetElevation,
                sheetContent = {
                    //章节选择
                    SelectPager(scrollState, pagerState, comic)
                }
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                ) {
                    if (ok) {
                        coroutineScope.launch {
                            state.hide()
                        }
                        MainActivity.navViewModel.fabOKCilck.postValue(false)
                    }
                    HorizontalPager(state = pagerState) { pageIndex ->
                        ComicItem(data = comic[pageIndex])
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
@ExperimentalAnimationApi
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
                style = MaterialTheme.typography.h6,
            )
        }

        if (placeholder) {
            Image(
                painter = rememberCoilPainter(request = R.drawable.load),
                contentDescription = null,
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(RATIO_COMIC)
            )
        } else {
            ImageCompose(url = data.url, hasRatio = true)
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
    pagerState: PagerState,
    comic: List<ComicData>
) {
    val coroutineScope = rememberCoroutineScope()
    LazyColumn(state = scrollState, contentPadding = PaddingValues(Dimen.largePadding)) {
        items(comic) {
            TocItem(scrollState, pagerState, it, coroutineScope)
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
    scrollState: LazyListState,
    pagerState: PagerState,
    it: ComicData,
    coroutineScope: CoroutineScope
) {
    val index = pagerState.pageCount - it.id
    val selected = remember {
        mutableStateOf(pagerState.currentPage)
    }
    val textColor = if (selected.value == index) {
        MaterialTheme.colors.primary
    } else {
        Color.Unspecified
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(Dimen.smallPadding)
            .clip(Shapes.small)
            .clickable {
                selected.value = index
                coroutineScope.launch {
                    scrollState.animateScrollToItem(index)
                }
                coroutineScope.launch {
                    pagerState.animateScrollToPage(index, animationSpec = defaultSpring())
                }
            },
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = it.id.toString(),
            color = textColor,
            textAlign = TextAlign.Start,
            style = MaterialTheme.typography.h6,
        )
        SelectionContainer(modifier = Modifier.padding(start = Dimen.mediuPadding)) {
            Text(
                text = it.title,
                color = textColor,
                textAlign = TextAlign.Start,
                style = MaterialTheme.typography.subtitle1,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }
    }
}