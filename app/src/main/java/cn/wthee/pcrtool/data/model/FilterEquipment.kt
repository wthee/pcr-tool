package cn.wthee.pcrtool.data.model

import androidx.core.content.edit
import cn.wthee.pcrtool.ui.mainSP
import cn.wthee.pcrtool.utils.Constants
import cn.wthee.pcrtool.utils.GsonUtil
import cn.wthee.pcrtool.utils.toIntList
import com.google.gson.Gson

data class FilterEquipment(
    var all: Boolean = true,
    var craft: Int = 1,
    var colorType: Int = 0,
    var name: String = "",
    var starIds: ArrayList<Int> = arrayListOf()
) {

    companion object {
        /**
         * 获取收藏列表
         */
        fun getStarIdList() =
            (GsonUtil.fromJson(mainSP().getString(Constants.SP_STAR_EQUIP, ""))
                ?: arrayListOf<Double>())
                .toIntList()

        /**
         * 新增或删除
         */
        fun addOrRemove(vararg id: Int) {
            val sp = mainSP()
            val list = getStarIdList()
            id.forEach {
                if (list.contains(it)) {
                    list.remove(it)
                } else {
                    list.add(it)
                }
            }
            //保存
            sp.edit {
                putString(Constants.SP_STAR_EQUIP, Gson().toJson(list))
            }
        }
    }

}

fun FilterEquipment.isFilter(): Boolean {
    return !(all && colorType == 0 && name == "" && craft == 1)
}