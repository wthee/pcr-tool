package cn.wthee.pcrtool.data

import javax.inject.Inject
import javax.inject.Singleton

//怪物数据Repository
@Singleton
class EnemyRepository @Inject constructor(private val enemyDao: EnemyDao) {

    //获取怪物信息
    fun getAllEnemy() = enemyDao.getAllEnemy()

    //获取怪物数量
    suspend fun getEnemyCount() = enemyDao.getEnemyCount()

}