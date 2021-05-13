package cn.wthee.pcrtool.data.db.dao

import androidx.room.*
import cn.wthee.pcrtool.data.db.entity.PvpFavoriteData

/**
 * 竞技场收藏 DAO
 */
@Dao
interface PvpDao {

    /**
     * 根据游戏版本 [region]，获取收藏信息
     */
    @Query("SELECT * FROM pvp_like WHERE region = :region ORDER BY date DESC")
    suspend fun getAll(region: Int): List<PvpFavoriteData>

    /**
     * 根据游戏版本 [region] 进攻 [atks] 防守 [defs]，获取收藏信息
     */
    @Query("SELECT * FROM pvp_like WHERE atks = :atks AND defs = :defs AND region = :region")
    suspend fun getLiked(atks: String, defs: String, region: Int): PvpFavoriteData?

    @Query("SELECT * FROM pvp_like WHERE defs = :defs AND region = :region")
    suspend fun getLikedList(defs: String, region: Int): List<PvpFavoriteData>


    /**
     * 插入数据 [PvpFavoriteData]
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(data: PvpFavoriteData)

    /**
     * 根据游戏版本 [region] 进攻 [atks] 防守 [defs]，删除收藏信息
     */
    @Query("DELETE  FROM pvp_like WHERE atks = :atks AND defs = :defs AND region = :region")
    suspend fun delete(atks: String, defs: String, region: Int)


    /**
     * 删除数据 [PvpFavoriteData]
     */
    @Delete
    suspend fun delete(data: PvpFavoriteData)
}