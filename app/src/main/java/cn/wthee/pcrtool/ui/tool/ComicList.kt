package cn.wthee.pcrtool.ui.tool

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.paging.ExperimentalPagingApi
import cn.wthee.pcrtool.R
import cn.wthee.pcrtool.data.db.entity.ComicData
import cn.wthee.pcrtool.data.enums.MainIconType
import cn.wthee.pcrtool.ui.compose.*
import cn.wthee.pcrtool.ui.theme.Dimen
import cn.wthee.pcrtool.viewmodel.ComicViewModel
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.rememberPagerState
import com.google.accompanist.placeholder.PlaceholderHighlight
import com.google.accompanist.placeholder.material.placeholder
import com.google.accompanist.placeholder.material.shimmer
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
fun ComicList(scrollState: LazyListState, comicViewModel: ComicViewModel = hiltViewModel()) {
    val coroutineScope = rememberCoroutineScope()

    comicViewModel.getComic()
    val comic = comicViewModel.comic.observeAsState().value ?: arrayListOf()
    val visible = comic.isNotEmpty()

    Box(
        modifier = Modifier
            .fillMaxSize()
    ) {
        val pagerState = rememberPagerState(pageCount = comic.size)

        FadeAnimation(visible = visible) {
            HorizontalPager(state = pagerState) { pageIndex ->
                ComicItem(data = comic[pageIndex])
            }
        }

        //回到顶部
        FabCompose(
            iconType = MainIconType.COMIC,
            text = stringResource(id = R.string.comic),
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(end = Dimen.fabMarginEnd, bottom = Dimen.fabMargin)
        ) {
            coroutineScope.launch {
                if (visible) {
                    scrollState.scrollToItem(0)
                }
            }
        }
    }
}


/**
 * 推特内容
 */
@ExperimentalAnimationApi
@ExperimentalPagerApi
@ExperimentalMaterialApi
@Composable
private fun ComicItem(data: ComicData) {
    val placeholder = data.id == -1
    val expand = remember {
        mutableStateOf(false)
    }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                expand.value = !expand.value
            }
            .placeholder(visible = placeholder, highlight = PlaceholderHighlight.shimmer())
    ) {
        Subtitle2(
            text = stringResource(
                id = R.string.comic_order,
                data.id.toString()
            )
        )
        Subtitle1(text = data.title)
        ImageCompose(url = data.url, hasRatio = true)
    }
}
