package cn.wthee.pcrtool.data.db.repository

import cn.wthee.pcrtool.data.db.dao.EnemyDao
import javax.inject.Inject

/**
 * 怪物信息 Repository
 *
 * @param enemyDao
 */
class EnemyRepository @Inject constructor(private val enemyDao: EnemyDao) {

    suspend fun getEnemyAttr(enemyId: Int) = enemyDao.getEnemyAttr(enemyId)

    suspend fun getAllBossIds() = enemyDao.getAllBossIds()

    suspend fun getMultiTargetEnemyInfo(enemyId: Int) = enemyDao.getMultiTargetEnemyInfo(enemyId)

    suspend fun getAtkCastTime(unitId: Int) = enemyDao.getAtkCastTime(unitId)
}