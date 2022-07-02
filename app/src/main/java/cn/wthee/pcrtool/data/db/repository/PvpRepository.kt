package cn.wthee.pcrtool.data.db.repository

import cn.wthee.pcrtool.data.db.dao.PvpDao
import cn.wthee.pcrtool.data.db.entity.PvpFavoriteData
import cn.wthee.pcrtool.data.db.entity.PvpHistoryData
import javax.inject.Inject

/**
 * 竞技场收藏 Repository
 *
 * @param pvpDao
 */
class PvpRepository @Inject constructor(private val pvpDao: PvpDao) {

    suspend fun getLiked(region: Int) = pvpDao.getAll(region)

    suspend fun getLikedList(defs: String, region: Int) =
        pvpDao.getLikedList(defs, region)

    suspend fun insert(data: PvpFavoriteData) = pvpDao.insert(data)

    suspend fun getHistory(region: Int) = pvpDao.getHistory(region)

    suspend fun insert(data: PvpHistoryData) = pvpDao.insert(data)

    suspend fun delete(atks: String, defs: String, region: Int) = pvpDao.delete(atks, defs, region)

    suspend fun getHistory(region: Int, startDate: String, endDate: String) =
        pvpDao.getHistory(region, startDate, endDate)

}