package cn.wthee.pcrtool.data.db.repository

import cn.wthee.pcrtool.data.db.dao.ClanBattleDao
import javax.inject.Inject

/**
 * 团队战 Repository
 *
 * @param clanBattleDao
 */
class ClanRepository @Inject constructor(private val clanBattleDao: ClanBattleDao) {

    suspend fun getAllClanBattleData() = clanBattleDao.getAllClanBattleData()

    suspend fun getClanInfo(clanId: Int) = clanBattleDao.getClanInfo(clanId)

    suspend fun getBossAttr(enemyId: Int) = clanBattleDao.getBossAttr(enemyId)

    suspend fun getAllBossAttr() = clanBattleDao.getAllBossAttr()

}