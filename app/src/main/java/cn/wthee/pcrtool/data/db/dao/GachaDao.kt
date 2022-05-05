package cn.wthee.pcrtool.data.db.dao


import androidx.room.Dao
import androidx.room.Query
import androidx.room.SkipQueryVerification
import androidx.room.Transaction
import cn.wthee.pcrtool.data.db.view.GachaFesUnitInfo
import cn.wthee.pcrtool.data.db.view.GachaInfo
import cn.wthee.pcrtool.data.db.view.GachaUnitInfo

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
            COALESCE( GROUP_CONCAT( b.unit_id, '-' ), "" ) AS unit_ids,
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
        LIMIT 0,:limit
    """
    )
    suspend fun getGachaHistory(limit: Int): List<GachaInfo>

    /**
     * 获取卡池角色
     * @param type 1、2、3: 常驻1、2、3星 ；4：限定；
     */
    @SkipQueryVerification
    @Query(
        """
        SELECT
            a.unit_id,
            b.unit_name,
            b.is_limited,
            b.rarity 
        FROM
            unit_profile AS a
            LEFT JOIN unit_data AS b ON a.unit_id = b.unit_id 
        WHERE
            ( b.is_limited = 0 OR ( b.is_limited = 1 AND b.rarity <> 1 ) ) 
            AND a.unit_id < 200000 
            AND a.unit_id IN ( SELECT MAX( unit_promotion.unit_id ) FROM unit_promotion WHERE unit_id = a.unit_id )
            AND 1 = CASE
            WHEN  1 = :type AND b.is_limited = 0 AND b.rarity = 1 THEN 1 
            WHEN  2 = :type AND b.is_limited = 0 AND b.rarity = 2 THEN 1 
            WHEN  3 = :type AND b.is_limited = 0 AND b.rarity = 3 AND a.unit_id NOT IN ${limitedIds} THEN 1 
            WHEN  4 = :type AND ((is_limited = 1 AND rarity = 3) OR a.unit_id IN ${limitedIds}) THEN 1
            END
        ORDER BY b.start_time DESC
    """
    )
    suspend fun getGachaUnits(type: Int): List<GachaUnitInfo>

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
            a.start_time DESC 
        LIMIT 0, 1
    """
    )
    suspend fun getFesUnitIds(): GachaFesUnitInfo
}