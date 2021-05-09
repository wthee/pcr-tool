package cn.wthee.pcrtool.data.db.dao

import androidx.room.*
import cn.wthee.pcrtool.data.db.entity.PvpLikedData

/**
 * 竞技场收藏 DAO
 */
@Dao
interface PvpDao {

    /**
     * 根据游戏版本 [region]，获取收藏信息
     */
    @Query("SELECT * FROM pvp_like WHERE region = :region ORDER BY date DESC")
    suspend fun getAll(region: Int): List<PvpLikedData>

    /**
     * 根据游戏版本 [region] 进攻 [atks] 防守 [defs] 自定义[type]，获取收藏信息
     */
    @Query("SELECT * FROM pvp_like WHERE atks = :atks AND defs = :defs AND region = :region AND type = :type")
    suspend fun getLiked(atks: String, defs: String, region: Int, type: Int): PvpLikedData?

    @Query("SELECT * FROM pvp_like WHERE defs = :defs AND region = :region AND type = :type")
    suspend fun getLikedList(defs: String, region: Int, type: Int): List<PvpLikedData>


    /**
     * 插入数据 [PvpLikedData]
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(data: PvpLikedData)

    /**
     * 根据游戏版本 [region] 进攻 [atks] 防守 [defs]，删除收藏信息
     */
    @Query("DELETE  FROM pvp_like WHERE atks = :atks AND defs = :defs AND region = :region")
    suspend fun delete(atks: String, defs: String, region: Int)


    /**
     * 删除数据 [PvpLikedData]
     */
    @Delete
    suspend fun delete(data: PvpLikedData)
}