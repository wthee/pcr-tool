package cn.wthee.pcrtool.data.db.repository

import cn.wthee.pcrtool.data.db.dao.GachaDao
import javax.inject.Inject

/**
 * 卡池 Repository
 *
 * @param gachaDao
 */
class GachaRepository @Inject constructor(private val gachaDao: GachaDao) {

    suspend fun getGachaHistory(limit: Int) = gachaDao.getGachaHistory(limit)

    suspend fun getFesUnitIds() = gachaDao.getFesUnitIds()
}