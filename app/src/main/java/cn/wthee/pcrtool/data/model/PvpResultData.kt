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
     */
    fun getAtkIdList(): List<Int> {
        val ids = arrayListOf<Int>()
        val atks = atk.split('-').filter {
            it != ""
        }
        atks.forEach {
            ids.add(it.toInt())
        }
        return ids
    }
}