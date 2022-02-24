package cn.wthee.pcrtool.data.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import cn.wthee.pcrtool.data.db.entity.MockGachaData
import cn.wthee.pcrtool.data.db.entity.MockGachaResultRecord

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
     * 插入卡池抽取结果数据
     * @param data 抽取结果
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertResult(data: MockGachaResultRecord)

    /**
     * 获取历史记录
     * @param region 区服
     */
    @Query("SELECT * FROM gacha_data WHERE region = :region ORDER BY lastUpdateTime DESC")
    suspend fun getHistory(region: Int): List<MockGachaData>

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
}