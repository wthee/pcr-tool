package cn.wthee.pcrtool.ui.media

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import cn.wthee.pcrtool.R
import cn.wthee.pcrtool.ui.components.CenterTipText
import cn.wthee.pcrtool.ui.components.CircularProgressCompose
import cn.wthee.pcrtool.ui.components.MainText
import cn.wthee.pcrtool.ui.components.MainTitleText
import cn.wthee.pcrtool.ui.components.VerticalGrid
import cn.wthee.pcrtool.ui.components.getItemWidth
import cn.wthee.pcrtool.ui.theme.CombinedPreviews
import cn.wthee.pcrtool.ui.theme.Dimen
import cn.wthee.pcrtool.ui.theme.PreviewLayout

/**
 * 媒体文件列表
 */
@Composable
fun MediaGridList(
    urlList: List<String>,
    loading: Boolean = false,
    title: String,
    noDataTipText: String = "",
    itemContent: @Composable (String) -> Unit
) {
    //标题、数量
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
    if (loading) {
        //加载中
        Box(modifier = Modifier.fillMaxSize()) {
            CircularProgressCompose(
                modifier = Modifier
                    .padding(vertical = Dimen.largePadding)
                    .align(Alignment.Center)
            )
        }
    } else {
        if (urlList.isNotEmpty()) {
            //正常加载
            VerticalGrid(itemWidth = getItemWidth()) {
                urlList.forEach {
                    itemContent(it)
                }
            }
        } else {
            //暂无数据提示
            CenterTipText(text = noDataTipText)
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
            PictureItem(it)
        }
    }
}