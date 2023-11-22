package cn.wthee.pcrtool.data.model

import cn.wthee.pcrtool.MyApplication
import cn.wthee.pcrtool.data.preferences.MainPreferencesKeys
import cn.wthee.pcrtool.ui.dataStoreMain
import cn.wthee.pcrtool.utils.GsonUtil
import kotlinx.coroutines.flow.first

data class FilterExtraEquipment(
    var all: Boolean = true,
    var flag: Int = 0,
    var rarity: Int = 0,
    var category: Int = 0,
    var name: String = ""
) {
    companion object {

        /**
         * 获取装备收藏列表
         */
        suspend fun getStarIdList(): ArrayList<Int> {
            val data = MyApplication.context.dataStoreMain.data.first()
            return GsonUtil.toIntList(data[MainPreferencesKeys.SP_STAR_EXTRA_EQUIP])
        }
    }
}


fun FilterExtraEquipment.isFilter(): Boolean {
    return !(all && rarity == 0 && name == "" && flag == 0 && category == 0)
}