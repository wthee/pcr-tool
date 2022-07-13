package cn.wthee.pcrtool.ui.tool

import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ModalBottomSheetLayout
import androidx.compose.material.ModalBottomSheetValue
import androidx.compose.material.rememberModalBottomSheetState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import cn.wthee.pcrtool.R
import cn.wthee.pcrtool.data.enums.MainIconType
import cn.wthee.pcrtool.data.model.ComicData
import cn.wthee.pcrtool.ui.MainActivity.Companion.navViewModel
import cn.wthee.pcrtool.ui.common.*
import cn.wthee.pcrtool.ui.theme.*
import cn.wthee.pcrtool.viewmodel.ComicViewModel
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.rememberPagerState
import kotlinx.coroutines.launch

/**
 * 推特列表
 */
@OptIn(ExperimentalMaterialApi::class, ExperimentalPagerApi::class)
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
    val sheetState = rememberModalBottomSheetState(
        ModalBottomSheetValue.Hidden
    )
    if (!sheetState.isVisible && !sheetState.isAnimationRunning) {
        navViewModel.fabMainIcon.postValue(MainIconType.BACK)
        navViewModel.fabOKCilck.postValue(false)
    }

    val pagerState = rememberPagerState(
        initialPage = if (comicId != -1) comicList.size - comicId else 0
    )

    //关闭监听
    val ok = navViewModel.fabOKCilck.observeAsState().value ?: false
    //确认
    if (ok) {
        LaunchedEffect(selectIndex) {
            pagerState.scrollToPage(selectIndex.value)
            sheetState.hide()
        }
    }


    Box(modifier = Modifier.fillMaxSize()) {
        FadeAnimation(visible = visible) {


            ModalBottomSheetLayout(
                sheetState = sheetState,
                scrimColor = if (isSystemInDarkTheme()) colorAlphaBlack else colorAlphaWhite,
                sheetShape = if (sheetState.offset.value == 0f) {
                    Shapes.None
                } else {
                    ShapeTop()
                },
                sheetContent = {
                    //章节选择
                    SelectPager(scrollState, selectIndex, comicList)
                },
                sheetBackgroundColor = MaterialTheme.colorScheme.surface
            ) {
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
                            if (sheetState.isVisible) {
                                navViewModel.fabMainIcon.postValue(MainIconType.BACK)
                                sheetState.hide()
                            } else {
                                navViewModel.fabMainIcon.postValue(MainIconType.OK)
                                selectIndex.value = pagerState.currentPage
                                scrollState.scrollToItem(selectIndex.value)
                                sheetState.show()
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
            Subtitle1(
                text = data.title
            )
        }

        if (placeholder) {
            navViewModel.loading.postValue(true)
        } else {
            navViewModel.loading.postValue(false)
            ImageCompose(
                data = data.url,
                ratio = RATIO_COMIC,
                modifier = Modifier.fillMaxWidth()
            )
        }
        CommonSpacer()
    }
}


/**
 * 章节选择
 */
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
        items(
            items = comic,
            key = {
                it.id
            }
        ) {
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
@Composable
private fun TocItem(
    selectIndex: MutableState<Int>,
    index: Int,
    it: ComicData,
) {
    SelectText(
        modifier = Modifier
            .fillMaxWidth()
            .padding(Dimen.smallPadding)
            .clip(MaterialTheme.shapes.extraSmall)
            .clickable {
                selectIndex.value = index
            },
        selected = selectIndex.value == index, text = "${it.id} ${it.title}",
        textStyle = MaterialTheme.typography.titleMedium,
        margin = 0.dp,
        padding = Dimen.mediumPadding,
        textAlign = TextAlign.Start
    )
}