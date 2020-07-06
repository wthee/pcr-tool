package cn.wthee.pcrtool.data

import androidx.room.Dao
import androidx.room.Query
import cn.wthee.pcrtool.data.model.EnemyData

@Dao
interface EnemyDao {

    @Query("SELECT * FROM enemy_parameter")
    suspend fun getAllEnemy(): List<EnemyData>
}