package cn.wthee.pcrtool.data.db.repository

import cn.wthee.pcrtool.data.db.dao.ClanBattleDao

/**
 * 团队战 Repository
 *
 * 数据来源 [ClanBattleDao]
 */
class ClanRepository(private val clanBattleDao: ClanBattleDao) {

    suspend fun getAllClanBattleData(type: Int) = clanBattleDao.getAllClanBattleData()
//        if (type == 1) clanBattleDao.getAllClanBattleData() else clanBattleDao.getAllClanBattleDataJP()

    companion object {

        fun getInstance(clanBattleDao: ClanBattleDao) = ClanRepository(clanBattleDao)
    }
}