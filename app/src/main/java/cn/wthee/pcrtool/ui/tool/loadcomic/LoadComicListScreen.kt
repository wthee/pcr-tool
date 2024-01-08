package cn.wthee.pcrtool.ui.tool.loadcomic

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import cn.wthee.pcrtool.R
import cn.wthee.pcrtool.data.enums.MainIconType
import cn.wthee.pcrtool.ui.LoadingState
import cn.wthee.pcrtool.ui.components.CenterTipText
import cn.wthee.pcrtool.ui.components.CircularProgressCompose
import cn.wthee.pcrtool.ui.components.MainScaffold
import cn.wthee.pcrtool.ui.components.MainSmallFab
import cn.wthee.pcrtool.ui.components.StateBox
import cn.wthee.pcrtool.ui.components.VerticalStaggeredGrid
import cn.wthee.pcrtool.ui.components.getItemWidth
import cn.wthee.pcrtool.ui.media.MediaGridList
import cn.wthee.pcrtool.ui.media.PictureItem
import cn.wthee.pcrtool.ui.theme.Dimen
import cn.wthee.pcrtool.ui.theme.noShape
import kotlinx.coroutines.launch

/**
 * 过场动画列表
 *
 */
@Composable
fun LoadComicListScreen(
    loadComicViewModel: LoadComicViewModel = hiltViewModel()
) {
    val uiState by loadComicViewModel.uiState.collectAsStateWithLifecycle()
    val scrollState = rememberLazyGridState()
    val coroutineScope = rememberCoroutineScope()

    MainScaffold(
        fab = {
            //回到顶部
            MainSmallFab(
                iconType = MainIconType.LOAD_COMIC,
                text = uiState.comicList?.size.toString(),
                extraContent = if (uiState.loadState == LoadingState.Loading) {
                    //加载提示
                    {
                        CircularProgressCompose()
                    }
                } else {
                    null
                }
            ) {
                coroutineScope.launch {
                    try {
                        scrollState.scrollToItem(0)
                    } catch (_: Exception) {
                    }
                }
            }
        }
    ) {
        StateBox(
            stateType = uiState.loadState,
            loadingContent = {
                VerticalStaggeredGrid(
                    itemWidth = getItemWidth() / 2,
                    contentPadding = Dimen.mediumPadding
                ) {
                    for (i in 0..10) {
                        PictureItem(
                            picUrl = "",
                            ratio = 1f,
                            shape = noShape()
                        )
                    }
                }
            },
            errorContent = {
                CenterTipText(stringResource(id = R.string.data_get_error))
            }
        ) {
            MediaGridList(
                urlList = uiState.comicList!!,
                showTitle = false,
                itemWidth = getItemWidth() / 2,
                scrollState = scrollState
            ) {
                PictureItem(
                    picUrl = it,
                    ratio = 1f,
                    shape = noShape(),
                    modifier = Modifier.padding(Dimen.mediumPadding)
                )
            }
        }
    }
}