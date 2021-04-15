package cn.wthee.pcrtool.data.db.dao


import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import cn.wthee.pcrtool.data.view.GachaInfo

/**
 * 卡池记录 DAO
 */
@Dao
interface GachaDao {

    /**
     * 获取所有卡池记录 [GachaInfo]
     */
    @Transaction
    @Query(
        """
        SELECT
            a.gacha_id,
            a.gacha_name,
            COALESCE( GROUP_CONCAT( b.unit_id, '-' ), "0" ) AS unit_ids,
            a.description,
            a.start_time,
            a.end_time 
        FROM
            gacha_data AS a
            LEFT JOIN gacha_exchange_lineup AS b ON a.exchange_id = b.exchange_id
        WHERE
            a.gacha_id NOT LIKE '1%' 
            AND a.gacha_id NOT LIKE '2%' 
            AND a.gacha_id < 60001 
        GROUP BY
            a.gacha_id 
        ORDER BY
            a.start_time DESC
    """
    )
    suspend fun getGachaHistory(): List<GachaInfo>
}