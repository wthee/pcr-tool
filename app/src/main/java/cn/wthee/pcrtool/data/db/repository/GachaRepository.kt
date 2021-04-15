package cn.wthee.pcrtool.data.db.repository

import cn.wthee.pcrtool.data.db.dao.GachaDao
import javax.inject.Inject

/**
 * 卡池 Repository
 *
 * 数据来源 [GachaDao]
 */
class GachaRepository @Inject constructor(private val gachaDao: GachaDao) {

    suspend fun getGachaHistory() = gachaDao.getGachaHistory()

    companion object {

        fun getInstance(gachaDao: GachaDao) = GachaRepository(gachaDao)
    }
}