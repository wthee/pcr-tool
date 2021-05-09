package cn.wthee.pcrtool.data.db.repository

import cn.wthee.pcrtool.data.db.dao.PvpDao
import javax.inject.Inject

/**
 * 竞技场收藏 Repository
 *
 * 数据来源 [PvpDao]
 */
class PvpRepository @Inject constructor(private val pvpDao: PvpDao) {

    suspend fun getLiked(region: Int) = pvpDao.getAll(region)

    suspend fun getLikedList(defs: String, region: Int, type: Int) =
        pvpDao.getLikedList(defs, region, type)

    companion object {

        fun getInstance(pvpDao: PvpDao) = PvpRepository(pvpDao)
    }
}