package cn.wthee.pcrtool.data.db.repository

import cn.wthee.pcrtool.data.db.dao.PvpDao


//卡池数据Repository
class PvpRepository(private val pvpDao: PvpDao) {

    //获取卡池信息
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