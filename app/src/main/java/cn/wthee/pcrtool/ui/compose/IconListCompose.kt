package cn.wthee.pcrtool.ui.compose

import androidx.compose.runtime.Composable
import cn.wthee.pcrtool.ui.theme.Dimen
import cn.wthee.pcrtool.utils.Constants
import com.google.accompanist.flowlayout.FlowRow
import com.google.accompanist.flowlayout.SizeMode.Expand

/**
 * 角色图标列表
 */
@Composable
fun IconListCompose(icons: List<Int>, toCharacterDetail: (Int) -> Unit) {
    FlowRow(
        mainAxisSize = Expand,
        mainAxisSpacing = Dimen.largePadding,
        crossAxisSpacing = Dimen.mediuPadding,
    ) {
        icons.forEach {
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
                CommonIconSpacer()
            } else {
                IconCompose(
                    data = Constants.UNIT_ICON_URL + iconId + Constants.WEBP,
                ) {
                    toCharacterDetail(unitId)
                }
            }
        }
    }
}