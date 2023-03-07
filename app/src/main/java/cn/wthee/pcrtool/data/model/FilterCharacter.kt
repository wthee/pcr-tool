package cn.wthee.pcrtool.data.model

import androidx.core.content.edit
import cn.wthee.pcrtool.data.enums.CharacterSortType
import cn.wthee.pcrtool.ui.mainSP
import cn.wthee.pcrtool.utils.Constants
import cn.wthee.pcrtool.utils.GsonUtil
import cn.wthee.pcrtool.utils.toIntList
import com.google.gson.Gson

/**
 * 角色信息筛选
 *
 */
class FilterCharacter(
    /**
     * 是否全部显示：0全部、1仅收藏
     */
    var all: Boolean = true,
    /**
     * 位置：0全部、1前、2中、3后
     */
    var position: Int = 0,
    /**
     * 攻击类型
     */
    var atk: Int = 0,
    /**
     * 六星：0全部、1六星、2非六星
     */
    var r6: Int = 0,
    /**
     * 公会下标
     */
    var guild: Int = 0,
    /**
     * 种族下标
     */
    var race: Int = 0,
    /**
     * 排序种类
     */
    var sortType: CharacterSortType = CharacterSortType.SORT_DATE,
    /**
     * 角色名
     */
    var name: String = "",
    /**
     * 升降序
     */
    var asc: Boolean = false,
    /**
     *  1:常驻 2：限定 3：活动限定 4：额外角色
     */
    var type: Int = 0,
    /**
     * 收藏的角色编号
     */
    var starIds: ArrayList<Int> = arrayListOf()

) {

    fun position() = when (this.position) {
        1 -> arrayListOf(0, 299)
        2 -> arrayListOf(300, 599)
        3 -> arrayListOf(600, 9999)
        else -> arrayListOf(0, 9999)
    }

    companion object {

        /**
         * 获取收藏列表
         */
        fun getStarIdList() =
            (GsonUtil.fromJson(mainSP().getString(Constants.SP_STAR_CHARACTER, ""))
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
                putString(Constants.SP_STAR_CHARACTER, Gson().toJson(list))
            }
        }
    }

}

/**
 * 是否有过滤
 */
fun FilterCharacter.isFilter(): Boolean {
    return !(all && position == 0 && atk == 0 && (r6 == 0) && guild == 0 && race == 0 && sortType == CharacterSortType.SORT_DATE && name == "" && (!asc) && type == 0)
}