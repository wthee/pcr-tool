package cn.wthee.pcrtool.data


import androidx.room.Dao
import androidx.room.Query
import cn.wthee.pcrtool.data.model.EnemyData

@Dao
interface EnemyDao {

    @Query("SELECT * FROM unit_enemy_data")
    suspend fun getAllEnemy(): List<EnemyData>

    @Query("SELECT COUNT(*) FROM unit_enemy_data")
    suspend fun getEnemyCount(): Int
}