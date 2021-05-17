package cn.wthee.pcrtool.data.model

import androidx.core.content.edit
import cn.wthee.pcrtool.ui.mainSP
import cn.wthee.pcrtool.utils.Constants
import com.google.gson.Gson

@Suppress("RemoveRedundantCallsOfConversionMethods")
class FilterEquipment(
    var all: Boolean = true,
    var type: Int = 0,
    var name: String = "",
) {
    var starIds = arrayListOf<Int>()
        set(value) {
            val list = arrayListOf<Int>()
            value.forEach {
                list.add(it.toInt())
            }
            field = list
        }


    fun addOrRemove(vararg id: Int) {
        val sp = mainSP()
        val list = starIds
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
//        MainScope().launch {
//            DataStoreUtil.save(Constants.SP_STAR_EQUIP, Gson().toJson(list))
//        }
    }
}

fun FilterEquipment.isFilter(): Boolean {
    return !(all && type == 0 && name == "")
}