package cn.wthee.pcrtool.data.db.repository

import cn.wthee.pcrtool.data.db.dao.PvpDao

/**
 * 竞技场收藏 Repository
 *
 * 数据来源 [PvpDao]
 */
class PvpRepository(private val pvpDao: PvpDao) {

    suspend fun getLiked(region: Int) = pvpDao.getAll(region)

    companion object {

        @Volatile
        private var instance: PvpRepository? = null

        fun getInstance(pvpDao: PvpDao) =
            instance ?: synchronized(this) {
                instance ?: PvpRepository(pvpDao).also { instance = it }
            }
    }
}