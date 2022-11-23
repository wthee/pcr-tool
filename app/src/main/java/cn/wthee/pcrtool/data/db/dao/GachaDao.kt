package cn.wthee.pcrtool.data.db.dao


import androidx.room.Dao
import androidx.room.Query
import androidx.room.SkipQueryVerification
import androidx.room.Transaction
import cn.wthee.pcrtool.data.db.view.GachaFesUnitInfo
import cn.wthee.pcrtool.data.db.view.GachaInfo

/**
 * 卡池记录 DAO
 */
@Dao
interface GachaDao {

    /**
     * 获取所有卡池记录
     */
    @SkipQueryVerification
    @Transaction
    @Query(
        """
        SELECT
            a.gacha_id,
            a.gacha_name,
            COALESCE( GROUP_CONCAT( b.unit_id, '-' ), '' ) AS unit_ids,
            COALESCE( GROUP_CONCAT( c.unit_name, '-' ), '' ) AS unit_names,
            COALESCE( GROUP_CONCAT( c.is_limited, '-' ), '' ) AS is_limiteds,
            COALESCE( GROUP_CONCAT( b.gacha_bonus_id, '-' ), '' ) AS is_ups,
            a.description,
            a.start_time,
            a.end_time 
        FROM
            gacha_data AS a
            LEFT JOIN gacha_exchange_lineup AS b ON a.exchange_id = b.exchange_id
            LEFT JOIN unit_data as c on b.unit_id = c.unit_id
        WHERE
            a.gacha_id NOT LIKE '1%' 
            AND a.gacha_id NOT LIKE '2%' 
            AND a.gacha_id < 60001 
        GROUP BY
            a.gacha_id 
        ORDER BY
            a.start_time DESC
        LIMIT 0,:limit
    """
    )
    suspend fun getGachaHistory(limit: Int): List<GachaInfo>


    /**
     * 获取 Fes 角色编号
     */
    @SkipQueryVerification
    @Query(
        """
        SELECT
            COALESCE( GROUP_CONCAT( b.unit_id, '-' ), '' ) AS unit_ids,
            COALESCE( GROUP_CONCAT( c.unit_name, '-' ), '' ) AS unit_names
        FROM
            gacha_data AS a
            LEFT JOIN gacha_exchange_lineup AS b ON a.exchange_id = b.exchange_id
            LEFT JOIN unit_data AS c ON b.unit_id = c.unit_id 
        WHERE
            a.gacha_id LIKE '5%' 
        GROUP BY
            a.gacha_id 
        ORDER BY
            a.exchange_id DESC 
        LIMIT 0, 1
    """
    )
    suspend fun getFesUnitIds(): GachaFesUnitInfo
}