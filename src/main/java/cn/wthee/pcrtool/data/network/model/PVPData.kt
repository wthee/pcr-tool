package cn.wthee.pcrtool.data.network.model


data class PvpData(
    val atk: String,
    val def: String,
    val down: Int,
    val id: String,
    val region: Int,
    val up: Int
) {
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