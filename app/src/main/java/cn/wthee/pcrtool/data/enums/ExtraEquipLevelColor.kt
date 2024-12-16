package cn.wthee.pcrtool.data.enums

import androidx.compose.ui.graphics.Color
import cn.wthee.pcrtool.ui.theme.colorBlue
import cn.wthee.pcrtool.ui.theme.colorCopper
import cn.wthee.pcrtool.ui.theme.colorGold
import cn.wthee.pcrtool.ui.theme.colorGray
import cn.wthee.pcrtool.ui.theme.colorPink
import cn.wthee.pcrtool.ui.theme.colorSilver
import cn.wthee.pcrtool.utils.Constants

/**
 * ex装备品级
 */
enum class ExtraEquipLevelColor(val type: Int, val color: Color, val typeName: String) {
    UNKNOWN(0, colorGray, Constants.UNKNOWN),
    RARITY_1(1, colorCopper, "★1"),
    RARITY_2(2, colorSilver, "★2"),
    RARITY_3(3, colorGold, "★3"),
    RARITY_4(4, colorPink, "★4"),
    RARITY_5(5, colorBlue, "★5"),
    ;

    companion object {
        fun getByType(type: Int) = ExtraEquipLevelColor.entries
            .find { it.type == type } ?: UNKNOWN
    }
}