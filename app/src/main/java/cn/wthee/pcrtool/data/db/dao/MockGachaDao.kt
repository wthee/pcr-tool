package cn.wthee.pcrtool.data.db.dao

import androidx.room.*
import cn.wthee.pcrtool.data.db.entity.MockGachaData
import cn.wthee.pcrtool.data.db.entity.MockGachaResultRecord
import cn.wthee.pcrtool.data.db.view.MockGachaProData

/**
 * 模拟抽卡 DAO
 */
@Dao
interface MockGachaDao {

    /**
     * 插入卡池数据
     * @param data 卡池信息
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGacha(data: MockGachaData)

    /**
     * 更新卡池日期
     * @param data 卡池信息
     */
    @Update
    suspend fun updateGacha(data: MockGachaData)

    /**
     * 插入卡池抽取结果数据
     * @param data 抽取结果
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertResult(data: MockGachaResultRecord)

    /**
     * 获取历史记录
     * @param region 区服
     */
    @Query(
        """
        SELECT
            a.*,
            COALESCE(GROUP_CONCAT( b.unitIds, '-' ), "") AS resultUnitIds,
            COALESCE(GROUP_CONCAT( b.unitRaritys, '-' ), "") AS resultUnitRaritys
        FROM
            gacha_data AS a
        LEFT JOIN gacha_result_record AS b ON a.gachaId = b.gachaId 
        WHERE region = :region
        GROUP BY a.gachaId 
        ORDER BY
            a.lastUpdateTime DESC,
            b.createTime DESC
    """
    )
    suspend fun getHistory(region: Int): List<MockGachaProData>

    /**
     * 获取卡池
     * @param gachaId 卡池编号
     */
    @Query("SELECT * FROM gacha_data WHERE gachaId = :gachaId ")
    suspend fun getGachaByGachaId(gachaId: String): MockGachaData

    /**
     * 根据 up 角色获取卡池
     * @param region 区服
     * @param pickUpIds up 角色
     */
    @Query("SELECT * FROM gacha_data WHERE region = :region AND pickUpIds = :pickUpIds ")
    suspend fun getGachaByPickUpIds(region: Int, pickUpIds: String): MockGachaData?

    /**
     * 获取抽卡记录
     * @param gachaId 卡池编号
     */
    @Query("SELECT * FROM gacha_result_record WHERE gachaId = :gachaId ORDER BY createTime DESC")
    suspend fun getResultByGachaId(gachaId: String): List<MockGachaResultRecord>

    /**
     * 删除指定卡池的所有记录
     * @param
     */
    @Query("DELETE FROM gacha_result_record WHERE gachaId = :gachaId")
    suspend fun deleteGachaResultByGachaId(gachaId: String)

    /**
     * 删除指定卡池的所有记录
     * @param
     */
    @Query("DELETE FROM gacha_data WHERE gachaId = :gachaId")
    suspend fun deleteGachaByGachaId(gachaId: String)
}