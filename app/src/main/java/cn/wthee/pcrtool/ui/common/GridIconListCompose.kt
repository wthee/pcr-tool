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
import cn.wthee.pcrtool.utils.ImageResourceHelper
import cn.wthee.pcrtool.utils.spanCount
import kotlin.math.max

/**
 * 角色图标列表
 * @param icons unitId
 * @param parentSpanCount 父级布局列数
 */
@Composable
fun GridIconListCompose(
    icons: List<Int>,
    parentSpanCount: Int = 1,
    onClickItem: (Int) -> Unit
) {
    VerticalGrid(
        modifier = Modifier.padding(top = Dimen.mediumPadding),
        spanCount = ((Dimen.iconSize + Dimen.mediumPadding * 2) * max(1, parentSpanCount)).spanCount
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
private fun UnitIcon(id: Int, onClickItem: (Int) -> Unit) {
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
            data = ImageResourceHelper.getInstance().getMaxIconUrl(unitId)
        ) {
            onClickItem(unitId)
        }
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