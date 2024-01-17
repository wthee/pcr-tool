package cn.wthee.pcrtool.data.model

import cn.wthee.pcrtool.utils.stringArrayList
import kotlinx.serialization.Serializable

/**
 * 竞技场查询结果
 */
@Serializable
data class PvpResultData(
    val id: String = "",
    val atk: String = "",
    val def: String = "",
    val region: Int = 2,
    val up: Int = 0,
    val down: Int = 0
) {

    /**
     * 获取进攻方角色 id 列表
     * 0: atk 1: def
     */
    fun getIdList(type: Int): List<Int> {
        val ids = arrayListOf<Int>()
        val idList = if (type == 0) {
            atk.stringArrayList
        } else {
            def.stringArrayList
        }
        idList.forEach {
            ids.add(it.toInt())
        }
        return ids
    }
}