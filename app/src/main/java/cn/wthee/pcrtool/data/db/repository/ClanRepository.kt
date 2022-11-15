package cn.wthee.pcrtool.data.db.repository

import cn.wthee.pcrtool.data.db.dao.ClanBattleDao
import javax.inject.Inject

/**
 * 公会战 Repository
 *
 * @param clanBattleDao
 */
class ClanRepository @Inject constructor(private val clanBattleDao: ClanBattleDao) {

    suspend fun getAllClanBattleData(clanId: Int) = clanBattleDao.getAllClanBattleData(clanId)

    suspend fun getAllClanBattleTargetCount(phase: Int) =
        clanBattleDao.getAllClanBattleTargetCount(phase)

    suspend fun getBossAttr(enemyId: Int) = clanBattleDao.getBossAttr(enemyId)

    suspend fun getAllBossAttr() = clanBattleDao.getAllBossAttr()

}