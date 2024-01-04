package cn.wthee.pcrtool.data.model

import cn.wthee.pcrtool.MyApplication
import cn.wthee.pcrtool.data.preferences.MainPreferencesKeys
import cn.wthee.pcrtool.ui.dataStoreMain
import cn.wthee.pcrtool.utils.JsonUtil
import kotlinx.coroutines.flow.first
import kotlinx.serialization.Serializable

@Serializable
data class FilterEquip(
    var all: Boolean = true,
    var craft: Int = 1,
    var colorType: Int = 0,
    var name: String = ""
) {
    companion object {

        /**
         * 获取装备收藏列表
         */
        suspend fun getFavoriteIdList(): ArrayList<Int> {
            val data = MyApplication.context.dataStoreMain.data.first()
            return JsonUtil.toIntList(data[MainPreferencesKeys.SP_STAR_EQUIP])
        }
    }
}


fun FilterEquip.isFilter(): Boolean {
    return !(all && colorType == 0 && name == "" && craft == 1)
}