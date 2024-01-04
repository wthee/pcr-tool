package cn.wthee.pcrtool.data.model

import cn.wthee.pcrtool.MyApplication
import cn.wthee.pcrtool.data.enums.CharacterSortType
import cn.wthee.pcrtool.data.preferences.MainPreferencesKeys
import cn.wthee.pcrtool.ui.dataStoreMain
import cn.wthee.pcrtool.utils.JsonUtil
import kotlinx.coroutines.flow.first
import kotlinx.serialization.Serializable

/**
 * 角色信息筛选
 *
 */
@Serializable
data class FilterCharacter(
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

) {

    fun position() = when (this.position) {
        1 -> arrayListOf(0, 299)
        2 -> arrayListOf(300, 599)
        3 -> arrayListOf(600, 9999)
        else -> arrayListOf(0, 9999)
    }

    companion object {

        /**
         * 获取角色收藏列表
         */
        suspend fun getStarIdList(): ArrayList<Int> {
            val data = MyApplication.context.dataStoreMain.data.first()
            return JsonUtil.toIntList(data[MainPreferencesKeys.SP_STAR_CHARACTER])
        }
    }
}

/**
 * 是否有过滤
 */
fun FilterCharacter.isFilter(): Boolean {
    return !(all && position == 0 && atk == 0 && (r6 == 0) && guild == 0 && race == 0 && sortType == CharacterSortType.SORT_DATE && name == "" && (!asc) && type == 0)
}