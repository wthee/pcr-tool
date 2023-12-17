package cn.wthee.pcrtool.data.db.repository

import cn.wthee.pcrtool.data.db.dao.PvpDao
import cn.wthee.pcrtool.data.db.entity.PvpFavoriteData
import cn.wthee.pcrtool.data.db.entity.PvpHistoryData
import cn.wthee.pcrtool.utils.LogReportUtil
import javax.inject.Inject

/**
 * 竞技场收藏 Repository
 *
 * @param pvpDao
 */
class PvpRepository @Inject constructor(private val pvpDao: PvpDao) {

    suspend fun getLiked(region: Int) = try {
        pvpDao.getAll(region)
    } catch (e: Exception) {
        LogReportUtil.upload(e, "getLiked#region:$region")
        emptyList()
    }

    suspend fun getLikedList(defs: String, region: Int) = try {
        pvpDao.getLikedList(defs, region)
    } catch (e: Exception) {
        LogReportUtil.upload(e, "getLikedList#region:$region,defs:$defs")
        emptyList()
    }

    suspend fun insert(data: PvpFavoriteData) = pvpDao.insert(data)

    suspend fun getHistory(region: Int, limit: Int) = try {
        pvpDao.getHistory(region, limit)
    } catch (e: Exception) {
        LogReportUtil.upload(e, "getHistory#region:$region,limit:$limit")
        emptyList()
    }

    suspend fun insert(data: PvpHistoryData) = pvpDao.insert(data)

    suspend fun delete(atks: String, defs: String, region: Int) = pvpDao.delete(atks, defs, region)

    suspend fun deleteOldHistory(region: Int, endDate: String) =
        pvpDao.deleteOldHistory(region, endDate)

}