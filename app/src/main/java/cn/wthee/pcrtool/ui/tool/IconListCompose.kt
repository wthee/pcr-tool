package cn.wthee.pcrtool.ui.tool

import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import cn.wthee.pcrtool.R
import cn.wthee.pcrtool.ui.compose.IconCompose
import cn.wthee.pcrtool.ui.compose.getGridData
import cn.wthee.pcrtool.ui.theme.Dimen
import cn.wthee.pcrtool.utils.Constants

/**
 * 角色图标列表
 */
@Composable
fun IconListCompose(icons: List<Int>, toCharacterDetail: (Int) -> Unit) {
    val spanCount = 6
    val newList = getGridData(spanCount = spanCount, list = icons, 0)
    Column {
        newList.forEachIndexed { index, _ ->
            if (index % spanCount == 0) {
                IconListRow(newList.subList(index, index + spanCount), toCharacterDetail)
            }
        }
    }
}

@Composable
private fun IconListRow(list: List<Int>, toCharacterDetail: (Int) -> Unit) {
    Row(
        horizontalArrangement = Arrangement.SpaceAround,
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = Dimen.mediuPadding)
    ) {
        list.forEach {
            val unitId: Int
            val iconId: Int
            if (it / 10000 == 3) {
                //item 转 unit
                iconId = it % 10000 * 100 + 11
                unitId = it % 10000 * 100 + 1
            } else {
                iconId = it + 30
                unitId = it
            }
            if (it == 0) {
                IconCompose(
                    data = R.drawable.unknown_gray,
                    modifier = Modifier.alpha(0f)
                )
            } else {
                IconCompose(
                    data = Constants.UNIT_ICON_URL + iconId + Constants.WEBP
                ) {
                    toCharacterDetail(unitId)
                }
            }
        }
    }
}