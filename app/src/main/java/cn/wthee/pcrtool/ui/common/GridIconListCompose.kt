package cn.wthee.pcrtool.ui.common

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import cn.wthee.pcrtool.ui.PreviewBox
import cn.wthee.pcrtool.ui.theme.Dimen
import cn.wthee.pcrtool.utils.ImageResourceHelper
import cn.wthee.pcrtool.utils.ImageResourceHelper.Companion.ICON_UNIT

/**
 * 角色图标列表
 */
@Composable
fun GridIconListCompose(
    icons: List<Int>,
    onClickItem: (Int) -> Unit
) {
    VerticalGrid(
        modifier = Modifier.padding(
            top = Dimen.mediumPadding,
            start = Dimen.mediumPadding,
            end = Dimen.mediumPadding
        ),
        maxColumnWidth = Dimen.iconSize + Dimen.mediumPadding * 2
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
fun UnitIcon(id: Int, onClickItem: (Int) -> Unit) {
    val unitId: Int
    val iconId: Int
    if (id / 10000 == 3) {
        //item 转 unit
        iconId = id % 10000 * 100 + 11
        unitId = id % 10000 * 100 + 1
    } else {
        iconId = id + 30
        unitId = id
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
            data = ImageResourceHelper.getInstance().getUrl(ICON_UNIT, iconId)
        ) {
            onClickItem(unitId)
        }
    }

}


@Preview
@Composable
private fun IconListComposePreview() {
    val mockData = arrayListOf<Int>()
    for (i in 0..10) {
        mockData.add(i)
    }
    PreviewBox {
        GridIconListCompose(icons = mockData, onClickItem = {})
    }
}