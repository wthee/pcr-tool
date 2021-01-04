package cn.wthee.pcrtool.data.bean

import androidx.core.content.edit
import cn.wthee.pcrtool.utils.Constants
import cn.wthee.pcrtool.utils.SharedPreferenceUtil
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class FilterEquipment(
    var all: Boolean,
    var type: String
) {
    var starIds = arrayListOf<Int>()
        get() {
            val star = SharedPreferenceUtil.getMain().getString(
                Constants.SP_STAR_EQUIP,
                Gson().toJson(arrayListOf<Int>())
            )
            return Gson().fromJson(star, object : TypeToken<List<Int>>() {}.type)
        }
        set(value) {
            SharedPreferenceUtil.getMain().edit {
                putString(Constants.SP_STAR_EQUIP, Gson().toJson(value))
            }
            field = value
        }

    fun add(vararg id: Int) {
        val list = starIds
        id.forEach {
            if (list.contains(it)) {
                list.remove(it)
            } else {
                list.add(it)
            }
        }
        starIds = list
    }

    fun remove(vararg id: Int) {
        val list = starIds
        id.forEach {
            if (list.contains(it)) {
                list.remove(it)
            }
        }
        starIds = list
    }

    fun initData() {
        this.type = "全部"
    }

}