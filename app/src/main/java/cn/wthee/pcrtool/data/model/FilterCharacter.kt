package cn.wthee.pcrtool.data.model

import cn.wthee.pcrtool.data.enums.SortType
import cn.wthee.pcrtool.utils.Constants
import cn.wthee.pcrtool.utils.DataStoreUtil
import com.google.gson.Gson
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import java.io.Serializable

/**
 * 角色信息筛选
 *
 */
@Suppress("RemoveRedundantCallsOfConversionMethods")
class FilterCharacter(
    var all: Boolean = true,
    var positon: Int = 0,
    var atk: Int = 0,
    var r6: Boolean = false,
    var guild: String = "全部",
    var sortType: SortType = SortType.SORT_DATE,
    var name: String = "",
    var asc: Boolean = false
) : Serializable {
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
            DataStoreUtil.save(Constants.SP_STAR_CHARACTER, Gson().toJson(list))
//            DataStoreUtil.get(Constants.SP_STAR_CHARACTER).collect { str ->
//                val newStarIds = DataStoreUtil.fromJson<ArrayList<Int>>(str)
//                starIds = newStarIds ?: arrayListOf()
//            }
        }
    }

    fun position() = when (this.positon) {
        1 -> arrayListOf(0, 299)
        2 -> arrayListOf(300, 599)
        3 -> arrayListOf(600, 9999)
        else -> arrayListOf(0, 9999)
    }

}