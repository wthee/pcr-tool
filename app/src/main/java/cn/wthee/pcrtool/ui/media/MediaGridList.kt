package cn.wthee.pcrtool.ui.media

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.staggeredgrid.LazyStaggeredGridState
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.foundation.lazy.staggeredgrid.rememberLazyStaggeredGridState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp
import cn.wthee.pcrtool.R
import cn.wthee.pcrtool.ui.LoadState
import cn.wthee.pcrtool.ui.components.CenterTipText
import cn.wthee.pcrtool.ui.components.CommonSpacer
import cn.wthee.pcrtool.ui.components.MainText
import cn.wthee.pcrtool.ui.components.MainTitleText
import cn.wthee.pcrtool.ui.components.StateBox
import cn.wthee.pcrtool.ui.components.getItemWidth
import cn.wthee.pcrtool.ui.theme.CombinedPreviews
import cn.wthee.pcrtool.ui.theme.Dimen
import cn.wthee.pcrtool.ui.theme.PreviewLayout

/**
 * 媒体文件预览列表
 *
 * @param urlList 资源地址
 * @param loadState 加载中
 * @param title 标题
 * @param showTitle 是否显示
 * @param itemWidth 子项宽度
 * @param scrollState 滚动状态
 */
@Composable
fun MediaGridList(
    urlList: List<String>,
    loadState: LoadState = LoadState.Success,
    title: String = "",
    noDataText: String = stringResource(id = R.string.no_data),
    showTitle: Boolean = true,
    itemWidth: Dp = getItemWidth(),
    scrollState: LazyStaggeredGridState = rememberLazyStaggeredGridState(),
    itemContent: @Composable (String) -> Unit
) {

    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        //标题、数量
        if (showTitle) {
            Row(
                modifier = Modifier
                    .padding(
                        top = Dimen.largePadding,
                        start = Dimen.largePadding,
                        end = Dimen.largePadding
                    )
                    .fillMaxWidth(),
            ) {
                MainTitleText(text = title)
                Spacer(modifier = Modifier.weight(1f))
                if (urlList.isNotEmpty()) {
                    MainText(text = urlList.size.toString())
                }
            }
        }

        StateBox(
            stateType = loadState,
            noDataContent = {
                CenterTipText(noDataText)
            },
            errorContent = {
                CenterTipText(stringResource(id = R.string.data_get_error))
            }
        ) {
            //正常加载
            LazyVerticalStaggeredGrid(
                state = scrollState,
                columns = StaggeredGridCells.Adaptive(itemWidth)
            ) {
                items(urlList) {
                    itemContent(it)
                }

                item {
                    CommonSpacer()
                }
            }
        }

    }
}


@CombinedPreviews
@Composable
private fun ImageGridListPreview() {
    PreviewLayout {
        MediaGridList(
            urlList = arrayListOf("1", "2", "3"),
            title = stringResource(id = R.string.debug_short_text)
        ) {
            PictureItem(
                picUrl = it,
                modifier = Modifier
                    .padding(
                        horizontal = Dimen.largePadding,
                        vertical = Dimen.mediumPadding
                    )
            )
        }
    }
}