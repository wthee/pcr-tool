package cn.wthee.pcrtool.data.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import cn.wthee.pcrtool.data.db.entity.PvpLikedData

@Dao
interface PvpDao {

    @Query("SELECT * FROM pvp_like WHERE region = :region ORDER BY date DESC")
    suspend fun getAll(region: Int): List<PvpLikedData>

    @Query("SELECT * FROM pvp_like WHERE atks = :atks AND defs = :defs")
    suspend fun get(atks: String, defs: String): PvpLikedData?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(data: PvpLikedData)


    @Query("DELETE  FROM pvp_like WHERE atks = :atks AND defs = :defs")
    suspend fun delete(atks: String, defs: String)

}