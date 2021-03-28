package cn.wthee.pcrtool.data.db.repository

import cn.wthee.pcrtool.data.db.dao.ClanBattleDao

/**
 * 团队战 Repository
 *
 * 数据来源 [ClanBattleDao]
 */
class ClanRepository(private val clanBattleDao: ClanBattleDao) {

    suspend fun getAllClanBattleData(type: Int) =
        if (type == 1) clanBattleDao.getAllClanBattleData() else clanBattleDao.getAllClanBattleDataJP()

    suspend fun getBossAttr(enemyId: Int) = clanBattleDao.getBossAttr(enemyId)

    companion object {

        fun getInstance(clanBattleDao: ClanBattleDao) = ClanRepository(clanBattleDao)
    }
}