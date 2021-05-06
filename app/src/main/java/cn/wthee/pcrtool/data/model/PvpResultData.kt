package cn.wthee.pcrtool.data.model

/**
 * 竞技场查询结果
 */
data class PvpResultData(
    val atk: String,
    val def: String,
    val down: Int,
    val id: String,
    val region: Int,
    val up: Int
) {

    /**
     * 获取进攻方角色 id 列表
     * 0: atk 1: def
     */
    fun getIdList(type: Int): List<Int> {
        val ids = arrayListOf<Int>()
        val idList = if (type == 0) {
            atk.split('-').filter {
                it != ""
            }
        } else {
            def.split('-').filter {
                it != ""
            }
        }
        idList.forEach {
            ids.add(it.toInt())
        }
        return ids
    }
}