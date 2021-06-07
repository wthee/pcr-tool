package cn.wthee.pcrtool.data.model

import androidx.core.content.edit
import cn.wthee.pcrtool.data.enums.SortType
import cn.wthee.pcrtool.ui.mainSP
import cn.wthee.pcrtool.utils.Constants
import com.google.gson.Gson

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
    var guild: Int = 0,
    var sortType: SortType = SortType.SORT_DATE,
    var name: String = "",
    var asc: Boolean = false
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
            putString(Constants.SP_STAR_CHARACTER, Gson().toJson(list))
        }
//        MainScope().launch {
//            DataStoreUtil.save(Constants.SP_STAR_CHARACTER, Gson().toJson(list))
////            DataStoreUtil.get(Constants.SP_STAR_CHARACTER).collect { str ->
////                val newStarIds = DataStoreUtil.fromJson<ArrayList<Int>>(str)
////                starIds = newStarIds ?: arrayListOf()
////            }
//        }
    }

    fun position() = when (this.positon) {
        1 -> arrayListOf(0, 299)
        2 -> arrayListOf(300, 599)
        3 -> arrayListOf(600, 9999)
        else -> arrayListOf(0, 9999)
    }

}

/**
 * 是否有过滤
 */
fun FilterCharacter.isFilter(): Boolean {
    return !(all && positon == 0 && atk == 0 && (!r6) && guild == 0 && sortType == SortType.SORT_DATE && name == "" && (!asc))
}