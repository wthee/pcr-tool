package cn.wthee.pcrtool.data.db.dao

import androidx.room.*
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
     * @param data 抽取结果
     */
    @Query("SELECT * FROM gacha_data")
    suspend fun getHistory(): List<MockGachaData>

}