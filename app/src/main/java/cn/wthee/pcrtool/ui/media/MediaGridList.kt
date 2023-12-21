package cn.wthee.pcrtool.ui.media

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp
import cn.wthee.pcrtool.R
import cn.wthee.pcrtool.ui.LoadingState
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
 * @param loading 加载中
 * @param title 标题
 * @param showTitle 是否显示
 * @param itemWidth 子项宽度
 * @param scrollState 滚动状态
 */
@Composable
fun MediaGridList(
    urlList: List<String>,
    loading: LoadingState = LoadingState.Success,
    title: String = "",
    showTitle: Boolean = true,
    itemWidth: Dp = getItemWidth(),
    scrollState: LazyGridState = rememberLazyGridState(),
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
            stateType = loading,
            errorContent = {
                CenterTipText(stringResource(id = R.string.data_get_error))
            }
        ) {
            //正常加载
            LazyVerticalGrid(state = scrollState, columns = GridCells.Adaptive(itemWidth)) {
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
            urlList = arrayListOf("1"),
            title = stringResource(id = R.string.debug_short_text)
        ) {
            PictureItem(picUrl = it)
        }
    }
}