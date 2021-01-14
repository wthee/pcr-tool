package cn.wthee.pcrtool.data.bean

import android.content.Context
import androidx.core.content.edit
import cn.wthee.pcrtool.utils.ActivityHelper
import cn.wthee.pcrtool.utils.Constants
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class FilterEquipment(
    var all: Boolean,
    var type: String
) {
    var starIds = arrayListOf<Int>()
        get() {
            val sp = ActivityHelper.instance.currentActivity!!.getSharedPreferences(
                "main",
                Context.MODE_PRIVATE
            )
            val star = sp.getString(
                Constants.SP_STAR_EQUIP,
                Gson().toJson(arrayListOf<Int>())
            )
            return Gson().fromJson(star, object : TypeToken<List<Int>>() {}.type)
        }
        set(value) {
            val sp = ActivityHelper.instance.currentActivity!!.getSharedPreferences(
                "main",
                Context.MODE_PRIVATE
            )
            sp.edit {
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