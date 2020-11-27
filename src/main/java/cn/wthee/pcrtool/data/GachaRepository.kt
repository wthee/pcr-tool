package cn.wthee.pcrtool.data


//怪物数据Repository
class GachaRepository(private val gachaDao: GachaDao) {

    //获取卡池信息
    suspend fun getGachaHistory() = gachaDao.getGachaHistory()

    companion object {

        @Volatile
        private var instance: GachaRepository? = null

        fun getInstance(gachaDao: GachaDao) =
            instance ?: synchronized(this) {
                instance ?: GachaRepository(gachaDao).also { instance = it }
            }
    }
}