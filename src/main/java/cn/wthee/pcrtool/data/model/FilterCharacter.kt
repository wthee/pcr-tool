package cn.wthee.pcrtool.data.model

import androidx.core.content.edit
import cn.wthee.pcrtool.MainActivity
import cn.wthee.pcrtool.utils.Constants
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.Serializable

class FilterCharacter(
    var all: Boolean,
    var positon: Int,
    var atk: Int,
    var r6: Boolean,
    var guild: String
) : Serializable {
    var starIds = arrayListOf<Int>()
        get() {
            val star = MainActivity.sp.getString(
                Constants.SP_STAR_CHARACTER,
                Gson().toJson(arrayListOf<Int>())
            )
            return Gson().fromJson(star, object : TypeToken<List<Int>>() {}.type)
        }
        set(value) {
            MainActivity.sp.edit {
                putString(Constants.SP_STAR_CHARACTER, Gson().toJson(value))
            }
            field = value
        }

    fun add(vararg id: Int) {
        val list = starIds
        id.forEach {
            if (!list.contains(it)) {
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
        this.all = true
        this.positon = 0
        this.atk = 0
        this.r6 = false
        this.guild = "全部"
    }

    fun position() = when (this.positon) {
        1 -> arrayListOf(0, 299)
        2 -> arrayListOf(300, 599)
        3 -> arrayListOf(600, 9999)
        else -> arrayListOf(0, 9999)
    }

}