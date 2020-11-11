package cn.wthee.pcrtool.data


import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import cn.wthee.pcrtool.data.view.GachaInfo

@Dao
interface GachaDao {

    //获取卡池记录
    @Transaction
    @Query(
        """
        SELECT
            a.gacha_id,
            a.gacha_name,
            COALESCE( GROUP_CONCAT( b.unit_id, '-' ), "102101" ) AS unit_ids,
            COALESCE( GROUP_CONCAT( c.unit_name, '-' ), "" ) AS unit_names,
            a.description,
            a.start_time,
            a.end_time 
        FROM
            gacha_data AS a
            LEFT JOIN gacha_exchange_lineup AS b ON a.exchange_id = b.exchange_id
            LEFT JOIN unit_data AS c ON b.unit_id = c.unit_id 
        WHERE
            a.gacha_id NOT LIKE '1%' 
            AND a.gacha_id NOT LIKE '2%' 
            AND a.gacha_id NOT LIKE '7%' 
            AND a.gacha_id <> 10001 
            AND a.gacha_id <> 60001 
        GROUP BY
            a.gacha_id 
        ORDER BY
            a.start_time DESC
    """
    )
    suspend fun getGachaHistory(): List<GachaInfo>
}