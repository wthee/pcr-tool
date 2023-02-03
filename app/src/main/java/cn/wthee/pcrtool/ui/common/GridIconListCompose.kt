package cn.wthee.pcrtool.ui.common

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import cn.wthee.pcrtool.ui.theme.CombinedPreviews
import cn.wthee.pcrtool.ui.theme.Dimen
import cn.wthee.pcrtool.ui.theme.PreviewLayout
import cn.wthee.pcrtool.utils.ImageRequestHelper

/**
 * 角色图标列表
 * @param icons unitId
 */
@Composable
fun GridIconListCompose(
    icons: List<Int>,
    onClickItem: ((Int) -> Unit)? = null
) {
    VerticalGrid(
        modifier = Modifier.padding(top = Dimen.mediumPadding),
        itemWidth = Dimen.iconSize,
        contentPadding = Dimen.mediumPadding
    ) {
        icons.forEach {
            UnitIcon(
                it,
                onClickItem
            )
        }
    }
}

/**
 * 角色图标
 */
@Composable
private fun UnitIcon(id: Int, onClickItem: ((Int) -> Unit)? = null) {
    val unitId: Int = if (id / 10000 == 3) {
        //item 转 unit
        id % 10000 * 100 + 1
    } else {
        id
    }
    Column(
        modifier = Modifier
            .padding(
                bottom = Dimen.mediumPadding
            )
            .fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        IconCompose(
            data = ImageRequestHelper.getInstance().getMaxIconUrl(unitId),
            onClick = if (onClickItem != null) {
                {
                    onClickItem(unitId)
                }
            } else {
                null
            }
        )
    }

}


@CombinedPreviews
@Composable
private fun IconListComposePreview() {
    val mockData = arrayListOf<Int>()
    for (i in 0..10) {
        mockData.add(i)
    }
    PreviewLayout {
        GridIconListCompose(icons = mockData, onClickItem = {})
    }
}