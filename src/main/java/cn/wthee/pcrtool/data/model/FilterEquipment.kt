package cn.wthee.pcrtool.data.model

import androidx.core.content.edit
import cn.wthee.pcrtool.MainActivity
import cn.wthee.pcrtool.utils.Constants
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class FilterEquipment(
    var all: Boolean,
    var type: String
) {
    var starIds = arrayListOf<Int>()
        get() {
            val star = MainActivity.sp.getString(
                Constants.SP_STAR_EQUIP,
                Gson().toJson(arrayListOf<Int>())
            )
            return Gson().fromJson(star, object : TypeToken<List<Int>>() {}.type)
        }
        set(value) {
            MainActivity.sp.edit {
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