package cn.wthee.pcrtool.data.db.dao

import androidx.room.*
import cn.wthee.pcrtool.data.db.entity.PvpFavoriteData
import cn.wthee.pcrtool.data.db.entity.PvpHistoryData

/**
 * 竞技场收藏 DAO
 */
@Dao
interface PvpDao {

    /**
     * 获取收藏信息
     * @param region 区服版本
     */
    @Query("SELECT * FROM pvp_like WHERE region = :region ORDER BY date DESC")
    suspend fun getAll(region: Int): List<PvpFavoriteData>

    /**
     * 获取单个收藏信息
     * @param atks 进攻队伍成员编码
     * @param defs 防守队伍成员编码
     * @param region 区服版本
     */
    @Query("SELECT * FROM pvp_like WHERE atks = :atks AND defs = :defs AND region = :region")
    suspend fun getLiked(atks: String, defs: String, region: Int): PvpFavoriteData?

    /**
     * 获取收藏列表
     * @param defs 防守队伍成员编码
     * @param region 区服版本
     */
    @Query("SELECT * FROM pvp_like WHERE defs = :defs AND region = :region")
    suspend fun getLikedList(defs: String, region: Int): List<PvpFavoriteData>

    /**
     * 插入数据
     * @param data 对战信息
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(data: PvpFavoriteData)

    /**
     * 删除收藏信息
     * @param atks 进攻队伍成员编码
     * @param defs 防守队伍成员编码
     * @param region 区服版本
     */
    @Query("DELETE  FROM pvp_like WHERE atks = :atks AND defs = :defs AND region = :region")
    suspend fun delete(atks: String, defs: String, region: Int)

    /**
     * 删除数据
     * @param data 对战信息
     */
    @Delete
    suspend fun delete(data: PvpFavoriteData)


    /**
     * 获取搜索历史信息
     * @param region 区服版本
     */
    @Query("SELECT * FROM pvp_history WHERE defs LIKE '' || :region || '@%' ORDER BY date DESC LIMIT 0,10")
    suspend fun getHistory(region: Int): List<PvpHistoryData>

    /**
     * 插入数据
     * @param data 搜索历史信息
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(data: PvpHistoryData)

    /**
     * 根据日期获取搜索历史信息
     * @param region 区服版本
     * @param startDate 开始日期
     * @param endDate 结束日期
     */
    @Query("SELECT * FROM pvp_history WHERE defs LIKE '' || :region || '@%'AND date >= :startDate AND date <= :endDate ORDER BY date DESC")
    suspend fun getHistory(region: Int, startDate: String, endDate: String): List<PvpHistoryData>

}