package cn.wthee.pcrtool.data

import javax.inject.Inject
import javax.inject.Singleton

//怪物数据Repository
@Singleton
class EnemyRepository @Inject constructor(private val enemyDao: EnemyDao) {

    //获取怪物信息
    suspend fun getAllEnemy() = enemyDao.getAllEnemy()

}