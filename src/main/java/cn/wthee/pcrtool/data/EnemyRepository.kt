package cn.wthee.pcrtool.data


//怪物数据Repository
class EnemyRepository(private val enemyDao: EnemyDao) {

    //获取怪物信息
    suspend fun getAllEnemy() = enemyDao.getAllEnemy()

    //获取怪物数量
    suspend fun getEnemyCount() = enemyDao.getEnemyCount()

    companion object {

        @Volatile
        private var instance: EnemyRepository? = null

        fun getInstance(enemyDao: EnemyDao) =
            instance ?: synchronized(this) {
                instance ?: EnemyRepository(enemyDao).also { instance = it }
            }
    }
}