package cn.wthee.pcrtool.data.db.repository

import cn.wthee.pcrtool.data.db.dao.GachaDao

/**
 * 卡池 Repository
 *
 * 数据来源 [GachaDao]
 */
class GachaRepository(private val gachaDao: GachaDao) {

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