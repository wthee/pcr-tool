package cn.wthee.pcrtool.data.model

import cn.wthee.pcrtool.utils.Constants
import cn.wthee.pcrtool.utils.DataStoreUtil
import com.google.gson.Gson
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch

@Suppress("RemoveRedundantCallsOfConversionMethods")
class FilterEquipment(
    var all: Boolean = true,
    var type: String = "全部"
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
        val list = starIds
        id.forEach {
            if (list.contains(it)) {
                list.remove(it)
            } else {
                list.add(it)
            }
        }
        //保存
        MainScope().launch {
            DataStoreUtil.save(Constants.SP_STAR_EQUIP, Gson().toJson(list))
        }
    }

    fun initData() {
        this.all = true
        this.type = "全部"
    }

}